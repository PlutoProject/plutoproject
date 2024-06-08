package ink.pmc.misc.api.sit

import ink.pmc.misc.api.MiscAPI
import org.bukkit.Location
import org.bukkit.Server
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

fun Player.sit(location: Location) {
    MiscAPI.instance.sitManager.sit(this, location)
}

val Player.seat: Entity?
    get() = MiscAPI.instance.sitManager.getSeat(this)
val Player.sitLocation: Location?
    get() = MiscAPI.instance.sitManager.getSitLocation(this)
var Player.isSitting: Boolean
    get() = MiscAPI.instance.sitManager.isSitting(this)
    set(value) {
        if (!value) {
            this.stand()
        }
    }

fun Player.stand() {
    MiscAPI.instance.sitManager.stand(this)
}

val Location.sitter: Player?
    get() = MiscAPI.instance.sitManager.getSitterByLocation(this)

val Entity.sitter: Player?
    get() = MiscAPI.instance.sitManager.getSitterBySeat(this)

val Entity.isSeat: Boolean
    get() = MiscAPI.instance.sitManager.isSeat(this)

val Location.isSitLocation: Boolean
    get() = MiscAPI.instance.sitManager.isSitLocation(this)

fun Server.standAll() {
    MiscAPI.instance.sitManager.standAll()
}