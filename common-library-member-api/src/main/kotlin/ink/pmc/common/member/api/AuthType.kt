package ink.pmc.common.member.api

import ink.pmc.common.member.api.fetcher.*
import net.kyori.adventure.text.Component

@Suppress("UNUSED")
enum class AuthType(val display: Component, val fetcher: ProfileFetcher) {

    OFFICIAL(OFFICIAL_AUTH, OfficialProfileFetcher),
    LITTLESKIN(LITTLESKIN_AUTH, LittleSkinProfileFetcher),
    BEDROCK_ONLY(BEDROCK_ONLY_AUTH, BedrockProfileFetcher),
    NONE(NONE_AUTH, EmptyProfileFetcher);

    val isOfficial
        get() = this == OFFICIAL

    val isBedrock
        get() = this == BEDROCK_ONLY

    val hasAuth
        get() = this != NONE

}