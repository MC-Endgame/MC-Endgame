package de.fuballer.mcendgame.component.custom_entity.types.dryad

import de.fuballer.mcendgame.component.damage.DamageCalculationEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.extension.EntityExtension.getCustomEntityType
import de.fuballer.mcendgame.util.extension.EntityExtension.getNearest
import de.fuballer.mcendgame.util.extension.EntityExtension.isAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.isEnemy
import org.bukkit.Color
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

@Service
class DryadService : Listener {
    @EventHandler(priority = EventPriority.HIGH)
    fun on(event: DamageCalculationEvent) {
        val damager = event.damager
        if (damager.getCustomEntityType() != DryadEntityType) return

        heal(damager, event.getFinalDamage())
        spawnHealParticle(damager)

        event.lessDamage.add(1.0)
    }

    private fun heal(entity: LivingEntity, heal: Double) {
        if (entity.isAlly()) return healNearbyAlly(entity, heal)
        healNearbyEnemy(entity, heal)
    }

    private fun healNearbyAlly(entity: LivingEntity, heal: Double) {
        val allies = entity.getNearbyEntities(DryadSettings.HEAL_RANGE, DryadSettings.HEAL_RANGE, DryadSettings.HEAL_RANGE)
            .filter { it.isAlly() || it is Player }
            .filterIsInstance<LivingEntity>()
        healNearest(entity, allies, heal)
    }

    private fun healNearbyEnemy(entity: LivingEntity, heal: Double) {
        val enemies = entity.getNearbyEntities(DryadSettings.HEAL_RANGE, DryadSettings.HEAL_RANGE, DryadSettings.HEAL_RANGE)
            .filter { it.isEnemy() }
            .filterIsInstance<LivingEntity>()
        healNearest(entity, enemies, heal)
    }

    private fun healNearest(entity: LivingEntity, targets: List<LivingEntity>, heal: Double) {
        val chosenAlly = entity.getNearest(targets) as? LivingEntity ?: return
        EntityUtil.heal(chosenAlly, heal)
        spawnHealParticle(chosenAlly)
    }

    private fun spawnHealParticle(entity: Entity) {
        val location = entity.location
        val dustOptions = Particle.DustOptions(Color.fromRGB(50, 255, 50), 1.0f)

        entity.world.spawnParticle(
            Particle.DUST,
            location.x, location.y + 1, location.z,
            15, 0.2, 0.2, 0.2, 0.01, dustOptions
        )
    }
}