@file:Suppress("DEPRECATION")

package tech.thatlukinhasguy.gondalkits.commands

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

class DeleteKitCmd(private val plugin: Main) : CommandExecutor, TabComplete(plugin) {

    override fun onCommand(player: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        if (player !is Player) {
            player.sendMessage("Apenas um jogador pode executar este comando.")
            return true
        }

        val prefix = MessageUtil(plugin).getPrefix()

        val kitFile = KitManager.getPlayerKitFile(player.name, plugin)

        if (args.isNotEmpty()) {
            val kitName = args[0]
            if (!KitManager.checkKit(player, kitName, plugin)) {
                player.sendMessage("$prefix O kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} não existe.")
                return false
            }
            KitManager.removeKit(player, kitName, plugin)
            plugin.saveKitConfig(kitFile)
            player.sendMessage("$prefix O kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} foi deletado com sucesso!")

        } else {
            player.sendMessage("$prefix Uso: ${ChatColor.GREEN}/deletekit <kit>")
            SoundUtil.sound(player, Sound.ENTITY_VILLAGER_TRADE)
        }
        return true
    }
}
