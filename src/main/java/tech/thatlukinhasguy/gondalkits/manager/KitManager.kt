@file:Suppress("DEPRECATION")

package tech.thatlukinhasguy.gondalkits.manager

import org.bukkit.*
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import tech.thatlukinhasguy.gondalkits.Main
import tech.thatlukinhasguy.gondalkits.utils.*
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

    fun givePlayerKit(plugin: Main, player: Player, kitName: String, refill: Boolean) {
        val kitConfig = loadKitConfig(player.name, plugin)

        val prefix = MessageUtil(plugin).getPrefix()
        if (!kitConfig.contains("kit.$kitName.items")) return

        val inventory = player.inventory
        val itemList = kitConfig.getList("kit.$kitName.items", emptyList<ItemStack>()) ?: return

        inventory.clear()

        for (i in 0 until inventory.size) {
            if (i >= itemList.size) break
            val item = itemList[i] as? ItemStack
            if (item != null) inventory.setItem(i, item)
        }

        if (refill) {
            player.sendTitle("${ChatColor.BLUE} Refill!", "${ChatColor.WHITE} Você recebeu um refill por conseguir ${ChatColor.GREEN} 3 kills consecutivas!", 2, 50, 2)
            SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP)
            return
        }

        player.sendActionBar("${ChatColor.WHITE}O kit ${ChatColor.GREEN}'$kitName'${ChatColor.WHITE} foi equipado!")
        SoundUtil.sound(player, Sound.ENTITY_ENDER_DRAGON_FLAP)

        Bukkit.getOnlinePlayers().forEach { p ->
            p.sendMessage("$prefix ${ChatColor.GREEN}${player.name}${ChatColor.WHITE} carregou um kit!")
        }
    }

    fun getKitNames(player: Player, plugin: Main) = loadKitConfig(player.name, plugin).getConfigurationSection("kit")?.getKeys(false) ?: emptyList()
}