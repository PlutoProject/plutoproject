package ink.pmc.common.server.request

import org.apache.logging.log4j.message.Message

interface Request : Message {

    val parameters: Map<String, Any>

}