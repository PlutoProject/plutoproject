package ink.pmc.hypervisor

import ink.pmc.advkt.component.*
import ink.pmc.advkt.sound.*
import ink.pmc.advkt.title.*
import ink.pmc.utils.chat.replace
import ink.pmc.utils.visual.*
import net.kyori.adventure.key.Key
import net.kyori.adventure.sound.Sound
import net.kyori.adventure.text.Component
import kotlin.time.Duration.Companion.seconds

val SERVER_STATUS
    get() = component {
        text("服务器状态：") with mochaFlamingo
        newline()
        text("  - ") with mochaSubtext0
        text("在线玩家：") with mochaText
        text(plugin.server.onlinePlayers.size.toString()) with mochaLavender
        newline()
        text("  - ") with mochaSubtext0
        text("当前 TPS：") with mochaText
        raw(sparkTPS5Secs.colorizedTPSComponent)
        newline()
        text("  - ") with mochaSubtext0
        text("当前 MSPT：") with mochaText
        raw(sparkMSPT10Secs.colorizedMSPTComponent)
        newline()
        text("  - ") with mochaSubtext0
        text("自 5s, 1m, 5m 以来的 TPS：") with mochaText
        raw(sparkTPS5Secs.colorizedTPSComponent)
        text(", ") with (mochaSubtext0)
        raw(sparkTPS1Mins.colorizedTPSComponent)
        text(", ") with (mochaSubtext0)
        raw(sparkTPS5Mins.colorizedTPSComponent)
        newline()

        text("  - ") with mochaSubtext0
        text("自 10s, 1m 以来的 MSPT：") with mochaText
        raw(sparkMSPT10Secs.colorizedMSPTComponent)
        text(", ") with mochaSubtext0
        raw(sparkMSPT1Min.colorizedMSPTComponent)
    }

val WORLD_STATUS = component {
    text("世界状态：") with mochaFlamingo
}

val WORLD_STATUS_ENTRIES: Component
    get() {
        val template = component {
            text("  - ") with mochaSubtext0
            text("<world>: ") with mochaText
            text("<players> ") with mochaLavender
            text("个玩家, ") with mochaSubtext0
            text("<entities> ") with mochaLavender
            text("个实体, ") with mochaSubtext0
            text("<chunks> ") with mochaLavender
            text("个已加载区块") with mochaSubtext0
        }

        return component {
            val worlds = plugin.server.worlds
            worlds.forEach {
                val name = it.name
                val players = it.playerCount
                val entities = it.entityCount
                val chunks = it.chunkCount

                raw(
                    template.replace("<world>", name)
                        .replace("<players>", players.toString())
                        .replace("<entities>", entities.toString())
                        .replace("<chunks>", chunks.toString())
                )

                if (worlds.indexOf(it) == worlds.size - 1) {
                    return@forEach
                }

                newline()
            }
        }
    }

val Double.colorizedTPSComponent: Component
    get() = component {
        text(this.toString()) with when {
            this@colorizedTPSComponent in 16.0..18.0 -> mochaYellow
            (this@colorizedTPSComponent < 16.0) -> mochaMaroon
            else -> mochaGreen
        }
    }

val Double.colorizedMSPTComponent: Component
    get() = component {
        text(this.toString()) with when {
            this@colorizedMSPTComponent in 0.0..35.0 -> mochaGreen
            this@colorizedMSPTComponent > 35.0 && this@colorizedMSPTComponent <= 50.0 -> mochaYellow
            else -> mochaMaroon
        }
    }

val MUSHROOM_DETECTOR_PLAYER_PLACE = title {
    mainTitle {
        text(" ")
    }
    subTitle {
        text("此处无法放置此类方块") with mochaMaroon
    }
    times {
        fadeIn(0.seconds)
        stay(1.seconds)
        fadeOut(0.seconds)
    }
}

val MUSHROOM_DETECTOR_PLAYER_PLACE_SOUND = sound {
    key(Key.key("block.note_block.hat"))
    source(Sound.Source.BLOCK)
    volume(1f)
    pitch(1f)
}