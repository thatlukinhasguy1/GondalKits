@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.utils

import org.bukkit.ChatColor
import dev.thatlukinhasguy.gondalkits.Main

class MessageUtil(private val plugin: Main) {
    fun getPrefix(): String {
        val plugin: Main = plugin
        return if (plugin.getConfig().getString("strings.prefix")!!.isNotEmpty()) ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("strings.prefix") + "&f"
        ) else ChatColor.BLUE.toString() + "Gondal" + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE
    }
    fun getTitle(): String {
        val plugin: Main = plugin
        return if (plugin.getConfig().getString("strings.kits-gui-title")!!.isNotEmpty()) ChatColor.translateAlternateColorCodes(
                '&',
                plugin.getConfig().getString("strings.kits-gui-title")!!
        ) else ChatColor.BLACK.toString() + "Your Kits"
    }
}