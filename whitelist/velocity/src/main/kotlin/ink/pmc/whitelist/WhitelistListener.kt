package ink.pmc.whitelist

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.PreLoginEvent
import ink.pmc.advkt.component.component
import ink.pmc.advkt.component.newline
import ink.pmc.advkt.component.text
import ink.pmc.utils.visual.mochaMaroon
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

@Suppress("UNUSED")
object WhitelistListener : KoinComponent {

    private val repo by inject<WhitelistRepository>()

    @Subscribe
    suspend fun PreLoginEvent.e() {
        uniqueId?.let {
            if (repo.hasById(it)) return
        }
        result = PreLoginEvent.PreLoginComponentResult.denied(component {
            text("此 ID 尚未获得白名单") with mochaMaroon
            newline()
            text("若已通过审核，请联系当日的审核员添加") with mochaMaroon
        })
    }

}