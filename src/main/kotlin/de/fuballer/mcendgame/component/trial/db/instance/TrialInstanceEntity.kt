package de.fuballer.mcendgame.component.trial.db.instance

import de.fuballer.mcendgame.component.dungeon.generation.data.SpawnLocation
import de.fuballer.mcendgame.framework.stereotype.Entity
import org.bukkit.World
import java.util.*

data class TrialInstanceEntity(
    override var id: UUID = UUID.randomUUID(),

    var world: World,

    var progress: Int,
    var allySpawnLocations: List<SpawnLocation>,
    var enemySpawnLocations: List<SpawnLocation>,

    var waveActive: Boolean = false
) : Entity<UUID> {
    constructor(world: World, progress: Int, allySpawnLocations: List<SpawnLocation>, enemySpawnLocations: List<SpawnLocation>) : this(
        UUID.randomUUID(),
        world,
        progress,
        allySpawnLocations,
        enemySpawnLocations
    )
}