package dev.thatlukinhasguy.gondalkits

import dev.thatlukinhasguy.gondalkits.commands.CreateKitCmd
import dev.thatlukinhasguy.gondalkits.commands.DeleteKitCmd
import dev.thatlukinhasguy.gondalkits.commands.KitCmd
import dev.thatlukinhasguy.gondalkits.commands.KitsCmd
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException

class Main : JavaPlugin() {

    override fun onEnable() {
        setupConfig()
        setupCommands()
        setupListeners()
        logger.info("+----------------------------------------------+")
        logger.info("|     GondalKits initialized with success!     |")
        logger.info("+----------------------------------------------+")
    }

    override fun onDisable() {
        logger.info("+----------------------------------------------+")
        logger.info("|      GondalKits disabled with success!       |")
        logger.info("+----------------------------------------------+")
    }

    private fun setupConfig() {
        saveDefaultConfig()
    }

    private fun setupCommands() {
        val pluginCommands = mapOf(
            "createkit" to CreateKitCmd(this),
            "deletekit" to DeleteKitCmd(this),
            "kits" to KitsCmd(this),
            "kit" to KitCmd(this)
        )

        pluginCommands.forEach { (commandName, executor) ->
            getCommand(commandName)?.setExecutor(executor)
        }
    }

    private fun setupListeners() {
        val listeners = listOf(KitsCmd(this))

        val pluginManager: PluginManager = server.pluginManager
        listeners.forEach { listener ->
            pluginManager.registerEvents(listener, this)
        }
    }

    fun saveKitConfig(file: File) {
        try {
            val kitConfig = YamlConfiguration.loadConfiguration(file)
            kitConfig.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}