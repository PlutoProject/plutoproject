package ink.pmc.common.misc.api

import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun Player.sit(location: Location) {
    MiscAPI.instance.sitManager.sit(this, location)
}

fun Player.stand() {
    MiscAPI.instance.sitManager.stand(this)
}

var Player.isSitting: Boolean
    get() = MiscAPI.instance.sitManager.isSitting(this)
    set(value) {
        if (!value) {
            this.stand()
        }
    }

val Player.seat: Entity?
    get() = MiscAPI.instance.sitManager.getSeat(this)