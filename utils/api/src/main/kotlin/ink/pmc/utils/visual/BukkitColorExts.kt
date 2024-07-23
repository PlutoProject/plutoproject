package ink.pmc.utils.visual

import java.awt.Color

val Color.bukkitColor: org.bukkit.Color
    get() = org.bukkit.Color.fromARGB(this.alpha, this.red, this.green, this.blue)