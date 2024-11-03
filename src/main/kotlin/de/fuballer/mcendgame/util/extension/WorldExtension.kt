package de.fuballer.mcendgame.util.extension

import de.fuballer.mcendgame.component.dungeon.generation.world.DungeonWorldSettings
import de.fuballer.mcendgame.component.trial.generation.world.TrialWorldSettings
import org.bukkit.World

object WorldExtension {
    fun World.isDungeonWorld() = name.contains(DungeonWorldSettings.DUNGEON_WORLD_PREFIX)
    fun World.isTrialWorld() = name.contains(TrialWorldSettings.TRIAL_WORLD_PREFIX)
}