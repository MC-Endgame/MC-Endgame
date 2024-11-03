package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.event.TrialWaveSpawnedEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.EntityExtension.getDistance
import de.fuballer.mcendgame.util.extension.EntityExtension.isAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.WorldExtension.isTrialWorld
import org.bukkit.GameMode
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityTargetEvent

@Service
class TrialTargetingService : Listener {
    @EventHandler
    fun on(event: TrialWaveSpawnedEvent) {
        for (ally in event.allies) {
            val target = getNearestEntity(ally, event.enemies) ?: continue
            targetEntity(ally, target)
        }

        val alliesAndPlayers = mutableListOf<LivingEntity>().apply {
            addAll(event.allies)
            addAll(event.players.filter { it.gameMode == GameMode.SURVIVAL || it.gameMode == GameMode.ADVENTURE })
        }

        for (enemy in event.enemies) {
            val target = getNearestEntity(enemy, alliesAndPlayers) ?: continue
            targetEntity(enemy, target)
        }
    }

    @EventHandler
    fun on(event: EntityTargetEvent) {
        val creature = event.entity as? Creature ?: return
        if (!creature.world.isTrialWorld()) return

        if (event.reason != EntityTargetEvent.TargetReason.CLOSEST_ENTITY) return event.cancel()
        if (creature.type != EntityType.PIGLIN_BRUTE) return event.cancel()

        val newTarget = event.target ?: return event.cancel()
        if (!isOpponent(creature, newTarget)) return event.cancel()
    }

    @EventHandler
    fun on(event: EntityDeathEvent) {
        val entity = event.entity
        val world = entity.world
        if (!world.isTrialWorld()) return

        val livingEntities = world.livingEntities
        val creatures = livingEntities.filterIsInstance<Creature>()

        val targetingCreatures = creatures.filter { it.target == entity }
        if (targetingCreatures.isEmpty()) return

        val newTargets =
            if (targetingCreatures[0].isAlly())
                creatures.filter { it.isEnemy() }
            else
                livingEntities.filter { it.isAlly() || (it is Player && (it.gameMode == GameMode.SURVIVAL || it.gameMode == GameMode.ADVENTURE)) }

        for (targetingCreature in targetingCreatures) {
            val nearestTarget = getNearestEntity(targetingCreature, newTargets)
            targetEntity(targetingCreature, nearestTarget)
        }
    }

    private fun isOpponent(entity: Entity, target: Entity): Boolean {
        if (entity.isAlly()) return target.isEnemy()

        return target.isAlly() || target is Player
    }

    private fun targetEntity(entity: Creature, target: LivingEntity?) {
        entity.target = target
        if (target == null) return

        val piglinBrute = entity as? PiglinBrute ?: return
        piglinBrute.damage(0.0, target)
    }

    private fun getNearestEntity(entity: Entity, entities: List<LivingEntity>) = entities.minByOrNull { it.getDistance(entity) }
}