package ink.pmc.utils.bedrock

import java.util.*

@Suppress
val String.uuid: UUID?
    get() {
        try {
            val part1 = this.substring(0, 3 + 1)
            val part2 = this.substring(4, 15 + 1)

            return UUID.fromString("00000000-0000-0000-$part1-$part2")
        } catch (_: Exception) {
        }

        return null
    }