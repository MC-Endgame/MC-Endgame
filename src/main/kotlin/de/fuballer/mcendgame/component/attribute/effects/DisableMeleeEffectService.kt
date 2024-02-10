package de.fuballer.mcendgame.component.attribute.effects

import de.fuballer.mcendgame.component.attribute.AttributeType
import de.fuballer.mcendgame.component.damage.DamageCalculationEvent
import de.fuballer.mcendgame.framework.annotation.Component
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent

@Component
class DisableMeleeEffectService {
    @EventHandler(ignoreCancelled = true)
    fun on(event: DamageCalculationEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.PROJECTILE) return

        if (!event.customDamagerAttributes.containsKey(AttributeType.DISABLE_MELEE)) return

        event.isCancelled = true
    }
}