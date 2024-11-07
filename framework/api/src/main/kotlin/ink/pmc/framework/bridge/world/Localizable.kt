package ink.pmc.framework.bridge.world

import ink.pmc.framework.bridge.server.ServerElement

interface Localizable : ServerElement {
    val location: BridgeLocation
}