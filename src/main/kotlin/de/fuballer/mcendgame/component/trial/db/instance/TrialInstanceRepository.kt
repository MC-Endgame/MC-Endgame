package de.fuballer.mcendgame.component.trial.db.instance

import de.fuballer.mcendgame.framework.InMemoryMapRepository
import de.fuballer.mcendgame.framework.annotation.Service
import org.bukkit.World
import java.util.*

@Service
class TrialInstanceRepository : InMemoryMapRepository<UUID, TrialInstanceEntity>() {

    fun findByWorld(world: World) = findAll().firstOrNull { it.world == world }
}