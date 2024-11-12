package ink.pmc.framework.bridge.server

enum class ServerType {
    PROXY, BACKEND;

    val isProxy: Boolean
        get() = this == PROXY
    val isBackend: Boolean
        get() = this == BACKEND
}