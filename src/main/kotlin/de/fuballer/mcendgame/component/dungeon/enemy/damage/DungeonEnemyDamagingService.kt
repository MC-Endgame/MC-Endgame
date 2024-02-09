package de.fuballer.mcendgame.component.dungeon.enemy.damage

import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.technical.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.technical.extension.EntityExtension.setIsEnemy
import org.bukkit.entity.EvokerFangs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

@Component
class DungeonEnemyDamagingService : Listener {  // TODO
    //    @EventHandler(priority = EventPriority.LOWEST)
//    fun on(event: EntityDamageByEntityEvent) {
//        val damager = event.damager
//
//        if (WorldUtil.isNotDungeonWorld(damager.world)) return
//        if (!DungeonUtil.isEnemyOrEnemyProjectile(damager)) return
//
//        val target = event.entity
//        if (DungeonUtil.isEnemyOrEnemyProjectile(target)) {
//            event.isCancelled = true
//            return
//        }
//
//        event.damage *= 2.0 / 3.0 // worlds on hard mode multiply damage by 1,5x, so we revert that
//    }
//
    @EventHandler
    fun on(event: EntitySpawnEvent) {
        val entity = event.entity
        if (entity !is EvokerFangs) return

        val owner = entity.owner ?: return
        if (!owner.isEnemy()) return

        entity.setIsEnemy()
    }
}
