@file:Suppress("DEPRECATION")

package tech.thatlukinhasguy.gondalkits.commands

import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import tech.thatlukinhasguy.gondalkits.Main
import tech.thatlukinhasguy.gondalkits.manager.KitManager
import tech.thatlukinhasguy.gondalkits.utils.MessageUtil
import tech.thatlukinhasguy.gondalkits.utils.SoundUtil

class CreateKitCmd(private val plugin: Main) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil(plugin).getPrefix()

        if (sender !is Player) {
            sender.sendMessage("Apenas um jogador pode executar este comando.")
            return true
        }

        if (args.isNotEmpty()) {
            val kitName = args[0]
            if (KitManager.checkKit(sender, kitName, plugin)) {
                sender.sendMessage("$prefix O kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} já existe.")
                return false
            }

            if (args[0].isEmpty()) {
                sender.sendMessage("$prefix O nome do kit é inválido.")
                return false
            }

            val kitLimit: Int = plugin.config.getInt("kit-limit", 26)

            if (KitManager.checkKitLimit(sender, plugin) > kitLimit) {
                sender.sendMessage("$prefix Você atingiu o limite de kits.")
                return false
            }

            KitManager.savePlayerInventory(sender, kitName, plugin)

            if (sender.inventory.isEmpty) return false

            sender.sendMessage("$prefix O kit ${ChatColor.GREEN}$kitName${ChatColor.WHITE} foi criado com sucesso!")

        } else {
            sender.sendMessage("$prefix Uso: ${ChatColor.GREEN}/createkit <kit>")
            SoundUtil.sound(sender, Sound.ENTITY_VILLAGER_TRADE)
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf("<nome>")
    }
}
