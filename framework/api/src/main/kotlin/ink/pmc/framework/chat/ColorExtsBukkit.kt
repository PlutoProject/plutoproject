package ink.pmc.framework.chat

import java.awt.Color

inline val Color.bukkitColor: org.bukkit.Color
    get() = org.bukkit.Color.fromARGB(this.alpha, this.red, this.green, this.blue)