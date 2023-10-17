@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.manager

import org.bukkit.*
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import dev.thatlukinhasguy.gondalkits.utils.SoundUtil
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.nio.file.Files

object KitManager {

    fun getPlayerKitFile(playerName: String, plugin: Main): File {
        val kitFile = File(plugin.dataFolder, "kits/$playerName.json")
        if (!kitFile.exists()) {
            kitFile.parentFile.mkdirs()
            Files.createFile(kitFile.toPath())
        }
        return kitFile
    }

    private fun loadKitConfig(playerName: String, plugin: Main): JSONObject {
        val fileContents = getPlayerKitFile(playerName, plugin).readText()
        return if (fileContents.isNotEmpty()) JSONObject(fileContents) else JSONObject()
    }

    fun checkKit(player: Player, kit: String, plugin: Main): Boolean {
        val kitConfig = loadKitConfig(player.name, plugin).optJSONObject("kit")
        return kitConfig?.optJSONObject(kit)?.has("items") ?: false
    }

    fun checkKitLimit(player: Player, plugin: Main): Int {
        val kitConfig = loadKitConfig(player.name, plugin).optJSONObject("kit")
        return kitConfig?.length() ?: 0
    }

    fun savePlayerInventory(player: Player, kitName: String, plugin: Main) {
        val inventory = player.inventory.contents.map { it ?: ItemStack(Material.AIR) }

        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin)

        if (!kitConfig.has("kit")) {
            kitConfig.put("kit", JSONObject())
        }

        val kitObject = kitConfig.getJSONObject("kit")
        val kitData = kitObject.optJSONObject(kitName) ?: JSONObject()
        val itemsArray = JSONArray()
        inventory.forEach { itemsArray.put(it.serialize()) }
        kitData.put("items", itemsArray)
        kitObject.put(kitName, kitData)
        kitConfig.put("kit", kitObject)

        object : BukkitRunnable() {
            override fun run() {
                kitFile.writeText(kitConfig.toString())
            }
        }.runTaskAsynchronously(plugin)
    }

    fun removeKit(player: Player, kitName: String, plugin: Main) {
        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin)

        kitConfig.getJSONObject("kit").remove(kitName)

        object : BukkitRunnable() {
            override fun run() {
                kitFile.writeText(kitConfig.toString(4))
            }
        }.runTaskAsynchronously(plugin)
    }

    fun givePlayerKit(plugin: Main, player: Player, kitName: String) {
        val kitConfig = loadKitConfig(player.name, plugin)

        val prefix = MessageUtil(plugin).getPrefix()
        if (!kitConfig.has("kit") || !kitConfig.getJSONObject("kit").has(kitName) || !kitConfig.getJSONObject("kit").getJSONObject(kitName).has("items")) return

        val inventory = player.inventory
        val itemList = kitConfig.getJSONObject("kit").getJSONObject(kitName).getJSONArray("items")

        inventory.clear()

        val announceLoadKit = plugin.config.getBoolean("kitConfig.announce-when-kit-is-loaded")

        for (i in 0 until inventory.size) {
            if (i >= itemList.length()) break
            val item = itemList.getJSONObject(i).toString()
            if (item.isNotBlank()) inventory.setItem(i, ItemStack.deserialize(JSONObject(item).toMap()))
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
        val kitConfig = loadKitConfig(player.name, plugin).optJSONObject("kit")
        return kitConfig?.keys()?.asSequence()?.toList() ?: emptyList()
    }
}