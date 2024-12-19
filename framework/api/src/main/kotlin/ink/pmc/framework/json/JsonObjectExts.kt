package ink.pmc.framework.json

import com.google.gson.Gson
import com.google.gson.JsonObject

inline fun <reified T> JsonObject.toObject(gsonInstance: Gson = gson): T {
    return gsonInstance.fromJson(this, T::class.java)
}