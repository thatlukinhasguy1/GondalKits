package tech.thatlukinhasguy.gondalkits.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import tech.thatlukinhasguy.gondalkits.Main
import tech.thatlukinhasguy.gondalkits.manager.KitManager

open class TabComplete(private val plugin: Main) : TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        val player = sender as? Player ?: return emptyList()
        val completions = ArrayList(KitManager.getKitNames(player, plugin))

        if (args.size == 1) {
            return completions.filter { s: String -> s.startsWith(args[0]) }
        }

        return emptyList()
    }
}