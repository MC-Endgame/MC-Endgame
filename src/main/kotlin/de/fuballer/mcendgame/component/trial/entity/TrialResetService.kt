package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.EntityExtension.isAlly
import org.bukkit.World

@Service
class TrialResetService {
    fun resetBeforeNewWave(world: World) = world.livingEntities.filter { it.isAlly() }.forEach { it.remove() }
}