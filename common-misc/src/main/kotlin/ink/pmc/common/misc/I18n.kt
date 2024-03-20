package ink.pmc.common.misc

import ink.pmc.common.utils.visual.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import net.kyori.adventure.title.Title
import java.time.Duration

val SUICIDE
    get() = Component.text("你终结了你自己...")
        .color(mochaFlamingo)

val STAND_UP
    get() = Component.text("使用 ").color(mochaText)
        .append(Component.keybind("key.sneak").color(mochaFlamingo))
        .append(Component.text(" 来站起").color(mochaText))

val ILLEGAL_LOC
    get() =
        Component.text("无法在此处坐下，请检查是否有实体方块和足够的空间").color(mochaMaroon)

val MULTI_SITTERS_TITLE
    get() = Title.title(
        Component.text(" "), Component.text("已有其他人坐在这个位置").color(mochaMaroon), Title.Times.times(
            Duration.ZERO, Duration.ofMillis(1000), Duration.ZERO
        )
    )

val MULTI_SITTERS_SOUND
    get() = Sound.sound(Key.key("block.note_block.hat"), Sound.Source.BLOCK, 1F, 1F)

val CHAT_FORMAT
    get() = Component.text("<player>").color(mochaYellow)
        .append(Component.text(": ").color(mochaSubtext0))
        .append(Component.text("<message>").color(mochaText))

val JOIN_FORMAT
    get() = Component.text("[+] ").color(mochaGreen)
        .append(Component.text("<player> ").color(mochaYellow))
        .append(Component.text("加入了游戏").color(mochaText))

val QUIT_FORMAT
    get() = Component.text("[-] ").color(mochaRed)
        .append(Component.text("<player> ").color(mochaYellow))
        .append(Component.text("退出了游戏").color(mochaText))