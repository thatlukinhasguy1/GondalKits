@file:Suppress("DEPRECATION")

package tech.thatlukinhasguy.gondalkits.commands

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import tech.thatlukinhasguy.gondalkits.Main
import tech.thatlukinhasguy.gondalkits.manager.KitManager
import tech.thatlukinhasguy.gondalkits.utils.MessageUtil
import tech.thatlukinhasguy.gondalkits.utils.SoundUtil

class KitCmd(private val plugin: Main) : CommandExecutor, TabComplete(plugin) {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Apenas um jogador pode executar este comando.")
            return true
        }

        val prefix = MessageUtil(plugin).getPrefix()

        if (args.isNotEmpty()) {
            val kitName = args[0]

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                if (!KitManager.checkKit(sender, kitName, plugin)) {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        sender.sendMessage("$prefix O kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} não existe.")
                    })
                } else {
                    Bukkit.getScheduler().runTask(plugin, Runnable {
                        KitManager.givePlayerKit(plugin, sender, kitName, false)
                    })
                }
            })

        } else {
            sender.sendMessage("$prefix Uso: ${ChatColor.GREEN}/kit <kit>")

            Bukkit.getScheduler().runTaskAsynchronously(plugin, Runnable {
                SoundUtil.sound(sender, Sound.ENTITY_VILLAGER_TRADE)
            })
        }
        return true
    }
}
