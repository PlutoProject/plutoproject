package ink.pmc.transfer

import ink.pmc.transfer.api.Category
import ink.pmc.transfer.api.Destination
import ink.pmc.transfer.api.DestinationStatus
import ink.pmc.transfer.proto.ProtoCategoryOuterClass.ProtoCategory
import ink.pmc.transfer.proto.ProtoDestinationOuterClass.ProtoDestination
import ink.pmc.transfer.proto.ProtoDestinationStatusOuterClass.ProtoDestinationStatus
import ink.pmc.transfer.proto.protoCategory
import ink.pmc.transfer.proto.protoDestination
import ink.pmc.utils.chat.json

val Destination.proto: ProtoDestination
    get() {
        return protoDestination {
            id = this@proto.id
            icon = this@proto.icon.namespacedKey
            name = this@proto.name.json
            description.addAll(this@proto.description.map { it.json })
            status = this@proto.status.proto
            playerCount = this@proto.playerCount
            maxPlayerCount = this@proto.maxPlayerCount
            isHidden = this@proto.isHidden
            this@proto.category?.let {
                category = id
            }
        }
    }

val Category.proto: ProtoCategory
    get() {
        return protoCategory {
            id = this@proto.id
            name = this@proto.name.json
            description.addAll(this@proto.description.map { it.json })
            playerCount = this@proto.playerCount
            destinations.addAll(this@proto.destinations.map { it.id })
        }
    }

val DestinationStatus.proto: ProtoDestinationStatus
    get() {
        return when (this) {
            DestinationStatus.ONLINE -> ProtoDestinationStatus.STATUS_ONLINE
            DestinationStatus.OFFLINE -> ProtoDestinationStatus.STATUS_OFFLINE
            DestinationStatus.MAINTENANCE -> ProtoDestinationStatus.STATUS_MAINTAINACE
        }
    }

val ProtoDestinationStatus.original: DestinationStatus
    get() {
        return when (this) {
            ProtoDestinationStatus.STATUS_ONLINE -> DestinationStatus.ONLINE
            ProtoDestinationStatus.STATUS_OFFLINE -> DestinationStatus.OFFLINE
            ProtoDestinationStatus.STATUS_MAINTAINACE -> DestinationStatus.MAINTENANCE
            else -> DestinationStatus.OFFLINE
        }
    }