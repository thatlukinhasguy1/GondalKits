@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.commands

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.manager.KitManager
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import dev.thatlukinhasguy.gondalkits.utils.SoundUtil

class CreateKitCmd(private val plugin: Main) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil(plugin).getPrefix()

        if (sender !is Player) {
            sender.sendMessage("$prefix Only a player can execute this command.")
            return true
        }

        if (args.isNotEmpty()) {
            val kitName = args[0]
            if (KitManager.checkKit(sender, kitName, plugin)) {
                sender.sendMessage("$prefix The kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} already exists.")
                return false
            }

            if (args[0].isEmpty()) {
                sender.sendMessage("$prefix The kit name is invalid.")
                return false
            }

            val configKitLimit: Int = plugin.config.getInt("kitConfig.kit-limit")

            val kitLimit: Int = if (configKitLimit <= 26) {
                plugin.config.getInt("kitConfig.kit-limit")
            } else {
                26
            }

            if (KitManager.checkKitLimit(sender, plugin) > kitLimit) {
                sender.sendMessage("$prefix You have reached the kit limit.")
                return false
            }

            KitManager.savePlayerInventory(sender, kitName, plugin)

            if (sender.inventory.isEmpty) return false

            sender.sendMessage("$prefix The kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} has been created successfully!")

        } else {
            sender.sendMessage("$prefix Usage: ${ChatColor.GREEN}/createkit <kit>")
            SoundUtil.sound(sender, Sound.ENTITY_VILLAGER_TRADE)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf("<value>")
    }
}
