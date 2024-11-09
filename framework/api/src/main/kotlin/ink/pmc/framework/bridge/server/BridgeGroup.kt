package ink.pmc.framework.bridge.server

import ink.pmc.framework.bridge.player.PlayerLookup

interface BridgeGroup : PlayerLookup, ServerLookup {
    val id: String
}