package ink.pmc.daily.commands

import cafe.adriel.voyager.navigator.Navigator
import ink.pmc.daily.CHECKED_IN
import ink.pmc.daily.api.Daily
import ink.pmc.daily.screens.DailyCalenderScreen
import ink.pmc.interactive.api.GuiManager
import ink.pmc.utils.PaperCm
import ink.pmc.utils.command.annotation.Command
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.command.checkPlayer
import ink.pmc.utils.dsl.cloud.invoke
import ink.pmc.utils.dsl.cloud.sender
import org.koin.java.KoinJavaComponent.getKoin

@Command("checkin")
@Suppress("UNUSED")
fun PaperCm.checkIn(aliases: Array<String>) {
    this("checkin", *aliases) {
        permission("daily.checkin")
        handler {
            checkPlayer(sender.sender) {
                val daily = getKoin().get<Daily>()
                val user = daily.getUserOrCreate(uniqueId)

                if (user.isCheckedInToday()) {
                    sendMessage(CHECKED_IN)
                    return@checkPlayer
                }

                user.checkIn()
                playSound(UI_SUCCEED_SOUND)
            }
        }

        "gui" {
            permission("daily.checkin.gui")
            handler {
                checkPlayer(sender.sender) {
                    GuiManager.startInventory(this) {
                        Navigator(DailyCalenderScreen())
                    }
                }
            }
        }
    }
}