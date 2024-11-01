package de.fuballer.mcendgame.component.trial.generation.world

import de.fuballer.mcendgame.component.world.ManagedWorldService
import de.fuballer.mcendgame.component.world.WorldSettings
import de.fuballer.mcendgame.component.world.db.trial.ManagedTrialWorldEntity
import de.fuballer.mcendgame.component.world.db.trial.ManagedTrialWorldRepository
import de.fuballer.mcendgame.framework.annotation.Service
import org.bukkit.World
import java.util.*

@Service
class TrialWorldService(
    private val managedWorldService: ManagedWorldService,
    private val managedTrialWorldRepo: ManagedTrialWorldRepository,
) {
    fun createTrialWorld(
        seed: Long
    ): World {
        val name = "${WorldSettings.WORLD_PREFIX}${TrialWorldSettings.TRIAL_WORLD_PREFIX}${UUID.randomUUID()}"
        val world = managedWorldService.createWorld(seed, name)

        val entity = ManagedTrialWorldEntity(name, world)
        managedTrialWorldRepo.save(entity)

        return world
    }
}