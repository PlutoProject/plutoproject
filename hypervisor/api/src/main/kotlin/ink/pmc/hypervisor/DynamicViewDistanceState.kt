package ink.pmc.hypervisor

enum class DynamicViewDistanceState {
    ENABLED, DISABLED, DISABLED_DUE_PING, DISABLED_DUE_VHOST;

    val isDisabledLocally: Boolean
        get() = when (this) {
            ENABLED -> false
            DISABLED -> false
            DISABLED_DUE_PING -> true
            DISABLED_DUE_VHOST -> true
        }
}