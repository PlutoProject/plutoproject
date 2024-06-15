package ink.pmc.member.storage

import ink.pmc.member.UID_START
import ink.pmc.utils.concurrent.withLock
import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.bson.types.ObjectId
import java.util.concurrent.locks.ReentrantLock

@Serializable
data class StatusBean(
    @SerialName("_id") @Contextual val objectId: ObjectId,
    var lastMember: Long,
    var lastDataContainer: Long,
    var lastBedrockAccount: Long
) {

    @Transient
    private val accessLock = ReentrantLock()

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