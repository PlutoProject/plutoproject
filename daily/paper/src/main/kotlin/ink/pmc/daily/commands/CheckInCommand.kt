package ink.pmc.daily.commands

import ink.pmc.daily.CHECKED_IN
import ink.pmc.daily.api.Daily
import ink.pmc.utils.PaperCm
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender

@Command("checkin")
@Suppress("UNUSED")
fun PaperCm.checkIn(aliases: Array<String>) {
    this("checkin", *aliases) {
        permission("daily.checkin")
        handler {
            checkPlayer(sender.sender) {
                val user = Daily.getUserOrCreate(uniqueId)

                if (user.isCheckedInToday()) {
                    sendMessage(CHECKED_IN)
                    return@checkPlayer
                }

                user.checkIn()
                playSound(UI_SUCCEED_SOUND)
            }
        }
    }
}