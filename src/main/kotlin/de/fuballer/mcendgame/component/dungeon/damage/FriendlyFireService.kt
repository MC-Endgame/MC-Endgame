package de.fuballer.mcendgame.component.dungeon.damage

import de.fuballer.mcendgame.component.damage.DamageCalculationEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.EntityExtension.isAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@Service
class FriendlyFireService : Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    fun on(event: DamageCalculationEvent) {
        if (!(event.isDungeonWorld || event.isTrialWorld)) return

        // disable player and ally friendly fire
        if ((event.damager is Player || event.damager.isAlly()) && (event.damaged is Player || event.damaged.isAlly())) {
            event.cancel()
            return
        }

        // disable enemy to enemy friendly fire
        if (event.damager.isEnemy() && event.damaged.isEnemy()) {
            event.cancel()
            return
        }
    }
}