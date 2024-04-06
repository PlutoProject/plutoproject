package ink.pmc.common.member

import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.concurrent.submitAsync
import ink.pmc.common.utils.currentUnixTimestamp
import ink.pmc.common.utils.unixTimestamp
import ink.pmc.common.utils.visual.mochaYellow
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import java.util.*
import kotlin.time.Duration

val memberMigratorCommand = paperCommandManager.commandBuilder("member")
    .permission("member.migrator")
    .literal("migrate")
    .handler {
        submitAsync {
            val time = 5184000
            val sender = it.sender()

            val whitelisted = Bukkit.getServer().whitelistedPlayers.map { it.name!! }
            val hasId = mutableListOf<String>()
            val name2UUIDMap = mutableMapOf<String, UUID>()

            var count = 0

            whitelisted.forEach {
                if (count >= 30) {
                    sender.sendMessage(WAITING_API)
                    delay(Duration.parse("60s"))
                    count = 0
                }

                val id = getUUIDFromMojang(it)

                if (id != null) {
                    hasId.add(it)
                    val parsed = ID_ADDED.replace("<player>", Component.text(it).color(mochaYellow))
                    memberManager.createAndRegister {
                        uuid = id
                        name = it
                        joinTime = currentUnixTimestamp
                    }

                    val member = memberManager.get(id)!!
                    member.data.data["is_created_by_migrator"] = true
                    member.update()

                    sender.sendMessage(parsed)
                    name2UUIDMap[it.lowercase()] = id
                    ++count
                    return@forEach
                }

                val parsed = ID_NOT_EXIST.replace("<player>", Component.text(it).color(mochaYellow))
                sender.sendMessage(parsed)
                ++count
            }

            hasId.forEach {
                val lastJoinTime = coreProtectAPI.sessionLookup(it, time)
                    .map { data -> coreProtectAPI.parseResult(data) }
                    .filter { id -> id.actionId == 1 }
                    .maxOfOrNull { result -> result.timestamp }
                val lastQuitTime = coreProtectAPI.sessionLookup(it, time)
                    .map { data -> coreProtectAPI.parseResult(data) }
                    .filter { id -> id.actionId == 0 }
                    .maxOfOrNull { result -> result.timestamp }

                val member = memberManager.get(name2UUIDMap[it.lowercase()]!!)

                if (member == null) {
                    sender.sendMessage(TIME_UPDATED_FAILED.replace("<player>", Component.text(it).color(mochaYellow)))
                    return@forEach
                }

                if (lastJoinTime != null) {
                    member.lastJoinTime = lastJoinTime
                }

                if (lastQuitTime != null) {
                    member.lastQuitTime = lastQuitTime
                }

                member.update()
                sender.sendMessage(TIME_UPDATED.replace("<player>", Component.text(it).color(mochaYellow)))
            }

            sender.sendMessage(OPERATION_FINISHED)
        }
    }