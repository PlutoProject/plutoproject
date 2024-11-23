package ink.pmc.daily.screens

import androidx.compose.runtime.*
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import ink.pmc.advkt.component.italic
import ink.pmc.advkt.component.text
import ink.pmc.daily.*
import ink.pmc.daily.api.Daily
import ink.pmc.daily.api.DailyHistory
import ink.pmc.framework.interactive.LocalPlayer
import ink.pmc.framework.interactive.inventory.*
import ink.pmc.framework.interactive.inventory.canvas.Chest
import ink.pmc.framework.interactive.inventory.click.clickable
import ink.pmc.framework.interactive.inventory.jetpack.Arrangement
import ink.pmc.framework.interactive.inventory.layout.Box
import ink.pmc.framework.interactive.inventory.layout.Column
import ink.pmc.framework.interactive.inventory.layout.Row
import ink.pmc.framework.utils.chat.UI_PAGING_SOUND
import ink.pmc.framework.utils.chat.UI_SUCCEED_SOUND
import ink.pmc.framework.utils.chat.replace
import ink.pmc.framework.utils.dsl.itemStack
import ink.pmc.framework.utils.time.*
import ink.pmc.framework.utils.trimmed
import ink.pmc.framework.utils.visual.mochaFlamingo
import ink.pmc.framework.utils.visual.mochaSubtext0
import ink.pmc.framework.utils.visual.mochaText
import kotlinx.coroutines.launch
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.meta.SkullMeta
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.ZonedDateTime

class DailyCalenderScreen : Screen {
    override val key: ScreenKey = "daily_screen"

    private val localYearMonth: ProvidableCompositionLocal<MutableState<YearMonth>> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localLoadedHistory: ProvidableCompositionLocal<MutableMap<YearMonth, MutableList<DailyHistory>>> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localCheckInDays: ProvidableCompositionLocal<Int> =
        staticCompositionLocalOf { error("Unexpected") }
    private val localAccumulatedDays: ProvidableCompositionLocal<MutableState<Int>> =
        staticCompositionLocalOf { error("Unexpected") }

    @Composable
    override fun Content() {
        val player = LocalPlayer.current
        val date by remember { mutableStateOf(ZonedDateTime.now(player.zoneId)) }
        Chest(
            title = UI_TITLE
                .replace("<time>", date.formatDate()),
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
        val yearMonth = remember { mutableStateOf(YearMonth.now(player.zoneId)) }
        val loadedHistory = remember { mutableStateMapOf<YearMonth, MutableList<DailyHistory>>() }
        val checkInDays by remember(loadedHistory) { derivedStateOf { loadedHistory.size } }
        val accumulatedDays = remember { mutableStateOf(0) } // 子组件需要修改这个 state

        LaunchedEffect(Unit) {
            accumulatedDays.value = Daily.getAccumulatedDays(player.uniqueId)
        }

        CompositionLocalProvider(
            localYearMonth provides yearMonth,
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

    @Composable
    @Suppress("FunctionName")
    private fun Calender() {
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

    @Composable
    @Suppress("FunctionName")
    private fun CalenderSection() {
        val player = LocalPlayer.current
        val yearMonth by localYearMonth.current
        val loadedHistory = localLoadedHistory.current

        val days = yearMonth.lengthOfMonth()
        val start = yearMonth.atStartOfMonth().atStartOfDay().toInstant(player.zoneId.toOffset())
        val end = yearMonth.atEndOfMonth().atEndOfDay().toInstant(player.zoneId.toOffset())

        LaunchedEffect(yearMonth) {
            if (loadedHistory.containsKey(yearMonth)) return@LaunchedEffect // 如果有缓存（即上面已经读入数据）就不查数据库
            loadedHistory[yearMonth] = Daily.getHistoryByTime(player.uniqueId, start, end).toMutableStateList()
        }

        VerticalGrid(modifier = Modifier.fillMaxSize()) {
            repeat(days) {
                val day = it + 1
                val date = yearMonth.atDay(day)
                val history = loadedHistory[yearMonth]?.firstOrNull { h -> h.createdDate == date }
                Day(date, history)
            }
        }
    }

    @Composable
    @Suppress("FunctionName")
    private fun Day(date: LocalDate, history: DailyHistory?) {
        val player = LocalPlayer.current
        val loadedHistory = localLoadedHistory.current
        val yearMonth by localYearMonth.current
        var accumulatedDays by localAccumulatedDays.current
        val coroutineScope = rememberCoroutineScope()

        /*
        * 0 -> 未签到
        * 1 -> 已签到
        * */
        // 可能残留状态，让它在 date 变化时重新初始化
        var state by remember(date, history) { mutableStateOf(if (history != null) 1 else 0) }
        val now by remember { mutableStateOf(LocalDate.now(player.zoneId)) }

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
                            .replace("<time>", date.formatDate())
                    )
                    it.lore(
                        when {
                            state == 0 && date == now -> DAY_LORE
                            state == 0 && date.isBefore(now) -> DAY_LORE_PAST
                            state == 1 -> history?.let { history ->
                                val lore =
                                    if (history.rewarded > 0) DAY_LORE_CHECKED_IN_REWARDED else DAY_LORE_CHECKED_IN
                                lore.replace(
                                    "<time>",
                                    Component.text(
                                        LocalDateTime.ofInstant(history.createdAt, player.zoneId).formatTime()
                                    )
                                ).replace("<reward>", history.rewarded.trimmed())
                            }?.toList() ?: emptyList()

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
                                if (Daily.isCheckedInToday(player.uniqueId)) return@launch
                                Daily.checkIn(player.uniqueId).also {
                                    loadedHistory[yearMonth]?.add(it)
                                    accumulatedDays++
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
        val player = LocalPlayer.current
        var yearMonth by localYearMonth.current

        val prev = yearMonth.minusMonths(1)
        val now by remember { mutableStateOf(YearMonth.now(player.zoneId)) }
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
                        yearMonth = prev
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.RIGHT -> {
                        yearMonth = next
                        whoClicked.playSound(UI_PAGING_SOUND)
                    }

                    ClickType.SHIFT_LEFT -> {
                        yearMonth = now
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
        val accumulatedDays by localAccumulatedDays.current

        val now by remember { mutableStateOf(LocalDate.now(player.zoneId)) }
        val monthStart by remember { derivedStateOf { now.withDayOfMonth(1).atStartOfDay() } }
        val monthEnd by remember { derivedStateOf { now.withDayOfMonth(now.lengthOfMonth()).atEndOfDay() } }
        val monthDays by remember {
            derivedStateOf {
                loadedHistory.values.flatten()
                    .filter { LocalDateTime.ofInstant(it.createdAt, player.zoneId) in monthStart..monthEnd }.size
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
                    text("$accumulatedDays ") with mochaText without italic()
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