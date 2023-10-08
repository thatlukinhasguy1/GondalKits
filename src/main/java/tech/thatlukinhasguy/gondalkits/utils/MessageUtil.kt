@file:Suppress("DEPRECATION")

package tech.thatlukinhasguy.gondalkits.utils

import org.bukkit.ChatColor
import tech.thatlukinhasguy.gondalkits.Main

class MessageUtil(private val plugin: Main) {
    fun getPrefix(): String {
        val plugin: Main = plugin
        return if (plugin.getConfig().getString("prefix")!!.isNotEmpty()) ChatColor.translateAlternateColorCodes(
            '&',
            plugin.getConfig().getString("prefix") + "&f"
        ) else ChatColor.BLUE.toString() + "Gondal" + ChatColor.DARK_GRAY + " » " + ChatColor.WHITE
    }
    fun getTitle(): String {
        val plugin: Main = plugin
        return if (plugin.getConfig().getString("kits-gui-title")!!.isNotEmpty()) ChatColor.translateAlternateColorCodes(
                '&',
                plugin.getConfig().getString("kits-gui-title")!!
        ) else ChatColor.BLACK.toString() + "Seus Kits"
    }
}