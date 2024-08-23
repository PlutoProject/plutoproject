package ink.pmc.daily.commands

import ink.pmc.advkt.component.text
import ink.pmc.advkt.send
import ink.pmc.daily.api.Daily
import ink.pmc.utils.PaperCm
import ink.pmc.utils.annotation.Command
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import ink.pmc.utils.visual.mochaPink
import ink.pmc.utils.visual.mochaSubtext0

@Command("checkin")
@Suppress("UNUSED")
fun PaperCm.checkIn(aliases: Array<String>) {
    this("checkIn", *aliases) {
        permission("daily.checkin")
        handler {
            checkPlayer(sender.sender) {
                val user = Daily.getUserOrCreate(uniqueId)

                if (user.isCheckedInToday()) {
                    send { text("你今天已完成到访，请明天再来吧") with mochaSubtext0 }
                    return@checkPlayer
                }

                send { text("到访成功！") with mochaPink }
                playSound(UI_SUCCEED_SOUND)
                user.checkIn()
            }
        }
    }
}