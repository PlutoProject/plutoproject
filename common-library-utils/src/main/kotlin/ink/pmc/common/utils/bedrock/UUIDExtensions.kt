package ink.pmc.common.utils.bedrock

import java.util.*

@Suppress("UNUSED")
val UUID.xuid: String
    get() {
        val uuidStr = "00000000-0000-0000-0009-01f4bf5b7415"
        val part2 = uuidStr.substring(uuidStr.lastIndexOf('-') + 1, 35 + 1)
        val replaced = uuidStr.replace("-$part2", "")
        val part1 = replaced.substring(replaced.lastIndexOf('-') + 1, 22 + 1)

        return "$part1$part2"
    }