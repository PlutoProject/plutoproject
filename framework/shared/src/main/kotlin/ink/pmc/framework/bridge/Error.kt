package ink.pmc.framework.bridge

fun serverNotFound(id: String): Nothing {
    error("Server not found: $id")
}

fun remoteServerNotFound(id: String): Nothing {
    error("Remote server not found: $id")
}

fun remoteServerOffline(id: String): Nothing {
    error("Remote server offline: $id")
}

fun playerNotFound(name: String) {
    error("Player not found: $name")
}

fun remotePlayerNotFound(name: String): Nothing {
    error("Remote player not found: $name")
}

fun remotePlayerOffline(name: String): Nothing {
    error("Remote player offline: $name")
}

fun localPlayerNotFound(name: String): Nothing {
    error("Local player not found: $name")
}

fun worldNotFound(name: String, server: String?): Nothing {
    if (server != null) {
        error("World not found: $name (server: $server)")
    } else {
        error("World not found: $name")
    }
}

fun remoteWorldNotFound(name: String, server: String?): Nothing {
    if (server != null) {
        error("Remote world not found: $name (server: $server)")
    } else {
        error("Remote world not found: $name")
    }
}

fun localWorldNotFound(name: String): Nothing {
    error("Local world not found: $name")
}