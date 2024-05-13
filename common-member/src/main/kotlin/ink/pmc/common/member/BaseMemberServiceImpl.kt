package ink.pmc.common.member

import com.github.benmanes.caffeine.cache.AsyncCacheLoader
import com.github.benmanes.caffeine.cache.AsyncLoadingCache
import com.github.benmanes.caffeine.cache.Caffeine
import com.mongodb.client.model.Filters.*
import com.mongodb.client.model.PushOptions
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import com.mongodb.client.model.Updates.*
import com.mongodb.kotlin.client.coroutine.MongoCollection
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.api.Member
import ink.pmc.common.member.api.WhitelistStatus
import ink.pmc.common.member.api.data.MemberModifier
import ink.pmc.common.member.data.BedrockAccountImpl
import ink.pmc.common.member.data.DataContainerImpl
import ink.pmc.common.member.proto.DiffOuterClass.DiffType
import ink.pmc.common.member.proto.MemberUpdateNotifyOuterClass.MemberUpdateNotify
import ink.pmc.common.member.proto.diff
import ink.pmc.common.member.proto.memberUpdateNotify
import ink.pmc.common.member.storage.BedrockAccountStorage
import ink.pmc.common.member.storage.DataContainerStorage
import ink.pmc.common.member.storage.MemberStorage
import ink.pmc.common.member.storage.StatusStorage
import ink.pmc.common.utils.bedrock.xuid
import ink.pmc.common.utils.concurrent.io
import ink.pmc.common.utils.concurrent.submitAsyncIO
import ink.pmc.common.utils.json.toJsonString
import ink.pmc.common.utils.json.toObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.future.asCompletableFuture
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import org.bson.BsonDocument
import org.bson.conversions.Bson
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ElementValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.diff.changetype.container.ValueRemoved
import org.javers.core.diff.changetype.map.EntryAdded
import org.javers.core.diff.changetype.map.EntryRemoved
import org.javers.core.diff.changetype.map.EntryValueChange
import org.javers.core.diff.changetype.map.MapChange
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.CompletableFuture

const val UID_START = 10000L

@Suppress("UNUSED")
abstract class BaseMemberServiceImpl(
    database: MongoDatabase
) : AbstractMemberService() {

    private var closed = false
    override val statusCollection: MongoCollection<StatusStorage> = database.getCollection("member_status")
    override val members: MongoCollection<MemberStorage> = database.getCollection("member_members")
    override val dataContainers: MongoCollection<DataContainerStorage> =
        database.getCollection("member_data_containers")
    override val bedrockAccounts: MongoCollection<BedrockAccountStorage> =
        database.getCollection("member_bedrock_accounts")
    private val cacheLoader =
        AsyncCacheLoader<Long, AbstractMember?> { key, _ -> submitAsyncIO<AbstractMember?> { loadMember(key) }.asCompletableFuture() }
    override val loadedMembers: AsyncLoadingCache<Long, AbstractMember?> = Caffeine.newBuilder()
        .refreshAfterWrite(Duration.ofMinutes(5))
        .buildAsync(cacheLoader)
    private lateinit var status: StatusStorage
    override lateinit var currentStatus: StatusStorage
    private val updateOptions = UpdateOptions().upsert(true)
    private val replaceOptions = ReplaceOptions().upsert(true)
    private val monitorJob: Job

    override suspend fun lookupBedrockAccountStorage(id: Long): BedrockAccountStorage? {
        return withContext(Dispatchers.IO) {
            bedrockAccounts.find(eq("id", id)).firstOrNull()
        }
    }

    override suspend fun lookupDataContainerStorage(id: Long): DataContainerStorage? {
        return withContext(Dispatchers.IO) {
            dataContainers.find(eq("id", id)).firstOrNull()
        }
    }

    private suspend fun loadMember(uid: Long): AbstractMember? {
        val memberStorage = members.find(eq("uid", uid)).firstOrNull() ?: return null
        return createMemberInstance(memberStorage) as AbstractMember
    }

    private suspend fun createMemberInstance(storage: MemberStorage): Member {
        val service = this
        return withContext(Dispatchers.IO) {
            MemberImpl(service, storage).apply {
                dataContainer = DataContainerImpl(this, lookupDataContainerStorage(storage.dataContainer)!!)
                if (storage.bedrockAccount != null) {
                    bedrockAccount = BedrockAccountImpl(this, lookupBedrockAccountStorage(storage.bedrockAccount!!)!!)
                }
            }
        }
    }

    override suspend fun lastUid(): Long {
        return currentStatus.lastMember
    }

    override suspend fun lastMember(): Member? {
        return lookup(currentStatus.lastMember)
    }

    override suspend fun lastMemberCreatedAt(): Instant? {
        return lastMember()?.createdAt
    }

    init {
        submitAsyncIO {
            var lookupStatus: StatusStorage?
            lookupStatus = statusCollection.find(exists("lastMember")).firstOrNull()

            if (lookupStatus == null) {
                lookupStatus = StatusStorage(ObjectId(), -1, -1, -1, -1, -1)
                statusCollection.insertOne(lookupStatus)
            }

            status = lookupStatus
            currentStatus = lookupStatus
        }

        monitorJob = submitAsyncIO { monitorUpdate() }
    }

    override fun close() {
        monitorJob.cancel()
        closed = true
    }

    override suspend fun create(name: String, authType: AuthType): Member? {
        var memberStorage = members.find(
            and(
                eq("name", name),
                eq("authType", authType.toString())
            )
        ).firstOrNull()

        if (memberStorage != null) {
            return loadedMembers.get(memberStorage.uid).await()
        }

        val profile = authType.fetcher.fetch(name) ?: return null
        val nextMember = currentStatus.nextMember()
        val nextDataContainer = currentStatus.nextDataContainer()
        val nextBedrockAccountId = currentStatus.nextBedrockAccount()

        val dataContainerStorage = DataContainerStorage(
            ObjectId(),
            nextDataContainer,
            nextMember,
            System.currentTimeMillis(),
            System.currentTimeMillis(),
            BsonDocument(),
            true
        )
        memberStorage = MemberStorage(
            ObjectId(),
            nextMember,
            profile.uuid.toString(),
            name.lowercase(),
            name,
            WhitelistStatus.WHITELISTED.toString(),
            authType.toString(),
            System.currentTimeMillis(),
            null,
            null,
            nextDataContainer,
            if (authType.isBedrock) nextBedrockAccountId else null,
            null,
            false,
            new = true
        )

        val member = MemberImpl(this, memberStorage)
        member.dataContainer = DataContainerImpl(member, dataContainerStorage)

        loadedMembers.put(nextMember, CompletableFuture.completedFuture(member))

        if (authType.isBedrock) {
            if (bedrockAccounts.find(eq("xuid", profile.uuid)).firstOrNull() != null) {
                return null
            }

            val bedrockStorage = BedrockAccountStorage(
                ObjectId(),
                nextBedrockAccountId,
                nextMember,
                profile.uuid.xuid,
                name,
                true
            )

            member.bedrockAccount = BedrockAccountImpl(member, bedrockStorage)
            currentStatus.increaseBedrockAccount()
        }

        save(member)
        currentStatus.increaseDataContainer()
        currentStatus.increaseMember()

        return member
    }

    override suspend fun lookup(uid: Long): Member? = withContext(Dispatchers.IO) {
        if (!exist(uid)) {
            return@withContext null
        }

        loadedMembers.get(uid).await()
    }

    override suspend fun lookup(uuid: UUID): Member? {
        if (!exist(uuid)) {
            return null
        }

        val storage = members.find(eq("id", uuid.toString())).firstOrNull() ?: return null
        return loadedMembers.get(storage.uid).await()
    }

    override suspend fun lookup(name: String, authType: AuthType): Member? {
        val member = members.find(
            and(
                eq("name", name.lowercase()),
                eq("authType", authType.toString())
            )
        ).firstOrNull() ?: return null
        return loadedMembers.get(member.uid).await()
    }

    override suspend fun exist(uid: Long): Boolean {
        val memberStorage = members.find(eq("uid", uid)).firstOrNull()
        return memberStorage != null
    }

    override suspend fun exist(uuid: UUID): Boolean {
        return members.find(eq("id", uuid.toString())).firstOrNull() != null
    }

    override suspend fun exist(name: String, authType: AuthType): Boolean {
        val member = members.find(
            and(
                eq("name", name.lowercase()),
                eq("authType", authType.toString())
            )
        ).firstOrNull()
        return member != null
    }

    override suspend fun existDataContainer(id: Long): Boolean {
        val dataContainerStorage = dataContainers.find(eq("id", id)).firstOrNull()
        return dataContainerStorage != null
    }

    override suspend fun existBedrockAccount(id: Long): Boolean {
        val bedrockAccountStorage = bedrockAccounts.find(eq("id", id)).firstOrNull()
        return bedrockAccountStorage != null
    }

    override suspend fun isWhitelisted(uid: Long): Boolean {
        return exist(uid) && lookup(uid)!!.isWhitelisted
    }

    override suspend fun isWhitelisted(uuid: UUID): Boolean {
        return exist(uuid) && lookup(uuid)!!.isWhitelisted
    }

    override suspend fun modifier(uid: Long, refresh: Boolean): MemberModifier? {
        if (!exist(uid)) {
            return null
        }

        val member = lookup(uid)

        if (refresh) {
            return member!!.modifier
        }

        return member!!.modifier
    }

    override suspend fun modifier(uuid: UUID, refresh: Boolean): MemberModifier? {
        if (!exist(uuid)) {
            return null
        }

        val member = lookup(uuid)

        if (refresh) {
            return member!!.modifier
        }

        return member!!.modifier
    }

    private suspend fun saveMember(old: MemberStorage?, new: MemberStorage): Diff {
        if (old == null) {
            members.insertOne(new)
            return javers.compare(null, new)
        }

        val bson = mutableListOf<Bson>()
        val diff = new.diff(old)

        if (!diff.hasChanges()) {
            println("member: no diff")
            return diff
        }

        println("member changes: ${javers.jsonConverter.toJson(diff)}")

        diff.changes.filterIsInstance<ValueChange>().forEach {
            bson.add(set(it.propertyName, it.right))
        }

        diff.changes.filterIsInstance<ListChange>().forEach { containerChange ->
            val arrayName = containerChange.propertyName

            containerChange.changes.filterIsInstance<ValueAdded>().forEach {
                bson.add(pushEach(arrayName, listOf(it.value), PushOptions().position(it.index)))
            }

            containerChange.changes.filterIsInstance<ElementValueChange>().forEach {
                bson.add(set("$arrayName.${it.index}", it.rightValue))
            }

            containerChange.changes.filterIsInstance<ValueRemoved>().forEach {
                bson.add(unset("$arrayName.${it.index}"))
                bson.add(pull(arrayName, null))
            }
        }

        val updates = combine(bson)
        println("member: updates $updates")
        members.updateOne(eq("uid", new.uid), updates, updateOptions)
        return diff
    }

    private suspend fun saveBedrockAccount(old: BedrockAccountStorage?, new: BedrockAccountStorage?): Diff {
        if (old == null && new == null) {
            return javers.compare(null, null)
        }

        if (old == null && new != null) {
            bedrockAccounts.insertOne(new)
            return javers.compare(null, new)
        }

        if (old != null && new == null) {
            bedrockAccounts.deleteOne(eq("id", old.id))
            return javers.compare(old, null)
        }

        val bson = mutableListOf<Bson>()
        val diff = new!!.diff(old)

        if (!diff.hasChanges()) {
            return diff
        }

        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "id" -> bson.add(set("id", it.right))
                "linkedWith" -> bson.add(set("linkedWith", it.right))
                "xuid" -> bson.add(set("xuid", it.right))
                "gamertag" -> bson.add(set("gamertag", it.right))
            }
        }

        val updates = combine(bson)
        bedrockAccounts.updateOne(eq("id", new.id), updates, updateOptions)
        return diff
    }

    private suspend fun saveDataContainer(old: DataContainerStorage?, new: DataContainerStorage): Diff {
        if (old == null) {
            dataContainers.insertOne(new)
            return javers.compare(null, new)
        }

        val bson = mutableListOf<Bson>()
        val diff = new.diff(old)

        if (!diff.hasChanges()) {
            println("dc: no diff")
            return diff
        }

        println("dc changes: ${javers.jsonConverter.toJson(diff)}")

        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "id" -> bson.add(set("id", it.right))
                "owner" -> bson.add(set("owner", it.right))
                "createdAt" -> bson.add(set("createdAt", it.right))
                "lastModifiedAt" -> bson.add(set("lastModifiedAt", it.right))
            }
        }

        val mapChanged = diff.changes.filterIsInstance<MapChange<*>>().isNotEmpty()

        if (mapChanged) {
            bson.add(set("contents", new.contents))
        }

        val updates = combine(bson)
        println("dc: updates $updates")
        dataContainers.updateOne(eq("id", new.id), updates, updateOptions)
        return diff
    }

    private suspend fun saveStatus(old: StatusStorage, new: StatusStorage): Diff {
        val bson = mutableListOf<Bson>()
        val diff = new.diff(old)

        if (!diff.hasChanges()) {
            return diff
        }

        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "lastMember" -> bson.add(set("lastMember", it.right))
                "lastPunishment" -> bson.add(set("lastPunishment", it.right))
                "lastComment" -> bson.add(set("lastComment", it.right))
                "lastDataContainer" -> bson.add(set("lastDataContainer", it.right))
                "lastBedrockAccount" -> bson.add(set("lastBedrockAccount", it.right))
            }
        }

        val updates = combine(bson)
        dataContainers.updateOne(exists("lastMember"), updates, updateOptions)
        status = currentStatus
        return diff
    }

    abstract suspend fun notifyUpdate(notify: MemberUpdateNotify)

    abstract suspend fun monitorUpdate()

    suspend fun handleUpdate(notify: MemberUpdateNotify) {
        withContext(Dispatchers.IO) {
            if (UUID.fromString(notify.serviceId) == id) {
                return@withContext
            }

            serverLogger.info("Received update notify from server (serviceId=${notify.serviceId}) for UID ${notify.memberId}, processing...")

            val memberId = notify.memberId

            if (loadedMembers.getIfPresent(memberId) == null) {
                return@withContext
            }

            val member = loadedMembers.get(memberId).get()!! as MemberImpl

            if (notify.hasMemberDiff()) {
                val memberDiff = notify.memberDiff
                when (memberDiff.type) {
                    DiffType.MODIFY -> {
                        println("update: member diff: ${memberDiff.diff}")
                        val diff = memberDiff.diff.toDiff()!!
                        val diffedMemberStorage = member.storage.copy().applyDiff(diff)
                        member.reload(diffedMemberStorage as MemberStorage)
                    }

                    else -> {}
                }
            }

            if (notify.hasStatusDiff()) {
                val statusDiff = notify.statusDiff
                val diff = statusDiff.diff.toDiff()!!
                currentStatus.applyDiff(diff)
                status = currentStatus
            }

            if (notify.hasBedrockAccountDiff()) {
                val bedrockAccountDiff = notify.bedrockAccountDiff
                when (bedrockAccountDiff.type) {
                    DiffType.ADD -> {
                        val storage = bedrockAccountDiff.storage.toObject(BedrockAccountStorage::class.java)
                        member.bedrockAccount = BedrockAccountImpl(member, storage)
                    }

                    DiffType.REMOVE -> {
                        member.bedrockAccount = null
                    }

                    DiffType.MODIFY -> {
                        println("update: ba diff: ${bedrockAccountDiff.diff}")
                        val diff = bedrockAccountDiff.diff.toDiff()!!
                        val diffedBedrockAccountStorage = member.bedrockAccount!!.storage.copy().applyDiff(diff)
                        member.bedrockAccount!!.reload(diffedBedrockAccountStorage as BedrockAccountStorage)
                    }

                    else -> {}
                }
            }

            if (notify.hasDataContainerDiff()) {
                val dataContainerDiff = notify.dataContainerDiff
                when (dataContainerDiff.type) {
                    DiffType.MODIFY -> {
                        println("update: dc diff: ${dataContainerDiff.diff}")
                        val diff = dataContainerDiff.diff.toDiff()!!
                        val diffedDataContainerStorage = member.dataContainer.storage.copy().applyDiff(diff)
                        member.dataContainer.reload(diffedDataContainerStorage as DataContainerStorage)
                    }

                    else -> {}
                }
            }
        }

        serverLogger.info("Notify processed for UID ${notify.memberId}")
    }

    override suspend fun save(member: Member) {
        if (member !is AbstractMember) {
            return
        }

        io {
            val modifiedMemberStorage = member.toStorage()
            println("new member: $modifiedMemberStorage")
            val modifiedBedrockAccountStorage = member.bedrockAccount?.toStorage()
            println("new ba: $modifiedBedrockAccountStorage")
            val modifiedDataContainerStorage = member.dataContainer.toStorage()
            println("new dc: $modifiedDataContainerStorage")

            val oldMemberStorage = if (member.storage.new) null else member.storage
            println("old member: $oldMemberStorage")
            val oldBedrockAccountStorage =
                if (member.bedrockAccount?.storage?.new == true) null else member.bedrockAccount?.storage
            println("old ba: $oldBedrockAccountStorage")
            val oldDataContainerStorage = if (member.dataContainer.storage.new) null else member.dataContainer.storage
            println("old dc: $oldDataContainerStorage")

            val diffStatus = saveStatus(status, currentStatus)
            val diffMember = saveMember(oldMemberStorage, modifiedMemberStorage)
            val diffBedrockAccount = saveBedrockAccount(oldBedrockAccountStorage, modifiedBedrockAccountStorage)
            val diffDataContainer = saveDataContainer(oldDataContainerStorage, modifiedDataContainerStorage)

            val notify = memberUpdateNotify {
                serviceId = id.toString()
                memberId = member.uid

                if (diffStatus.hasChanges()) {
                    statusDiff = diff {
                        type = DiffType.MODIFY
                        diff = diffStatus.toJson()
                    }
                }

                if (diffMember.hasChanges()) {
                    memberDiff = diff {
                        type = if (oldMemberStorage == null) {
                            storage = modifiedMemberStorage.toJsonString()
                            DiffType.ADD
                        } else {
                            diff = diffMember.toJson()
                            DiffType.MODIFY
                        }
                    }
                }

                if (diffBedrockAccount.hasChanges()) {
                    bedrockAccountDiff = diff {
                        type = if (oldBedrockAccountStorage == null && modifiedBedrockAccountStorage != null) {
                            storage = modifiedBedrockAccountStorage.toJsonString()
                            DiffType.ADD
                        } else if (modifiedBedrockAccountStorage == null) {
                            DiffType.REMOVE
                        } else {
                            diff = diffBedrockAccount.toJson()
                            DiffType.MODIFY
                        }
                    }
                }

                if (diffDataContainer.hasChanges()) {
                    dataContainerDiff = diff {
                        type = if (oldDataContainerStorage == null) {
                            storage = modifiedDataContainerStorage.toJsonString()
                            DiffType.ADD
                        } else {
                            diff = diffDataContainer.toJson()
                            DiffType.MODIFY
                        }
                    }
                }
            }

            member.storage = modifiedMemberStorage
            if (member.bedrockAccount != null && modifiedBedrockAccountStorage != null) {
                member.bedrockAccount!!.storage = modifiedBedrockAccountStorage
            }
            member.dataContainer.storage = modifiedDataContainerStorage

            println("notify")
            notifyUpdate(notify)
        }
    }

    override suspend fun save(uid: Long) {
        loadedMembers.asMap().values.firstOrNull { it.await()?.uid == uid } ?: return
        save(loadedMembers.get(uid).await()!!)
    }

    override suspend fun save(uuid: UUID) {
        val member = loadedMembers.asMap().values.firstOrNull { it.await()?.id == uuid } ?: return
        save(member.await()!!)
    }

    override suspend fun sync(member: Member): Member? {
        if (!exist(member.uid)) {
            return null
        }

        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(member.uid)
        return lookup(member.uid)
    }

    override suspend fun sync(uid: Long): Member? {
        if (!exist(uid)) {
            return null
        }

        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(uid)
        return lookup(uid)
    }

    override suspend fun sync(uuid: UUID): Member? {
        if (!exist(uuid)) {
            return null
        }

        val member = lookup(uuid)
        currentStatus = statusCollection.find(exists("lastMember")).firstOrNull()!!
        loadedMembers.synchronous().invalidate(member!!.uid)
        return lookup(uuid)
    }

}