package de.fuballer.mcendgame.component.dungeon.generation.world

import de.fuballer.mcendgame.component.world.ManagedWorldService
import de.fuballer.mcendgame.component.world.WorldSettings
import de.fuballer.mcendgame.component.world.db.dungeon.ManagedDungeonWorldEntity
import de.fuballer.mcendgame.component.world.db.dungeon.ManagedDungeonWorldRepository
import de.fuballer.mcendgame.framework.annotation.Service
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

@Service
class DungeonWorldService(
    private val managedWorldService: ManagedWorldService,
    private val managedDungeonWorldRepo: ManagedDungeonWorldRepository,
) {
    fun createDungeonWorld(
        player: Player,
        seed: Long
    ): World {
        val name = "${WorldSettings.WORLD_PREFIX}${DungeonWorldSettings.DUNGEON_WORLD_PREFIX}${UUID.randomUUID()}"
        val world = managedWorldService.createWorld(seed, name)

        val entity = ManagedDungeonWorldEntity(name, world, player)
        managedDungeonWorldRepo.save(entity)

        return world
    }
}