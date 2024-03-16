package ink.pmc.common.member

import ink.pmc.common.utils.replace
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.incendo.cloud.parser.standard.StringParser
import java.util.Date

@OptIn(DelicateCoroutinesApi::class)
val whitelistAddCommand = commandManager.commandBuilder("whitelist")
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

            sender.sendMessage(MEMBER_ADD_SUCCEED.replace("<name>", name))
        }
    }