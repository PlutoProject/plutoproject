package ink.pmc.essentials.manager

import ink.pmc.essentials.*
import ink.pmc.essentials.api.teleport.*
import ink.pmc.essentials.api.teleport.TeleportDirection.COME
import ink.pmc.essentials.api.teleport.TeleportDirection.GO
import ink.pmc.utils.chat.replace
import org.bukkit.entity.Player
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.*

class TeleportRequestImpl(
    override val options: RequestOptions,
    override val source: Player,
    override val destination: Player,
    override val direction: TeleportDirection
) : TeleportRequest, KoinComponent {

    private val manager by inject<TeleportManager>()

    override val id: UUID = UUID.randomUUID()
    override val createdAt: Instant = Instant.now()
    override var status: RequestStatus = RequestStatus.WAITING
    override val isFinished: Boolean
        get() = status != RequestStatus.WAITING

    override fun accept(prompt: Boolean) {
        if (isFinished) {
            return
        }

        status = RequestStatus.ACCEPTED

        when (direction) {
            GO -> manager.teleport(source, destination, prompt = prompt)
            COME -> manager.teleport(destination, source, prompt = prompt)
        }

        if (!prompt) {
            return
        }

        source.sendMessage(
            TELEPORT_REQUEST_ACCEPTED_SOURCE
                .replace("<player>", destination.name)
        )
    }

    override fun deny(prompt: Boolean) {
        if (isFinished) {
            return
        }

        status = RequestStatus.DENYED

        if (!prompt) {
            return
        }

        source.sendMessage(
            TELEPORT_REQUEST_DENIED_SOURCE
                .replace("<player>", destination.name)
        )
        source.playSound(TELEPORT_REQUEST_DENIED_SOUND)
    }

    override fun expire(prompt: Boolean) {
        if (isFinished) {
            return
        }

        status = RequestStatus.EXPIRED

        if (!prompt) {
            return
        }

        source.sendMessage(
            TELEPORT_REQUEST_EXPIRED_SOURCE
                .replace("<player>", destination.name)
        )
        source.playSound(TELEPORT_REQUEST_CANCELLED_SOUND)
    }

    override fun cancel(prompt: Boolean) {
        if (isFinished) {
            return
        }

        status = RequestStatus.CANCELED

        if (!prompt) {
            return
        }

        destination.sendMessage(
            TELEPORT_REQUEST_CANCELED
                .replace("<player>", source.name)
        )
        destination.playSound(TELEPORT_REQUEST_CANCELLED_SOUND)
    }

}