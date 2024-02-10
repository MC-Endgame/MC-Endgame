package de.fuballer.mcendgame.component.dungeon.enemy.damage

import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.technical.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.technical.extension.EntityExtension.setIsEnemy
import org.bukkit.entity.EvokerFangs
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntitySpawnEvent

@Component
class DungeonEnemyDamagingService : Listener {
    @EventHandler
    fun on(event: EntitySpawnEvent) {
        val entity = event.entity
        if (entity !is EvokerFangs) return

        val owner = entity.owner ?: return
        if (!owner.isEnemy()) return

        entity.setIsEnemy()
    }
}
