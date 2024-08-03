package ink.pmc.member.commands

import com.mongodb.client.model.Filters.eq
import com.velocitypowered.api.command.CommandSource
import ink.pmc.member.*
import ink.pmc.member.api.AuthType
import ink.pmc.utils.bedrock.disconnect
import ink.pmc.utils.bedrock.isFloodgatePlayer
import ink.pmc.utils.bedrock.xuid
import ink.pmc.utils.chat.replace
import ink.pmc.utils.command.VelocityCommand
import ink.pmc.utils.command.velocityRequiredOnlinePlayersArgument
import ink.pmc.utils.platform.proxy
import ink.pmc.utils.visual.mochaYellow
import kotlinx.coroutines.flow.firstOrNull
import net.kyori.adventure.text.Component
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.component.DefaultValue
import org.incendo.cloud.kotlin.coroutines.extension.suspendingHandler
import org.incendo.cloud.parser.flag.FlagContext
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.CompletableFuture

object MemberCommand : VelocityCommand() {

    private val authTypeArg = CommandComponent.builder<CommandSource, String>()
        .suggestionProvider { _, _ ->
            CompletableFuture.completedFuture(
                listOf(
                    Suggestion.suggestion("OFFICIAL"),
                    Suggestion.suggestion("LITTLESKIN"),
                    Suggestion.suggestion("BEDROCK_ONLY")
                )
            )
        }
        .parser(StringParser.stringParser())
        .defaultValue(DefaultValue.constant("OFFICIAL"))
        .name("authType")
        .optional()

    private val memberCreate = commandManager.commandBuilder("member")
        .permission("member.create")
        .literal("create")
        .required("name", StringParser.stringParser())
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            sender.sendMessage(MEMBER_FETCH)
            val profile = authType.fetcher.fetch(name)

            if (profile == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED)
                return@suspendingHandler
            }

            if (authType.isBedrock) {
                val xuid = profile.uuid.xuid
                val beStorage = memberService.bedrockAccounts.find(eq("xuid", xuid)).firstOrNull()

                if (beStorage != null) {
                    val linkedMember = memberService.lookup(beStorage.linkedWith)!!
                    sender.sendMessage(
                        MEMBER_CREATE_BE_ALREADY_EXISTED
                            .replace("<player>", profile.name)
                            .replace("<gamertag>", Component.text(beStorage.gamertag))
                            .replace("<xuid>", Component.text(beStorage.xuid))
                            .replace("<other>", Component.text(linkedMember.rawName))
                    )
                    return@suspendingHandler
                }
            }

            if (memberService.exist(profile.uuid)) {
                sender.sendMessage(MEMBER_CREATE_ALREADY_EXIST)
                return@suspendingHandler
            }

            memberService.create(profile.name, authType)
            sender.sendMessage(
                MEMBER_CREATE_SUCCEED.replace(
                    "<player>", Component.text(profile.name)
                )
            )
        }

    private val memberModifyExemptWhitelist = commandManager.commandBuilder("member")
        .permission("member.modify.exemptwhitelist")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("exemptwhitelist")
        .flag(commandManager.flagBuilder("hide").withAliases("h"))
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (!member.isWhitelisted) {
                sender.sendMessage(
                    MEMBER_MODIFY_EXEMPT_WHITELIST_FAILED_NOT_WHITELISTED
                        .replace("<player>", Component.text(member.rawName))
                )
                return@suspendingHandler
            }

            if (it.flags().isPresent("hide") && !member.isHidden) {
                member.modifier.hide(true)
                sender.sendMessage(
                    MEMBER_MODIFY_HIDE_SUCCEED
                        .replace("<player>", Component.text(member.rawName))
                )
            }

            member.exemptWhitelist()
            member.save()

            val player = proxy.getPlayer(member.id)

            if (player.isPresent) {
                player.get().disconnect(MEMBER_NOT_WHITELISTED, MEMBER_NOT_WHITELISTED_BE)
            }

            sender.sendMessage(
                MEMBER_MODIFY_EXEMPT_WHITELIST_SUCCEED
                    .replace(
                        "<player>", Component.text(member.rawName)
                    )
            )
        }

    private val memberModifyGrantWhitelist = commandManager.commandBuilder("member")
        .permission("member.modify.grantwhitelist")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("grantwhitelist")
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (member.isWhitelisted) {
                sender.sendMessage(
                    MEMBER_MODIFY_GRANT_WHITELIST_FAILED_ALREADY_WHITELISTED
                        .replace(
                            "<player>", Component.text(member.rawName).color(mochaYellow)
                        )
                )
                return@suspendingHandler
            }

            member.grantWhitelist()
            member.save()

            sender.sendMessage(
                MEMBER_MODIFY_GRANT_WHITELIST_SUCCEED
                    .replace(
                        "<player>", Component.text(member.rawName)
                    )
            )
        }

    private val memberModifyLinkBeAccount = commandManager.commandBuilder("member")
        .permission("member.modify.linkbedrock")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("linkbedrock")
        .required("gamertag", StringParser.stringParser())
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .flag(commandManager.flagBuilder("force").withAliases("f"))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())
            val gamertag = it.get<String>("gamertag")
            val force = it.flags().isPresent("force")

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            if (authType.isBedrock && !force) {
                sender.sendMessage(MEMBER_MODIFY_LINK_BE_FAILED_BE_ONLY)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (member.bedrockAccount != null) {
                sender.sendMessage(MEMBER_MODIFY_LINK_BE_FAILED_ALREADY_LINKED)
                return@suspendingHandler
            }

            sender.sendMessage(MEMBER_FETCH)
            val xuid = AuthType.BEDROCK_ONLY.fetcher.fetch(gamertag)?.uuid?.xuid

            if (xuid == null) {
                sender.sendMessage(MEMBER_MODIFY_LINK_BE_FAILED_NOT_EXISTED)
                return@suspendingHandler
            }

            val beStorage = memberService.bedrockAccounts.find(eq("xuid", xuid)).firstOrNull()

            if (beStorage != null) {
                val linkedMember = memberService.lookup(beStorage.linkedWith)!!
                sender.sendMessage(
                    MEMBER_MODIFY_LINK_BE_FAILED_ACCOUNT_ALREADY_EXISTED
                        .replace("<gamertag>", Component.text(beStorage.gamertag).color(mochaYellow))
                        .replace("<xuid>", Component.text(beStorage.xuid).color(mochaYellow))
                        .replace("<other>", Component.text(linkedMember.rawName).color(mochaYellow))
                )
                return@suspendingHandler
            }

            member.linkBedrock(xuid, gamertag)
            member.save()

            sender.sendMessage(
                MEMBER_MODIFY_LINK_BE_SUCCEED
                    .replace("<player>", Component.text(member.rawName).color(mochaYellow))
            )
        }

    private val memberModifyUnlinkBeAccount = commandManager.commandBuilder("member")
        .permission("member.modify.unlinkbedrock")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("unlinkbedrock")
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .flag(commandManager.flagBuilder("force").withAliases("f"))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())
            val force = it.flags().isPresent("force")

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            if (authType.isBedrock && !force) {
                sender.sendMessage(MEMBER_MODIFY_UNLINK_BE_FAILED_ALREADY_BE_ONLY)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (member.bedrockAccount == null) {
                sender.sendMessage(MEMBER_MODIFY_UNLINK_BE_FAILED_NOT_LINKED)
                return@suspendingHandler
            }

            member.unlinkBedrock()
            member.save()

            if (isFloodgatePlayer(member.id)) {
                val player = proxy.getPlayer(member.id).get()
                player.disconnect(MEMBER_MODIFY_UNLINK_BE_KICK)
            }

            sender.sendMessage(MEMBER_MODIFY_UNLINK_BE_SUCCEED
                .replace("<player>", Component.text(member.rawName)))
        }

    private val memberModifyHide = commandManager.commandBuilder("member")
        .permission("member.modify.hide")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("hide")
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (member.isHidden) {
                sender.sendMessage(
                    MEMBER_MODIFY_HIDE_FAILED
                        .replace("<player>", Component.text(member.rawName))
                )
                return@suspendingHandler
            }

            member.modifier.hide(true)
            member.save()

            sender.sendMessage(
                MEMBER_MODIFY_HIDE_SUCCEED
                    .replace("<player>", Component.text(member.rawName))
            )
        }

    private val memberModifyUnHide = commandManager.commandBuilder("member")
        .permission("member.modify.unhide")
        .literal("modify")
        .argument(velocityRequiredOnlinePlayersArgument())
        .literal("unhide")
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            if (!member.isHidden) {
                sender.sendMessage(
                    MEMBER_MODIFY_UN_HIDE_FAILED
                        .replace("<player>", Component.text(member.rawName))
                )
                return@suspendingHandler
            }

            member.modifier.hide(false)
            member.save()

            sender.sendMessage(
                MEMBER_MODIFY_UN_HIDE_SUCCEED
                    .replace("<player>", Component.text(member.rawName))
            )
        }

    private val memberLookup = commandManager.commandBuilder("member")
        .permission("member.lookup")
        .literal("lookup")
        .argument(velocityRequiredOnlinePlayersArgument())
        .flag(commandManager.flagBuilder("auth").withAliases("a").withComponent(authTypeArg))
        .suspendingHandler {
            val sender = it.sender()
            val name = it.get<String>("name")
            val authType = parseAuthType(it.flags())

            if (authType == null) {
                sender.sendMessage(MEMBER_FETCH_FAILED_UNKNOWN_AUTH_TYPE)
                return@suspendingHandler
            }

            if (!memberService.exist(name, authType)) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@suspendingHandler
            }

            val member = memberService.lookup(name, authType)!!

            sender.sendMessage(
                MEMBER_LOOKUP
                    .replace("<uid>", member.uid.toString())
                    .replace("<id>", member.id.toString())
                    .replace("<name>", member.name)
                    .replace("<rawName>", member.rawName)
                    .replace("<whitelistStatus>", member.whitelistStatus.toString())
                    .replace("<authType>", member.authType.toString())
                    .replace("<createdAt>", formattedTime(member.createdAt))
                    .replace("<lastJoinedAt>", formattedTime(member.lastJoinedAt))
                    .replace("<lastQuitedAt>", formattedTime(member.lastQuitedAt))
                    .replace("<dataContainer>", member.dataContainer.contents.toString())
                    .replace("<bedrockAccount>", nullableString(member.bedrockAccount?.gamertag))
                    .replace("<bio>", nullableString(member.bio))
                    .replace("<isHidden>", booleanString(member.isHidden))
            )
        }

    private fun parseAuthType(flagContext: FlagContext): AuthType? {
        if (!flagContext.isPresent("auth")) {
            return AuthType.OFFICIAL
        }

        val type: AuthType? = try {
            AuthType.valueOf(flagContext.get<String>("auth")!!.uppercase())
        } catch (e: Exception) {
            null
        }

        return type
    }

    private fun formattedTime(instant: Instant?): String {
        if (instant == null) {
            return "无"
        }

        val zonedDateTime = instant.atZone(ZoneId.of("Asia/Shanghai"))
        val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd a hh:mm")
            .withLocale(Locale.forLanguageTag("zh-CN"))
            .withZone(ZoneId.of("Asia/Shanghai"))

        return formatter.format(zonedDateTime)
    }

    private fun nullableString(string: String?): String {
        if (string == null) {
            return "无"
        }

        return string
    }

    private fun booleanString(boolean: Boolean): String {
        return if (boolean) "是" else "否"
    }

    init {
        command(memberCreate)
        command(memberModifyExemptWhitelist)
        command(memberModifyGrantWhitelist)
        command(memberLookup)
        command(memberModifyLinkBeAccount)
        command(memberModifyUnlinkBeAccount)
        command(memberModifyHide)
        command(memberModifyUnHide)
    }

}