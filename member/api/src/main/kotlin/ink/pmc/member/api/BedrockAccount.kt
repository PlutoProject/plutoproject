package ink.pmc.member.api

@Suppress("UNUSED")
interface BedrockAccount {

    val id: Long
    val linkedWith: Member
    val xuid: String
    val gamertag: String
}