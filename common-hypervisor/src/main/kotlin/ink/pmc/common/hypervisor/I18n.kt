package ink.pmc.common.hypervisor

import ink.pmc.common.utils.chat.replace
import ink.pmc.common.utils.visual.*
import net.kyori.adventure.text.Component

val SERVER_STATUS
    get() = Component.text("服务器状态：").color(mochaFlamingo)
        .appendNewline()
        .append(Component.text("  - ").color(mochaSubtext0))
        .append(Component.text("在线玩家：").color(mochaText))
        .append(Component.text(plugin.server.onlinePlayers.size).color(mochaLavender))
        .appendNewline()
        .append(Component.text("  - ").color(mochaSubtext0))
        .append(Component.text("当前 TPS：").color(mochaText))
        .append(sparkTPS5Secs.colorizedTPSComponent)
        .appendNewline()
        .append(Component.text("  - ").color(mochaSubtext0))
        .append(Component.text("当前 MSPT：").color(mochaText))
        .append(sparkMSPT10Secs.colorizedMSPTComponent)
        .appendNewline()
        .append(Component.text("  - ").color(mochaSubtext0))
        .append(Component.text("自 5s, 1m, 5m 以来的 TPS：").color(mochaText))
        .append(sparkTPS5Secs.colorizedTPSComponent)
        .append(Component.text(", ").color(mochaSubtext0))
        .append(sparkTPS1Mins.colorizedTPSComponent)
        .append(Component.text(", ").color(mochaSubtext0))
        .append(sparkTPS5Mins.colorizedTPSComponent)
        .appendNewline()
        .append(Component.text("  - ").color(mochaSubtext0))
        .append(Component.text("自 10s, 1m 以来的 MSPT：").color(mochaText))
        .append(sparkMSPT10Secs.colorizedMSPTComponent)
        .append(Component.text(", ").color(mochaSubtext0))
        .append(sparkMSPT1Min.colorizedMSPTComponent)

val WORLD_STATUS
    get() = Component.text("世界状态：").color(mochaFlamingo)

val WORLD_STATUS_ENTRIES: Component
    get() {
        val single = Component.text("  - ").color(mochaSubtext0)
            .append(Component.text("<world>: ").color(mochaText))
            .append(Component.text("<players> ").color(mochaLavender))
            .append(Component.text("个玩家, ").color(mochaSubtext0))
            .append(Component.text("<entities> ").color(mochaLavender))
            .append(Component.text("个实体, ").color(mochaSubtext0))
            .append(Component.text("<chunks> ").color(mochaLavender))
            .append(Component.text("个已加载区块").color(mochaSubtext0))

        var result: Component = Component.empty()
        val worlds = plugin.server.worlds

        worlds.forEach {
            val name = it.name
            val players = it.playerCount
            val entities = it.entityCount
            val chunks = it.chunkCount

            val replaced = single
                .replace("<world>", name)
                .replace("<players>", players.toString())
                .replace("<entities>", entities.toString())
                .replace("<chunks>", chunks.toString())

            result = result.append(replaced)

            if (worlds.indexOf(it) == worlds.size - 1) {
                return@forEach
            }

            result = result.appendNewline()
        }

        return result
    }

val Double.colorizedTPSComponent: Component
    get() {
        if (this in 16.0..18.0) {
            return Component.text(this).color(mochaYellow)
        }

        if (this < 16.0) {
            return Component.text(this).color(mochaMaroon)
        }

        return Component.text(this).color(mochaGreen)
    }

val Double.colorizedMSPTComponent: Component
    get() {
        if (this in 0.0..35.0) {
            return Component.text(this).color(mochaGreen)
        }

        if (this > 35.0 && this <= 50.0) {
            return Component.text(this).color(mochaYellow)
        }

        return Component.text(this).color(mochaMaroon)
    }