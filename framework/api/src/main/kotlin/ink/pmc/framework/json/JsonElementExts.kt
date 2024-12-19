package ink.pmc.framework.json

import com.google.gson.Gson
import com.google.gson.JsonElement

inline fun <reified T> JsonElement.toObject(gsonInstance: Gson = gson): T {
    return gsonInstance.fromJson(this, T::class.java)
}