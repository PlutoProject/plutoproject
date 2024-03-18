package ink.pmc.common.hypervisor

import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.absoluteValue

private fun calcServerUtil(mspt: Double, maxMSPT: Double): Double {
    val util = twoDecimal(mspt / maxMSPT)
    return util
}

private fun calcNetworkUtil(speed: Double, maxSpeed: Double): Double {
    val util = twoDecimal(speed / maxSpeed)
    return util
}

private fun calcVTOffset(networkUtil: Double): Int {
    if (networkUtil > 1.0) {
        return -2
    }

    var level = ((networkUtil - 1).absoluteValue / 0.14).toInt()
    level = level.coerceIn(0, 6)

    return level
}

private fun calcSTOffset(serverUtil: Double): Int {
    if (serverUtil > 1.0) {
        return -2
    }

    var level = ((serverUtil - 1).absoluteValue / 0.14).toInt()
    level = level.coerceIn(0, 6)

    return level
}

private fun twoDecimal(value: Double): Double {
    return BigDecimal(value).setScale(2, RoundingMode.HALF_EVEN).toDouble()
}

private fun calcBalance(vtOffset: Int, stOffset: Int): Pair<Int, Int> {
    if (vtOffset < stOffset) {
        return vtOffset to vtOffset
    }

    return vtOffset to stOffset
}

@Suppress("UNUSED")
fun calcOffset(
    mspt: Double,
    networkSpeed: Double,
    maxMSPT: Double = 50.0,
    maxNetworkSpeed: Double = 30720.0
): Pair<Int, Int> {
    val serverUtil = calcServerUtil(mspt, maxMSPT)
    val networkUtil = calcNetworkUtil(networkSpeed, maxNetworkSpeed)
    val vtOffset = calcVTOffset(networkUtil)
    val stOffset = calcSTOffset(serverUtil)

    return calcBalance(vtOffset, stOffset)
}