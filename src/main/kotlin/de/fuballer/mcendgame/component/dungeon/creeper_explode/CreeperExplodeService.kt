package de.fuballer.mcendgame.component.dungeon.creeper_explode

import de.fuballer.mcendgame.framework.stereotype.Service
import de.fuballer.mcendgame.util.WorldUtil
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.event.entity.ExplosionPrimeEvent

class CreeperExplodeService : Service {
    fun onEntityPrimed(event: ExplosionPrimeEvent) {
        val entity = event.entity

        if (entity.type != EntityType.CREEPER) return
        if (WorldUtil.isNotDungeonWorld(event.entity.world)) return

        val creeper = entity as LivingEntity
        creeper.activePotionEffects.forEach { potionEffect ->
            creeper.removePotionEffect(potionEffect.type)
        }
    }
}
