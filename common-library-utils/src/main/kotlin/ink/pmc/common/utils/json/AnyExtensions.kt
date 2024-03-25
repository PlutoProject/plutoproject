package ink.pmc.common.utils.json

import com.google.gson.JsonObject

fun Any.toJsonObject(): JsonObject {
    return gson.toJsonTree(this).asJsonObject
}

fun Any.toJsonString(): String {
    return gson.toJson(this)
}