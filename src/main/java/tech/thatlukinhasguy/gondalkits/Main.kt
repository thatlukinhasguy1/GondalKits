package tech.thatlukinhasguy.gondalkits

import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.PluginManager
import org.bukkit.plugin.java.JavaPlugin
import tech.thatlukinhasguy.gondalkits.commands.*
import tech.thatlukinhasguy.gondalkits.listener.KillListener
import java.io.File
import java.io.IOException

class Main : JavaPlugin() {

    override fun onEnable() {
        setupConfig()
        setupCommands()
        setupListeners()
        logger.info("+----------------------------------------------+")
        logger.info("|     GondalKits inicializado com sucesso!     |")
        logger.info("+----------------------------------------------+")
    }

    override fun onDisable() {
        logger.info("+----------------------------------------------+")
        logger.info("|     GondalKits desabilitado com sucesso!     |")
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
        val listeners = listOf(KillListener(this), KitsCmd(this))

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