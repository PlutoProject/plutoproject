package ink.pmc.common.server.request

import ink.pmc.common.server.message.Message

interface Request : Message {

    val name: String
    val parameters: Map<String, String>

}