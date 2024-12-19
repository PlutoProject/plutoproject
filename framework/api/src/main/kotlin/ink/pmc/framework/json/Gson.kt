package ink.pmc.framework.json

import com.google.gson.Gson
import com.google.gson.GsonBuilder

private var internalGson = Gson()

val gson: Gson
    get() = internalGson

fun transformGson(block: GsonBuilder.() -> Unit) {
    val builder = internalGson.newBuilder()
    block.invoke(builder)
    internalGson = builder.create()
}