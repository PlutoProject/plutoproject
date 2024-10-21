package ink.pmc.hypervisor

import ink.pmc.utils.inject.inlinedGet

interface Hypervisor {
    companion object : Hypervisor by inlinedGet()
}