package ink.pmc.common.member.storage

import ink.pmc.common.member.UID_START
import ink.pmc.common.utils.concurrent.withLock
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.DiffIgnore
import java.util.concurrent.locks.ReentrantLock

data class StatusStorage(
    @BsonId @DiffIgnore val objectId: ObjectId,
    var lastMember: Long,
    var lastPunishment: Long,
    var lastComment: Long,
    var lastDataContainer: Long,
    var lastBedrockAccount: Long
) : Diffable<StatusStorage>() {

    @DiffIgnore private val accessLock = ReentrantLock()

    override fun applyDiff(diff: Diff): Diffable<StatusStorage> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "lastMember" -> lastMember = it.right as Long
                "lastPunishment" -> lastPunishment = it.right as Long
                "lastComment" -> lastComment = it.right as Long
                "lastDataContainer" -> lastDataContainer = it.right as Long
                "lastBedrockAccount" -> lastBedrockAccount = it.right as Long
            }
        }

        return this
    }

    fun nextMember(): Long {
        if (lastMember == -1L) {
            return UID_START
        }

        return lastMember + 1
    }

    fun increaseMember() {
        accessLock.withLock {
            if (lastMember == -1L) {
                lastMember = UID_START
                return@withLock
            }

            lastMember += 1
        }
    }

    fun nextPunishment(): Long {
        if (lastPunishment == -1L) {
            return 0
        }

        return lastPunishment + 1
    }

    fun increasePunishment() {
        accessLock.withLock {
            lastPunishment += 1
        }
    }

    fun nextComment(): Long {
        if (lastComment == -1L) {
            return 0
        }

        return lastComment + 1
    }

    fun increaseComment() {
        accessLock.withLock {
            lastComment += 1
        }
    }

    fun nextDataContainer(): Long {
        if (lastDataContainer == -1L) {
            return 0
        }

        return lastDataContainer + 1
    }

    fun increaseDataContainer() {
        accessLock.withLock {
            lastDataContainer += 1
        }
    }

    fun nextBedrockAccount(): Long {
        if (lastBedrockAccount == -1L) {
            return 0
        }

        return lastBedrockAccount + 1
    }

    fun increaseBedrockAccount() {
        accessLock.withLock {
            lastBedrockAccount += 1
        }
    }

}