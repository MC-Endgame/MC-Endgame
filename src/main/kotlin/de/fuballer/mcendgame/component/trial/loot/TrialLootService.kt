package de.fuballer.mcendgame.component.trial.loot

import de.fuballer.mcendgame.event.TrialEnemyDeathEvent
import de.fuballer.mcendgame.event.TrialWaveCompletedEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.ItemUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@Service
class TrialLootService : Listener {
    @EventHandler
    fun on(event: TrialEnemyDeathEvent) {
        val originalEvent = event.entityDeathEvent
        originalEvent.drops.clear()

        val instance = event.trialInstance
        for (item in EntityUtil.getEquipmentList(event.enemy)) {
            if (Random.nextDouble() > getItemDropProbability(item)) continue
            instance.currentLoot.add(item)
        }
    }

    @EventHandler
    fun on(event: TrialWaveCompletedEvent) {
        val world = event.world
        val instance = event.trialInstance

        val spawner = instance.spawner ?: return
        val dropLocation = spawner.location.add(0.0, 0.8, 0.0)

        for ((index, item) in instance.currentLoot.withIndex()) {
            val damagedItem = ItemUtil.setRandomDurability(item)
            SchedulingUtil.runTaskLater(index * TrialLootSettings.DELAY_BETWEEN_DROPS) {
                world.dropItemNaturally(dropLocation, damagedItem)
            }
        }

        instance.currentLoot = mutableListOf()
    }

    private fun getItemDropProbability(item: ItemStack): Double {
        val typeString = item.type.toString()

        if (typeString.contains("DIAMOND")) {
            return TrialLootSettings.DIAMOND_EQUIPMENT_DROP_PROBABILITY
        }
        if (typeString.contains("NETHERITE")) {
            return TrialLootSettings.NETHERITE_EQUIPMENT_DROP_PROBABILITY
        }
        if (typeString.contains("TRIDENT") || typeString.contains("MACE")) {
            return 0.0
        }

        return TrialLootSettings.EQUIPMENT_DROP_PROBABILITY
    }
}