package ink.pmc.member.storage

import ink.pmc.member.UID_START
import ink.pmc.utils.concurrent.withLock
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.types.ObjectId
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.TypeName
import java.util.concurrent.locks.ReentrantLock

@Serializable
@TypeName("Status")
data class StatusBean(
    @Id @SerialName("_id") @Contextual val objectId: ObjectId,
    var lastMember: Long,
    var lastDataContainer: Long,
    var lastBedrockAccount: Long
) : Diffable<StatusBean>() {

    @Transient
    @DiffIgnore
    private val accessLock = ReentrantLock()

    override fun applyDiff(diff: Diff): Diffable<StatusBean> {
        diff.changes.filterIsInstance<ValueChange>().forEach {
            when (it.propertyName) {
                "lastMember" -> lastMember = it.right as Long
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