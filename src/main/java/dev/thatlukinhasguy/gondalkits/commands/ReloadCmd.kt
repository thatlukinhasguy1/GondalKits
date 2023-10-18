package dev.thatlukinhasguy.gondalkits.commands

import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import java.io.IOException

class ReloadCmd(private val plugin: Main) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<out String>): Boolean {
        val prefix = MessageUtil(plugin).getPrefix()

        try {
            plugin.reloadConfig()
            sender.sendMessage("$prefix The plugin was reloaded successfully!")
        } catch (e: IOException) {
            sender.sendMessage("$prefix The plugin was reloaded with an error. Check the console for more information.")
            e.printStackTrace()
            return false
        }

        return true
    }
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return mutableListOf("reload")
        }
        return mutableListOf()
    }
}