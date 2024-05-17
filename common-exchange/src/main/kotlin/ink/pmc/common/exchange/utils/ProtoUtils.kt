package ink.pmc.common.exchange.utils

import com.google.protobuf.ProtocolStringList
import com.google.protobuf.kotlin.DslList
import ink.pmc.common.exchange.proto.lobby2proxy.ItemDistributeNotifyKt.Dsl.ItemProxy
import ink.pmc.common.utils.player.itemStackFromBase64
import ink.pmc.common.utils.player.itemStackToBase64
import org.bukkit.inventory.ItemStack

fun DslList<String, ItemProxy>.encodeItems(array: List<ItemStack>) {
    val mutable = toMutableList()
    array.forEach {
        mutable.add(itemStackToBase64(it))
    }
}

fun ProtocolStringList.decodeItems(): List<ItemStack> {
    return map {
        itemStackFromBase64(it)
    }
}