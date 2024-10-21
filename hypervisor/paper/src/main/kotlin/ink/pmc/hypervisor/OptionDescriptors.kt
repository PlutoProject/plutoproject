package ink.pmc.hypervisor

import ink.pmc.options.api.EntryValueType
import ink.pmc.options.api.dsl.descriptor

val DYNAMIC_VIEW_DISTANCE = descriptor<Boolean> {
    key = "hypervisor.dynamic_view_distance"
    type = EntryValueType.BOOLEAN
    defaultValue = false
}