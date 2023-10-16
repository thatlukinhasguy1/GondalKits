@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.manager.KitManager
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import dev.thatlukinhasguy.gondalkits.utils.SoundUtil

class KitCmd(private val plugin: Main) : CommandExecutor, TabComplete(plugin) {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil(plugin).getPrefix()

        if (sender !is Player) {
            sender.sendMessage("$prefix Only a player can execute this command.")
            return true
        }

        if (args.isNotEmpty()) {
            val kitName = args[0]

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if (!KitManager.checkKit(sender, kitName, plugin)) {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        sender.sendMessage("$prefix The kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} does not exist.")
                    })
                } else {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        KitManager.givePlayerKit(plugin, sender, kitName)
                    })
                }
            })

        } else {
            sender.sendMessage("$prefix Usage: ${ChatColor.GREEN}/kit <kit>")

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                SoundUtil.sound(sender, Sound.ENTITY_VILLAGER_TRADE)
            })
        }
        return true
    }
}
