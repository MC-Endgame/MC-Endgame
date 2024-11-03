package de.fuballer.mcendgame.component.trial.generation

import de.fuballer.mcendgame.component.dungeon.generation.DungeonBuilderService
import de.fuballer.mcendgame.component.dungeon.generation.data.PlaceableTile
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceEntity
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceRepository
import de.fuballer.mcendgame.component.trial.generation.world.TrialWorldService
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.VectorUtil
import org.bukkit.Location
import org.bukkit.util.Vector
import java.util.*

@Service
class TrialGenerationService(
    private val trialWorldService: TrialWorldService,
    private val dungeonBuilderService: DungeonBuilderService,
    private val trialInstanceRepo: TrialInstanceRepository,
) {
    fun generateTrial(
        leaveLocation: Location
    ): Location {
        val seed = "${UUID.randomUUID()}".hashCode().toLong()
        val world = trialWorldService.createTrialWorld(seed)

        val room = TrialRooms.ROOMS.random()

        val tile = PlaceableTile(room.schematicData, room.format, Vector(0, 0, 0), 0.0)
        dungeonBuilderService.build(world, listOf(tile))

        val instanceEntity = TrialInstanceEntity(world, 0, room.allySpawnLocations, room.enemySpawnLocations)
        trialInstanceRepo.save(instanceEntity)

        val spawnLocation = room.startLocation!!
        val startLocation = VectorUtil.toLocation(world, spawnLocation.location, spawnLocation.rotation)
        return startLocation
    }
}