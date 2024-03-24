package ink.pmc.common.server.request

import ink.pmc.common.server.message.Reply

interface Response : Reply {

    val values: Map<String, Any>

}