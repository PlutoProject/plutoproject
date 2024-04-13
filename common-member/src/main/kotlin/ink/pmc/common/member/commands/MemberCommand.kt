package ink.pmc.common.member.commands

import com.velocitypowered.api.command.CommandSource
import ink.pmc.common.member.*
import ink.pmc.common.member.api.AuthType
import ink.pmc.common.member.velocity.commandManager
import ink.pmc.common.member.velocity.proxy
import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.command.VelocityCommand
import ink.pmc.common.utils.concurrent.submitAsync
import org.incendo.cloud.component.CommandComponent
import org.incendo.cloud.component.DefaultValue
import org.incendo.cloud.parser.standard.StringParser
import org.incendo.cloud.suggestion.Suggestion
import java.util.concurrent.CompletableFuture

object MemberCommand : VelocityCommand() {

    private val authTypeArg = CommandComponent.builder<CommandSource, String>()
        .suggestionProvider { _, _ ->
            CompletableFuture.completedFuture(
                listOf(
                    Suggestion.simple("OFFICIAL"),
                    Suggestion.simple("LITTLESKIN"),
                    Suggestion.simple("BEDROCK_ONLY"),
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
        .argument(authTypeArg)
        .handler {
            submitAsync {
                val sender = it.sender()
                val name = it.get<String>("name").lowercase()
                val authType = AuthType.valueOf(it.get("authType"))

                sender.sendMessage(LOOKUP)
                val uuid = authType.fetcher.fetch(name)

                if (uuid == null) {
                    sender.sendMessage(LOOKUP_FAILED)
                    return@submitAsync
                }

                if (memberService.exist(uuid)) {
                    sender.sendMessage(MEMBER_ALREADY_EXIST)
                    return@submitAsync
                }

                memberService.create(name, authType)
                sender.sendMessage(MEMBER_ADD_SUCCEED.replace("<player>", name))
            }
        }!!

    val memberModifyExemptWhitelist = commandManager.commandBuilder("member")
        .permission("member.modify.exemptwhitelist")
        .literal("modify")
        .literal("exemptwhitlist")
        .required("name", StringParser.stringParser())
        .argument(authTypeArg)
        .handler {
            submitAsync {
                val sender = it.sender()
                val name = it.get<String>("name").lowercase()
                val authType = AuthType.valueOf(it.get("authType"))

                sender.sendMessage(LOOKUP)
                val uuid = authType.fetcher.fetch(name)

                if (uuid == null) {
                    sender.sendMessage(LOOKUP_FAILED)
                    return@submitAsync
                }

                if (!memberService.exist(uuid)) {
                    sender.sendMessage(MEMBER_NOT_EXIST)
                    return@submitAsync
                }

                val member = memberService.lookup(uuid)!!.refresh()!!

                member.exemptWhitelist()
                member.update()

                val player = proxy.getPlayer(uuid)

                if (player.isPresent) {
                    player.get().disconnect(NOT_WHITELISTED)
                }

                sender.sendMessage(MEMBER_EXEMPT_WHITELIST_SUCCEED.replace("<player>", name))
            }
        }

    init {
        command(memberCreate)
        command(memberModifyExemptWhitelist)
    }

}