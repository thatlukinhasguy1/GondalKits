package tech.thatlukinhasguy.gondalkits.utils

import org.bukkit.Sound
import org.bukkit.entity.Player

object SoundUtil {
    fun sound(player: Player, sound: Sound?) {
        player.playSound(player.location, sound!!, 1f, 1f)
    }
}
