package ink.pmc.common.server.request

interface HeartbeatRequest : Request {

    val time: Long

}