package ink.pmc.common.member

import ink.pmc.common.utils.chat.replace
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.incendo.cloud.parser.standard.StringParser
import java.time.Duration
import java.util.Date

@OptIn(DelicateCoroutinesApi::class)
val memberAddCommand = commandManager.commandBuilder("member")
    .literal("add")
    .required("name", StringParser.stringParser())
    .handler {
        GlobalScope.launch {
            val sender = it.sender()
            val name = it.get<String>("name").lowercase()
            sender.sendMessage(LOOKUP)

            val uuid = getUUIDFromMojang(name)

            if (uuid == null) {
                sender.sendMessage(LOOKUP_FAILED)
                return@launch
            }

            if (memberManager.exist(uuid)) {
                sender.sendMessage(MEMBER_ALREADY_EXIST)
                return@launch
            }

            memberManager.createAndRegister {
                this.name = name
                this.uuid = uuid
                this.joinTime = Date()
            }

            sender.sendMessage(MEMBER_ADD_SUCCEED.replace("<player>", name))
        }
    }

@OptIn(DelicateCoroutinesApi::class)
val memberLookupCommand = commandManager.commandBuilder("member")
    .literal("lookup")
    .required("name", StringParser.stringParser())
    .handler {
        GlobalScope.launch {
            val sender = it.sender()
            val name = it.get<String>("name").lowercase()
            sender.sendMessage(LOOKUP)

            val uuid = getUUIDFromMojang(name)

            if (uuid == null) {
                sender.sendMessage(LOOKUP_FAILED)
                return@launch
            }

            val member = memberManager.get(uuid)

            if (member == null) {
                sender.sendMessage(MEMBER_NOT_EXIST)
                return@launch
            }

            val message = MEMBER_LOOKUP
                .replace("<player>", name)
                .replace("<uuid>", uuid.toString())
                .replace("<bio>", member.bio.toString())
                .replace("<joinTime>", member.joinTime.toString())
                .replace("<lastJoinTime>", member.lastJoinTime.toString())
                .replace("<lastQuitTime>", member.lastQuitTime.toString())
                .replace("<data>", member.data.data.toString())
                .replace("<totalPlayTime>", Duration.ofMillis(member.totalPlayTime).toString())

            sender.sendMessage(message)
        }
    }