package ink.pmc.framework.bridge

import ink.pmc.framework.bridge.proto.BridgeRpcOuterClass.*

fun serverNotFound(id: String): Nothing {
    error("Server not found: $id")
}

fun remoteServerNotFound(id: String): Nothing {
    error("Remote server not found: $id")
}

fun remoteServerOffline(id: String): Nothing {
    error("Remote server offline: $id")
}

fun playerNotFound(name: String): Nothing {
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

fun worldNotFound(): Nothing {
    error("World not found")
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

fun playerOperationTimeout(player: String): Nothing {
    error("Player operation timeout: $player")
}

fun missingFields(): Nothing {
    error("Missing fields")
}

fun statusNotSet(name: String): Nothing {
    error("Received a $name without status")
}

fun contentNotSet(name: String): Nothing {
    error("Received a $name without content")
}

fun checkCommonResult(result: CommonResult) {
    when (result.statusCase!!) {
        CommonResult.StatusCase.OK -> {}
        CommonResult.StatusCase.MISSING_FIELDS -> missingFields()
        CommonResult.StatusCase.STATUS_NOT_SET -> statusNotSet("CommonResult")
    }
}

fun checkPlayerOperationResult(request: PlayerOperation, result: PlayerOperationResult) {
    when (result.statusCase!!) {
        PlayerOperationResult.StatusCase.OK -> {}
        PlayerOperationResult.StatusCase.PLAYER_OFFLINE -> playerNotFound(request.playerUuid)
        PlayerOperationResult.StatusCase.SERVER_OFFLINE -> serverNotFound(request.executor)
        PlayerOperationResult.StatusCase.WORLD_NOT_FOUND -> worldNotFound()
        PlayerOperationResult.StatusCase.TIMEOUT -> playerOperationTimeout(request.playerUuid)
        PlayerOperationResult.StatusCase.UNSUPPORTED -> error("Unsupported")
        PlayerOperationResult.StatusCase.MISSING_FIELDS -> missingFields()
        PlayerOperationResult.StatusCase.STATUS_NOT_SET -> statusNotSet("PlayerOperationResult")
    }
}