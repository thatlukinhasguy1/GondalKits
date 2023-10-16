@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.commands

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

class DeleteKitCmd(private val plugin: Main) : CommandExecutor, TabComplete(plugin) {

    override fun onCommand(player: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil(plugin).getPrefix()

        if (player !is Player) {
            player.sendMessage("$prefix Only a player can execute this command.")
            return true
        }

        val kitFile = KitManager.getPlayerKitFile(player.name, plugin)

        if (args.isNotEmpty()) {
            val kitName = args[0]
            if (!KitManager.checkKit(player, kitName, plugin)) {
                player.sendMessage("$prefix The kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} does not exist.")
                return false
            }
            KitManager.removeKit(player, kitName, plugin)
            plugin.saveKitConfig(kitFile)
            player.sendMessage("$prefix The kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} has been successfully deleted!")

        } else {
            player.sendMessage("$prefix Usage: ${ChatColor.GREEN}/deletekit <kit>")
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE)
        }
        return true
    }
}