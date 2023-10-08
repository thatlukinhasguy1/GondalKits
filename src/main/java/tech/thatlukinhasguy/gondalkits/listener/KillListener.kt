package tech.thatlukinhasguy.gondalkits.listener

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.scheduler.BukkitRunnable
import tech.thatlukinhasguy.gondalkits.Main
import tech.thatlukinhasguy.gondalkits.manager.KitManager
import java.lang.ref.WeakReference
import java.util.*

class KillListener(private val plugin: Main) : Listener {
    private val killCount = HashMap<WeakReference<Player>, Int>()
    private val commandHistory = mutableMapOf<Player, Queue<String>>()

    @EventHandler
    fun onDeath(event: PlayerDeathEvent) {
        val killer = event.entity.killer
        killer?.let {
            val cmd = commandHistory[it]?.peek()
            val count = killCount.getOrPut(WeakReference(it)) { 0 } + 1
            val killStreakMax = plugin.config.getInt("killstreak-till-refill")
            killCount[WeakReference(it)] = count
            if (count % killStreakMax == 0) {
                object : BukkitRunnable() {
                    override fun run() {
                        if (cmd?.isNotEmpty()!!) {
                            KitManager.givePlayerKit(plugin = plugin, player = killer, kitName = cmd, true)
                        }
                    }
                }.runTaskAsynchronously(plugin)
                killCount[WeakReference(it)] = 0
            }
        }
    }

    @EventHandler
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        val (player, command) = event.player to event.message
        val history = commandHistory.getOrPut(player) { LinkedList() }
        history.offer(command)
        if (history.size > 10) history.poll()
    }
}
