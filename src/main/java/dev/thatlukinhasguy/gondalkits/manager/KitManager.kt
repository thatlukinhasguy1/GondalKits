@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.manager

import org.bukkit.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import dev.thatlukinhasguy.gondalkits.Main
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil
import dev.thatlukinhasguy.gondalkits.utils.SoundUtil
import java.io.File

object KitManager {

    fun getPlayerKitFile(playerName: String, plugin: Main): File {
        val kitFile = File(plugin.dataFolder, "kits/$playerName.yml")
        if (!kitFile.exists()) kitFile.parentFile.mkdirs() && kitFile.createNewFile()
        return kitFile
    }

    private fun loadKitConfig(playerName: String, plugin: Main) = YamlConfiguration.loadConfiguration(getPlayerKitFile(playerName, plugin))

    fun checkKit(player: Player, kit: String, plugin: Main) = loadKitConfig(player.name, plugin).contains("kit.$kit.items")

    fun checkKitLimit(player: Player, plugin: Main) = try {
        loadKitConfig(player.name, plugin).getConfigurationSection("kit")?.getKeys(false)?.size ?: 0
    } catch (e: NullPointerException) {
        0
    }

    fun savePlayerInventory(player: Player, kitName: String, plugin: Main) {
        val inventory = player.inventory.contents
        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin)

        kitConfig["kit.$kitName.items"] = inventory

        object : BukkitRunnable() {
            override fun run() {
                kitConfig.save(kitFile)
            }
        }.runTaskAsynchronously(plugin)
    }

    fun removeKit(player: Player, kitName: String, plugin: Main) {
        val kitFile = getPlayerKitFile(player.name, plugin)
        val kitConfig = loadKitConfig(player.name, plugin)

        kitConfig["kit.$kitName"] = null

        object : BukkitRunnable() {
            override fun run() {
                kitConfig.save(kitFile)
            }
        }.runTaskAsynchronously(plugin)
    }

    fun givePlayerKit(plugin: Main, player: Player, kitName: String) {
        val kitConfig = loadKitConfig(player.name, plugin)

        val prefix = MessageUtil(plugin).getPrefix()
        if (!kitConfig.contains("kit.$kitName.items")) return

        val inventory = player.inventory
        val itemList = kitConfig.getList("kit.$kitName.items", emptyList<ItemStack>()) ?: return

        inventory.clear()

        val announceLoadKit = plugin.config.getBoolean("kitConfig.announce-when-kit-is-loaded")

        for (i in 0 until inventory.size) {
            if (i >= itemList.size) break
            val item = itemList[i] as? ItemStack
            if (item != null) inventory.setItem(i, item)
        }

        player.sendActionBar("${ChatColor.WHITE}The kit ${ChatColor.GREEN}'$kitName'${ChatColor.WHITE} was equipped!")
        SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP)

        if (announceLoadKit) {
            Bukkit.getOnlinePlayers().forEach { p ->
                p.sendMessage("$prefix ${ChatColor.GREEN}${player.name}${ChatColor.WHITE} loaded a kit!")
            }
            return
        }
        return
    }

    fun getKitNames(player: Player, plugin: Main) = loadKitConfig(player.name, plugin).getConfigurationSection("kit")?.getKeys(false) ?: emptyList()
}