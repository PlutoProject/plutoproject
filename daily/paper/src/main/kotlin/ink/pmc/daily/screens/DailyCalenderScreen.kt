package ink.pmc.daily.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.daily.*
import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.interactive.api.LocalPlayer
import ink.pmc.interactive.api.inventory.components.*
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
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.chat.replace
import ink.pmc.utils.concurrent.submitAsyncIO
import ink.pmc.utils.time.atEndOfDay
import ink.pmc.utils.time.atStartOfMonth
import ink.pmc.utils.time.currentZoneId
import ink.pmc.utils.time.toOffset
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.*

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
                .replace("<month>", date.month.value)
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
        val navigator = LocalNavigator.currentOrThrow
        Row(modifier = Modifier.fillMaxWidth().height(1)) {
            if (!navigator.canPop) return@Row
            Back()
        }
    }

    inner class CalenderScreen(private val yearMonth: YearMonth) : Screen {
        // 需要一个唯一的 key，否则在翻页的时候会残留老状态
        override val key: ScreenKey = "daily_screen_calender_${UUID.randomUUID()}"

        @Composable
        override fun Content() {
            CompositionLocalProvider(calendarYearMonth provides yearMonth) {
                Box(modifier = Modifier.fillMaxWidth().height(4)) {
                    VerticalGrid(modifier = Modifier.fillMaxSize()) {
                        repeat(4 * 9) {
                            Space()
                        }
                    }
                    CalenderSection()
                }
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
        val daily = koinInject<Daily>()
        val player = LocalPlayer.current
        /*
        * 0 -> 正常
        * 1 -> 加载中
        * */
        var state by remember { mutableStateOf(1) }
        val histories = rememberSaveable { mutableStateListOf<DailyHistory>() }
        val yearMonth = calendarYearMonth.current
        val days = yearMonth.lengthOfMonth()

        val start = yearMonth.atStartOfMonth().atStartOfDay().toInstant(currentZoneId.toOffset())
        val end = yearMonth.atEndOfMonth().atEndOfDay().toInstant(currentZoneId.toOffset())

        LaunchedEffect(yearMonth) {
            if (state != 1) return@LaunchedEffect
            histories.clear() // 理论上来说这个 list 里不应该有东西，但是防止意外情况
            histories.addAll(daily.getHistoryByTime(player.uniqueId, start, end).toList())
            state = 0
        }

        VerticalGrid(modifier = Modifier.fillMaxSize()) {
            repeat(days) {
                val day = it + 1
                val date = yearMonth.atDay(day)
                val history = histories.firstOrNull { h -> h.createdDate == date }
                Day(date, history)
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Day(date: LocalDate, history: DailyHistory?) {
        val daily = koinInject<Daily>()
        val player = LocalPlayer.current
        /*
        * 0 -> 未签到
        * 1 -> 已签到
        * */
        var state by remember(history) { mutableStateOf(if (history != null) 1 else 0) }
        val now by rememberSaveable { mutableStateOf(LocalDate.now()) }

        val head = when {
            state == 0 && date == now -> yellowExclamationHead
            state == 0 && date.isBefore(now) -> redCrossHead
            state == 1 -> greenCheckHead
            date.isAfter(now) -> grayQuestionHead
            else -> error("Unreachable")
        }

        Item(
            itemStack = head.clone().apply {
                amount = date.dayOfMonth
                editMeta {
                    it.itemName(
                        DAY
                            .replace("<year>", date.year)
                            .replace("<month>", date.month.value)
                            .replace("<day>", date.dayOfMonth)
                    )
                    it.lore(
                        when {
                            state == 0 && date == now -> DAY_LORE
                            state == 0 && date.isBefore(now) -> DAY_LORE_PAST
                            state == 1 -> DAY_LORE_CHECKED_IN
                            date.isAfter(now) -> DAY_LORE_FUTURE
                            else -> error("Unreachable")
                        }
                    )
                    it.setEnchantmentGlintOverride(date == now)
                }
            },
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (state == 0 && date == now) {
                            submitAsyncIO {
                                if (daily.isCheckedInToday(player.uniqueId)) return@submitAsyncIO
                                daily.checkIn(player.uniqueId)
                            }
                            state = 1
                            player.playSound(UI_SUCCEED_SOUND)
                        }
                    }

                    else -> {}
                }
            }
        )
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
            return yearMonth == now.minusMonths(12)
        }

        val lore = if (!isReachedLimit()) NAVIGATE_LORE else NAVIGATE_LORE_PREV_REACHED

        Item(
            material = Material.ARROW,
            name = NAVIGATE
                .replace("<year>", yearMonth.year)
                .replace("<month>", yearMonth.month.value),
            lore = lore
                .replace("<prevYear>", prev.year)
                .replace("<prevMonth>", prev.month.value)
                .replace("<nextYear>", next.year)
                .replace("<nextMonth>", next.month.value)
                .toList(),
            modifier = Modifier.clickable {
                when (clickType) {
                    ClickType.LEFT -> {
                        if (isReachedLimit()) return@clickable
                        navigator.replaceAll(CalenderScreen(prev))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.RIGHT -> {
                        navigator.replaceAll(CalenderScreen(next))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.MIDDLE -> {
                        navigator.replaceAll(CalenderScreen(now))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    else -> {}
                }
            }
        )
    }

}