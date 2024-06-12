package ink.pmc.exchange

import ink.pmc.member.api.Member
import ink.pmc.utils.bedrock.useFallbackColors
import ink.pmc.utils.item.keyed
import ink.pmc.utils.visual.mochaFlamingo
import ink.pmc.utils.visual.mochaText
import ink.pmc.utils.visual.mochaYellow
import ink.pmc.visual.api.toast.Toast
import ink.pmc.visual.api.toast.dsl.toast
import net.kyori.adventure.text.Component
import org.bukkit.Material

fun Member.ticketGrantToast(): Toast {
    val count = exchangeService.tickets(this)
    return toast {
        icon(Material.PAPER.keyed)
        message(
            Component
                .text("你幸运地获得了一个兑换券").color(mochaFlamingo)
                .appendNewline()
                .append(Component.text("目前你拥有 ").color(mochaText))
                .append(Component.text("$count/$TICKETS_LIMIT").color(mochaYellow))
                .append(Component.text(" 个兑换券").color(mochaText))
        )
    }
}

fun Member.ticketGrantToastBe(): Toast {
    val count = exchangeService.tickets(this)
    return toast {
        icon(Material.PAPER.keyed)
        message(
            Component
                .text("你幸运地获得了一个兑换券！ ").color(mochaFlamingo)
                .append(Component.text("目前你拥有 ").color(mochaText))
                .append(Component.text("$count/$TICKETS_LIMIT").color(mochaYellow))
                .append(Component.text(" 个兑换券。").color(mochaText))
                .useFallbackColors()
        )
    }
}