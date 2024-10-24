package ink.pmc.exchange

import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.member.api.Member
import ink.pmc.framework.utils.bedrock.useFallbackColors
import ink.pmc.framework.utils.multiplaform.item.exts.keyed
import ink.pmc.framework.utils.visual.mochaFlamingo
import ink.pmc.framework.utils.visual.mochaMaroon
import ink.pmc.framework.utils.visual.mochaText
import ink.pmc.framework.utils.visual.mochaYellow
import ink.pmc.framework.visual.toast.Toast
import ink.pmc.framework.visual.toast.dsl.toast
import net.kyori.adventure.text.Component
import org.bukkit.Material

fun Member.ticketGrantToast(): Toast {
    val count = exchangeService.tickets(this)
    return toast {
        icon(Material.PAPER.keyed)
        message {
            text("你幸运地获得了一个兑换券") with mochaFlamingo
            newline()
            text("目前你拥有 ") with mochaText
            text("$count/$TICKETS_LIMIT") with mochaYellow
            text(" 个兑换券") with mochaText
        }
    }
}

fun Member.ticketGrantToastBe(): Toast {
    val count = exchangeService.tickets(this)
    return toast {
        icon(Material.PAPER.keyed)
        message(
            component {
                text("你幸运地获得了一个兑换券！ ") with mochaFlamingo
                text("目前你拥有 ") with mochaText
                text("$count/$TICKETS_LIMIT") with mochaYellow
                text(" 个兑换券。") with mochaText
            }.useFallbackColors()
        )
    }
}