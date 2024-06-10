package de.fuballer.mcendgame.component.dungeon.tweaks

import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.util.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.WorldExtension.isDungeonWorld
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause

private val IGNORED_DAMAGE = listOf(
    DamageCause.CONTACT,
    DamageCause.SUFFOCATION,
    DamageCause.FALL,
    DamageCause.DROWNING,
    DamageCause.HOT_FLOOR,
    DamageCause.DRYOUT,
    DamageCause.FREEZE,
)

@Component
class EntitySuicideService : Listener {
    @EventHandler
    fun on(event: EntityDamageEvent) {
        if (!event.entity.world.isDungeonWorld()) return
        if (!event.entity.isEnemy()) return

        if (!IGNORED_DAMAGE.contains(event.cause)) return

        event.cancel()
    }
}