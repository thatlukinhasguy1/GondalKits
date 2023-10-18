@file:Suppress("DEPRECATION", "UNCHECKED_CAST")

package dev.thatlukinhasguy.gondalkits.manager

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import dev.thatlukinhasguy.gondalkits.utils.SoundUtil
import java.io.File
import java.nio.file.Files

object KitManager {

    private val objectMapper = jacksonObjectMapper()

    fun getPlayerKitFile(playerName: String, plugin: Main): File {
        val kitFile = File(plugin.dataFolder, "kits/$playerName.json")
        if (!kitFile.exists()) {
            kitFile.parentFile.mkdirs()
            Files.createFile(kitFile.toPath())
        }
        return kitFile
    }

    private fun loadKitConfig(playerName: String, plugin: Main): Map<String, Any> {
        val kitFile = getPlayerKitFile(playerName, plugin)
        val fileContents = kitFile.readText()
        return if (fileContents.isNotEmpty()) {
            val typeReference = object : TypeReference<Map<String, Any>>() {}
            objectMapper.readValue(fileContents, typeReference)
        } else {
            emptyMap()
        }
    }


    fun checkKit(player: Player, kit: String, plugin: Main): Boolean {
        val kitConfig = loadKitConfig(player.name, plugin)["kit"] as? Map<*, *>
        return kitConfig?.get(kit) != null
    }

    fun checkKitLimit(player: Player, plugin: Main): Int {
        val kitConfig = loadKitConfig(player.name, plugin)["kit"] as? Map<*, *>
        return kitConfig?.size ?: 0
    }

    fun savePlayerInventory(player: Player, kitName: String, plugin: Main) {
        val inventory = player.inventory.contents.map { it ?: ItemStack(Material.AIR) }

        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin).toMutableMap()

        if (!kitConfig.containsKey("kit")) {
            kitConfig["kit"] = mutableMapOf<String, Any>()
        }

        val kitObject = kitConfig["kit"] as MutableMap<String, Any>
        val kitData = mutableMapOf<String, Any>()
        val itemsArray = inventory.map { it.serialize() }
        kitData["items"] = itemsArray
        kitObject[kitName] = kitData
        kitConfig["kit"] = kitObject

        object : BukkitRunnable() {
            override fun run() {
                kitFile.writeText(objectMapper.writeValueAsString(kitConfig))
            }
        }.runTaskAsynchronously(plugin)
    }

    fun removeKit(player: Player, kitName: String, plugin: Main) {
        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin).toMutableMap()

        (kitConfig["kit"] as MutableMap<String, Any>).remove(kitName)

        object : BukkitRunnable() {
            override fun run() {
                val fileWriter = objectMapper.writerWithDefaultPrettyPrinter()
                kitFile.writeText(fileWriter.writeValueAsString(kitConfig))
            }
        }.runTaskAsynchronously(plugin)
    }

    fun givePlayerKit(plugin: Main, player: Player, kitName: String) {
        val kitConfig = loadKitConfig(player.name, plugin)

        val prefix = MessageUtil(plugin).getPrefix()
        if (!kitConfig.containsKey("kit") || kitConfig["kit"] !is Map<*, *>) return

        val kitObject = kitConfig["kit"] as Map<*, *>
        val kitData = kitObject[kitName] as? Map<*, *> ?: return
        val itemList = kitData["items"] as List<*>

        val inventory = player.inventory
        inventory.clear()

        val announceLoadKit = plugin.config.getBoolean("kitConfig.announce-when-kit-is-loaded")

        for (i in 0 until inventory.size) {
            if (i >= itemList.size) break
            val item = itemList[i] as? Map<String, Any> ?: continue
            val itemStack = ItemStack.deserialize(item)
            inventory.setItem(i, itemStack)
        }

        player.sendActionBar("${ChatColor.WHITE}The kit ${ChatColor.GREEN}'$kitName'${ChatColor.WHITE} was equipped!")
        SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP)

        if (announceLoadKit) {
            Bukkit.getOnlinePlayers().forEach { p ->
                p.sendMessage("$prefix ${ChatColor.GREEN}${player.name}${ChatColor.WHITE} loaded a kit!")
            }
        }
    }

    fun getKitNames(player: Player, plugin: Main): List<String> {
        val kitConfig = loadKitConfig(player.name, plugin)["kit"] as? Map<*, *>
        return kitConfig?.keys?.mapNotNull { it as? String } ?: emptyList()
    }
}
