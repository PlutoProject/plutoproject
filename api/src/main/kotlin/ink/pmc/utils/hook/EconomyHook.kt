package ink.pmc.utils.hook

import net.milkbowl.vault.economy.Economy
import org.bukkit.Bukkit

class EconomyHook {
    val instance = Bukkit.getServicesManager().getRegistration(Economy::class.java)!!.provider
}