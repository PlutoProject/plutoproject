package ink.pmc.framework.json

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject

fun Any.toJsonObject(gsonInstance: Gson = gson): JsonObject {
    return gsonInstance.toJsonTree(this).asJsonObject
}

fun Any.toJsonElement(gsonInstance: Gson = gson): JsonElement {
    return gsonInstance.toJsonTree(this)
}

fun Any.toJsonString(gsonInstance: Gson = gson): String {
    return gsonInstance.toJson(this)
}