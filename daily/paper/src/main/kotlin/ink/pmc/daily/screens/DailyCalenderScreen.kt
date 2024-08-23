package ink.pmc.daily.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.daily.NAVIGATE
import ink.pmc.daily.NAVIGATE_LORE
import ink.pmc.daily.NAVIGATE_LORE_PREV_REACHED
import ink.pmc.daily.UI_TITLE
import ink.pmc.interactive.api.inventory.components.Back
import ink.pmc.interactive.api.inventory.components.Background
import ink.pmc.interactive.api.inventory.components.Item
import ink.pmc.interactive.api.inventory.components.VerticalGrid
import ink.pmc.interactive.api.inventory.components.canvases.Chest
import ink.pmc.interactive.api.inventory.jetpack.Arrangement
import ink.pmc.interactive.api.inventory.layout.Box
import ink.pmc.interactive.api.inventory.layout.Column
import ink.pmc.interactive.api.inventory.layout.Row
import ink.pmc.interactive.api.inventory.modifiers.Modifier
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.interactive.api.inventory.modifiers.fillMaxSize
import ink.pmc.interactive.api.inventory.modifiers.fillMaxWidth
import ink.pmc.interactive.api.inventory.modifiers.height
import ink.pmc.utils.chat.UI_PAGING_SOUND
import ink.pmc.utils.chat.replace
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import java.time.YearMonth
import java.time.ZonedDateTime

class DailyCalenderScreen : Screen {

    override val key: ScreenKey = "daily_screen"

    private val calendarYearMonth: ProvidableCompositionLocal<YearMonth> =
        staticCompositionLocalOf { error("Unexpected") }

    @Composable
    override fun Content() {
        val date by rememberSaveable { mutableStateOf(ZonedDateTime.now()) }
        Chest(
            title = UI_TITLE
                .replace("<year>", date.year)
                .replace("<month>", date.month)
                .replace("<day>", date.dayOfMonth),
            modifier = Modifier.height(6)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Background()
                Column(modifier = Modifier.fillMaxSize()) {
                    InnerContents()
                }
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun InnerContents() {
        Top()
        Calender()
    }

    @Composable
    @Suppress("FunctionName")
    private fun Top() {
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            Back()
        }
    }

    inner class CalenderScreen(private val yearMonth: YearMonth) : Screen {
        override val key: ScreenKey = "daily_screen_calender"

        @Composable
        override fun Content() {
            CompositionLocalProvider(calendarYearMonth provides yearMonth) {
                CalenderSection()
                Bottom()
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Calender() {
        Navigator(CalenderScreen(YearMonth.now()))
    }

    @Composable
    @Suppress("FunctionName")
    private fun CalenderSection() {
        val yearMonth = calendarYearMonth.current
        val days = yearMonth.lengthOfMonth()
        
        VerticalGrid(modifier = Modifier.fillMaxWidth().height(4)) {
            repeat(days) {
                Day(yearMonth, it + 1)
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Day(yearMonth: YearMonth, day: Int) {

    }

    @Composable
    @Suppress("FunctionName")
    private fun Bottom() {
        Row(modifier = Modifier.fillMaxWidth().height(1), horizontalArrangement = Arrangement.Center) {
            Navigate()
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Navigate() {
        val navigator = LocalNavigator.currentOrThrow
        val yearMonth = calendarYearMonth.current

        val prev = yearMonth.minusMonths(1)
        val now by rememberSaveable { mutableStateOf(YearMonth.now()) }
        val next = yearMonth.plusMonths(1)

        fun isReachedLimit(): Boolean {
            return yearMonth == now.minusYears(12)
        }

        val lore = if (!isReachedLimit()) NAVIGATE_LORE else NAVIGATE_LORE_PREV_REACHED

        Item(
            material = Material.ARROW,
            name = NAVIGATE
                .replace("<year>", yearMonth.year)
                .replace("<month>", yearMonth.month),
            lore = lore
                .replace("<prevYear>", prev.year)
                .replace("<prevMonth>", prev.month)
                .replace("<nextYear>", next.year)
                .replace("<nextMonth>", next.month)
                .toList(),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (isReachedLimit()) return@clickable
                        navigator.push(CalenderScreen(prev))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.RIGHT -> {
                        navigator.push(CalenderScreen(next))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.SHIFT_LEFT -> {
                        navigator.push(CalenderScreen(now))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    else -> {}
                }
            }
        )
    }

}