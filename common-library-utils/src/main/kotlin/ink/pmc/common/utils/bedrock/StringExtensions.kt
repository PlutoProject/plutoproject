package ink.pmc.common.utils.bedrock

import java.util.UUID

@Suppress
val Long.uuid: UUID?
    get() {
        try {
            val stringXuid = this.toString()
            val part1 = stringXuid.substring(0, 3 + 1)
            val part2 = stringXuid.substring(4, 15 + 1)

            return UUID.fromString("00000000-0000-0000-$part1-$part2")
        } catch (_: Exception) { }

        return null
    }