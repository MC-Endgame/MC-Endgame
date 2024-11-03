package de.fuballer.mcendgame.component.trial.loot

import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceEntity
import de.fuballer.mcendgame.framework.annotation.Service
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.inventory.ItemStack

@Service
class TrialLootService {
    fun dropLoot(world: World, instanceEntity: TrialInstanceEntity) {
        val spawner = instanceEntity.spawner ?: return
        world.dropItemNaturally(spawner.location, ItemStack(Material.DIAMOND))
    }
}