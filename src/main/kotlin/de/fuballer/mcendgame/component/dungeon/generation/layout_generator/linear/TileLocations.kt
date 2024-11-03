package de.fuballer.mcendgame.component.dungeon.generation.layout_generator.linear

import de.fuballer.mcendgame.component.dungeon.generation.data.SpawnLocation
import org.bukkit.util.Vector

data class TileLocations(
    val startLocation: Vector?,
    val doors: List<Door>,
    val enemySpawnLocations: List<SpawnLocation>,
    val bossSpawnLocations: List<SpawnLocation>,
    val allySpawnLocations: List<SpawnLocation>
)