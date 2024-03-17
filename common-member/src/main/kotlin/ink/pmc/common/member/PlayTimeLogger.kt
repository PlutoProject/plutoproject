package ink.pmc.common.member

import com.velocitypowered.api.proxy.Player

object PlayTimeLogger {

    private val data = mutableMapOf<Player, Long>()

    fun start(player: Player) {
        val time = System.currentTimeMillis()
        data[player] = time
    }

    fun stop(player: Player): Long {
        if (!data.containsKey(player)) {
            throw RuntimeException("Operation not allowed before start play")
        }

        val startTime = data[player]!!
        val stopTime = System.currentTimeMillis()

        data.remove(player)

        return stopTime - startTime
    }

}