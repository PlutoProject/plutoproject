package ink.pmc.daily.screens

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
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
import ink.pmc.interactive.api.inventory.modifiers.*
import ink.pmc.interactive.api.inventory.modifiers.click.clickable
import ink.pmc.utils.chat.UI_PAGING_SOUND
import ink.pmc.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.utils.chat.replace
import ink.pmc.utils.dsl.itemStack
import ink.pmc.utils.time.*
import ink.pmc.utils.visual.mochaFlamingo
import ink.pmc.utils.visual.mochaSubtext0
import ink.pmc.utils.visual.mochaText
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.SkullMeta
import org.koin.compose.koinInject
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.*

class DailyCalenderScreen : Screen {

    override val key: ScreenKey = "daily_screen"

    private val calendarYearMonth: ProvidableCompositionLocal<YearMonth> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localLoadedHistory: ProvidableCompositionLocal<MutableMap<YearMonth, MutableList<DailyHistory>>> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localCheckInDays: ProvidableCompositionLocal<Int> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localAccumulatedDays: ProvidableCompositionLocal<State<Int>> =
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
        val player = LocalPlayer.current
        val daily = koinInject<Daily>()

        val loadedHistory = rememberSaveable { mutableStateMapOf<YearMonth, MutableList<DailyHistory>>() }
        val checkInDays by rememberSaveable(loadedHistory) { derivedStateOf { loadedHistory.size } }
        val accumulatedDays = rememberSaveable { mutableStateOf(0) } // 这里需要将 state 提供给子组件，以在数据更新时重组需要的子组件

        LaunchedEffect(loadedHistory.size) {
            accumulatedDays.value = daily.getAccumulatedDays(player.uniqueId)
        }

        CompositionLocalProvider(
            localLoadedHistory provides loadedHistory,
            localCheckInDays provides checkInDays,
            localAccumulatedDays provides accumulatedDays
        ) {
            Top()
            Calender()
        }
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
        val yearMonth = calendarYearMonth.current
        val loadedHistory = localLoadedHistory.current

        /*
        * 0 -> 正常
        * 1 -> 加载中
        * */
        var state by remember { mutableStateOf(1) }
        val histories = rememberSaveable(loadedHistory) {
            mutableStateListOf<DailyHistory>().apply {
                addAll(loadedHistory[yearMonth] ?: listOf()) // 先尝试直接从缓存里取
            }
        }
        val days = yearMonth.lengthOfMonth()

        val start = yearMonth.atStartOfMonth().atStartOfDay().toInstant(currentZoneId.toOffset())
        val end = yearMonth.atEndOfMonth().atEndOfDay().toInstant(currentZoneId.toOffset())

        suspend fun fetchHistory(): List<DailyHistory> {
            return loadedHistory.getOrPut(yearMonth) {
                daily.getHistoryByTime(player.uniqueId, start, end).toMutableStateList()
            }
        }

        LaunchedEffect(Unit) {
            if (state != 1) return@LaunchedEffect
            if (loadedHistory.containsKey(yearMonth)) return@LaunchedEffect // 如果有缓存（即上面已经读入数据）就不查数据库
            histories.clear() // 理论上来说这个 list 里不应该有东西，但是防止意外情况
            histories.addAll(fetchHistory())
            // histories.addAll(daily.getHistoryByTime(player.uniqueId, start, end).toList())
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
        val loadedHistory = localLoadedHistory.current
        val yearMonth = calendarYearMonth.current
        val coroutineScope = rememberCoroutineScope()

        /*
        * 0 -> 未签到
        * 1 -> 已签到
        * */
        var snapshotHistory by remember(history) { mutableStateOf(history) }
        var state by remember { mutableStateOf(if (snapshotHistory != null) 1 else 0) }
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
                            state == 1 -> snapshotHistory?.let { h ->
                                DAY_LORE_CHECKED_IN.replace("<time>", Component.text(h.createdAt.format()))
                            }?.toList() ?: listOf()

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
                            coroutineScope.launch {
                                if (daily.isCheckedInToday(player.uniqueId)) return@launch
                                daily.checkIn(player.uniqueId).also {
                                    loadedHistory[yearMonth]?.add(it)
                                    snapshotHistory = it
                                }
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
            Spacer(modifier = Modifier.size(1))
            Player()
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
                        navigator.replace(CalenderScreen(prev))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.RIGHT -> {
                        navigator.replace(CalenderScreen(next))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.MIDDLE -> {
                        navigator.replace(CalenderScreen(now))
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    else -> {}
                }
            }
        )
    }

    @Composable
    @Suppress("FunctionName")
    private fun Player() {
        val player = LocalPlayer.current
        val loadedHistory = localLoadedHistory.current
        val accumulatedDays = localAccumulatedDays.current

        val now by rememberSaveable { mutableStateOf(LocalDate.now()) }
        val monthStart by rememberSaveable { derivedStateOf { now.withDayOfMonth(1).atStartOfDay() } }
        val monthEnd by rememberSaveable { derivedStateOf { now.withDayOfMonth(now.lengthOfMonth()).atEndOfDay() } }
        val monthDays by rememberSaveable {
            derivedStateOf {
                loadedHistory.values.flatten().filter { it.createdAt in monthStart..monthEnd }.size
            }
        }

        Item(
            itemStack = itemStack(Material.PLAYER_HEAD) {
                displayName {
                    text(player.name) with mochaFlamingo without italic()
                }
                lore {
                    text("本月已到访 ") with mochaSubtext0 without italic()
                    text("$monthDays ") with mochaText without italic()
                    text("天，连续 ") with mochaSubtext0 without italic()
                    text("${accumulatedDays.value} ") with mochaText without italic()
                    text("天") with mochaSubtext0 without italic()
                }
                meta {
                    this as SkullMeta
                    playerProfile = player.playerProfile
                }
            }
        )
    }

}