package ink.pmc.common.server

import ink.pmc.common.server.impl.message.MessageImpl
import ink.pmc.common.server.impl.message.ReplyImpl
import ink.pmc.common.server.impl.request.RequestImpl
import ink.pmc.common.server.impl.request.ResponseImpl
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Suppress("UNUSED")
fun encodeMsg(message: MessageImpl): String {
    return encrypt(Json.encodeToString(message))
}

@Suppress("UNUSED")
fun decodeMsg(message: String): MessageImpl? {
    val decrypted = decrypt(message) ?: return null

    try {
        val decoded = Json.decodeFromString<MessageImpl>(decrypted)
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeReply(reply: ReplyImpl): String {
    return encrypt(Json.encodeToString(reply))
}

@Suppress("UNUSED")
fun decodeReply(reply: String): ReplyImpl? {
    val decrypted = decrypt(reply) ?: return null

    try {
        val decoded = Json.decodeFromString<ReplyImpl>(decrypted)
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeReq(req: RequestImpl): String {
    return encrypt(Json.encodeToString(req))
}

@Suppress("UNUSED")
fun decodeReq(req: String): RequestImpl? {
    val decrypted = decrypt(req) ?: return null

    try {
        val decoded = Json.decodeFromString<RequestImpl>(decrypted)
        return decoded
    } catch (e: Exception) {
        return null
    }
}

@Suppress("UNUSED")
fun encodeRsp(rsp: ResponseImpl): String {
    return encrypt(Json.encodeToString(rsp))
}

@Suppress("UNUSED")
fun decodeRsp(rsp: String): ResponseImpl? {
    val decrypted = decrypt(rsp) ?: return null

    try {
        val decoded = Json.decodeFromString<ResponseImpl>(decrypted)
        return decoded
    } catch (e: Exception) {
        return null
    }
}