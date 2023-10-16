@file:Suppress("DEPRECATION")

package dev.thatlukinhasguy.gondalkits.commands

import dev.thatlukinhasguy.gondalkits.Main
import org.bukkit.*
import org.bukkit.command.*
import org.bukkit.entity.Player
import org.bukkit.event.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.*
import org.bukkit.inventory.meta.ItemMeta
import dev.thatlukinhasguy.gondalkits.manager.KitManager
import dev.thatlukinhasguy.gondalkits.utils.MessageUtil

class KitsCmd(private val plugin: Main) : CommandExecutor, Listener, TabCompleter {

    private val title = MessageUtil(plugin).getTitle()

    companion object {
        private const val NO_KITS_MESSAGE = "Você não tem kits!"
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player: Player = event.whoClicked as? Player ?: return
        val clickedInventory: Inventory? = event.clickedInventory
        val clickedItem: ItemStack? = event.currentItem

        if (clickedInventory != null && clickedItem != null && event.view.title == title) {
            event.isCancelled = true

            if (clickedItem.type != Material.YELLOW_SHULKER_BOX) return

            val kitName = ChatColor.stripColor(clickedItem.itemMeta?.displayName ?: "") ?: return

            KitManager.givePlayerKit(plugin, player, kitName)

            player.playSound(player.location, Sound.UI_BUTTON_CLICK, 1.0f, 1.0f)
            player.closeInventory()
        }
    }

    private fun addItemToInventory(inventory: Inventory, material: Material, displayName: String, slot: Int) {
        val itemStack = ItemStack(material)
        val itemMeta: ItemMeta? = itemStack.itemMeta
        itemMeta?.setDisplayName(displayName)
        itemStack.itemMeta = itemMeta
        inventory.setItem(slot, itemStack)
    }

    private fun openKitGUI(player: Player) {
        val kitInventory: Inventory = Bukkit.createInventory(player, 27, title)
        val kits: Collection<String> = KitManager.getKitNames(player, plugin)

        if (kits.isNotEmpty()) {
            kits.forEachIndexed { i, kitName ->
                addItemToInventory(kitInventory, Material.YELLOW_SHULKER_BOX, "${ChatColor.GREEN}$kitName", i)
            }
        } else {
            addItemToInventory(kitInventory, Material.BARRIER, "${ChatColor.RED}$NO_KITS_MESSAGE", 13)
        }

        player.openInventory(kitInventory)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender is Player && command.name.equals("kits", ignoreCase = true)) {
            openKitGUI(sender)
            return true
        }
        return false
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return mutableListOf()
    }
}