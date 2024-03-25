package ink.pmc.common.server

import com.google.gson.JsonParser
import ink.pmc.common.server.impl.message.MessageImpl
import ink.pmc.common.server.impl.message.ReplyImpl
import ink.pmc.common.server.impl.request.RequestImpl
import ink.pmc.common.server.impl.request.ResponseImpl
import ink.pmc.common.server.message.Message
import ink.pmc.common.server.message.Reply
import ink.pmc.common.server.request.Request
import ink.pmc.common.server.request.Response
import ink.pmc.common.utils.json.gson
import ink.pmc.common.utils.json.toJsonObject
import ink.pmc.common.utils.json.toObject

@Suppress("UNUSED")
fun encodeMsg(message: Message): String {
    return encrypt(gson.toJson(message))
}

@Suppress("UNUSED")
fun decodeMsg(message: String): Message? {
    val decrypted = decrypt(message) ?: return null

    try {
        val decoded = decrypted.toObject<MessageImpl>()
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeReply(reply: ReplyImpl): String {
    return encrypt(gson.toJson(reply))
}

@Suppress("UNUSED")
fun decodeReply(reply: String): Reply? {
    val decrypted = decrypt(reply) ?: return null

    try {
        val decoded = decrypted.toObject<ReplyImpl>()
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeReq(req: RequestImpl): String {
    return encrypt(gson.toJson(req))
}

@Suppress("UNUSED")
fun decodeReq(req: String): Request? {
    val decrypted = decrypt(req) ?: return null

    try {
        val decoded = decrypted.toObject<RequestImpl>()
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeRsp(rsp: ResponseImpl): String {
    return encrypt(gson.toJson(rsp))
}

@Suppress("UNUSED")
fun decodeRsp(rsp: String): Response? {
    val decrypted = decrypt(rsp) ?: return null

    try {
        val decoded = decrypted.toObject<ResponseImpl>()
        return decoded
    } catch (e: Exception) {
        return null
    }
}

fun isJsonString(str: String): Boolean {
    try {
        JsonParser.parseString(str)
        return true
    } catch (e: Exception) {
        return false
    }
}

fun isMessage(str: String): Boolean {
    if (!isJsonString(str)) {
        return false
    }

    val jsonObj = str.toJsonObject()

    return jsonObj.has("content") && jsonObj["content"].asString != ""
}

fun isReply(str: String): Boolean {
    if (!isJsonString(str)) {
        return false
    }

    val jsonObj = str.toJsonObject()

    return jsonObj.has("target") && !jsonObj.has("values")
}

fun isRequest(str: String): Boolean {
    if (!isJsonString(str)) {
        return false
    }

    val jsonObj = str.toJsonObject()

    return jsonObj.has("parameters")
}

fun isResponse(str: String): Boolean {
    if (!isJsonString(str)) {
        return false
    }

    val jsonObj = str.toJsonObject()

    return jsonObj.has("values")
}