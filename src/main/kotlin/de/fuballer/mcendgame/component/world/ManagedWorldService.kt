package de.fuballer.mcendgame.component.world

import de.fuballer.mcendgame.component.world.db.ManagedWorldEntity
import de.fuballer.mcendgame.component.world.db.ManagedWorldRepository
import de.fuballer.mcendgame.event.DungeonWorldDeleteEvent
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.framework.annotation.Qualifier
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.framework.stereotype.LifeCycleListener
import de.fuballer.mcendgame.util.PluginUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.ThreadUtil.bukkitSync
import de.fuballer.mcendgame.util.file.FileHelper
import org.bukkit.World
import org.bukkit.WorldCreator
import org.bukkit.WorldType
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

@Service
class ManagedWorldService(
    private val managedWorldRepo: ManagedWorldRepository,
    private val fileHelper: FileHelper,
    @Qualifier("worldContainer")
    private val worldContainer: File
) : LifeCycleListener {

    override fun initialize(plugin: JavaPlugin) {
        SchedulingUtil.runTaskTimer(WorldSettings.WORLD_EMPTY_TEST_PERIOD) {
            checkWorldTimers()
        }
    }

    override fun terminate() {
        managedWorldRepo.findAll()
            .forEach { deleteWorld(it) }
    }

    fun createWorld(seed: Long, name: String): World {
        val worldCreator = WorldCreator(name)
            .seed(seed)
            .type(WorldType.FLAT)
            .generateStructures(false)
            .generatorSettings(WorldSettings.GENERATOR_SETTINGS)

        val world = bukkitSync {
            PluginUtil.createWorld(worldCreator).apply {
                WorldSettings.updateGameRules(this)

                difficulty = WorldSettings.DIFFICULTY
                time = WorldSettings.WORLD_TIME
            }
        }

        return world
    }

    private fun checkWorldTimers() {
        managedWorldRepo.findAll()
            .map {
                updateDeleteTimer(it)
                managedWorldRepo.save(it)
            }
            .filter { it.deleteTimer > WorldSettings.MAX_WORLD_EMPTY_TIME }
            .forEach { deleteWorld(it) }
    }

    private fun updateDeleteTimer(entity: ManagedWorldEntity) {
        if (entity.world.players.isEmpty()) {
            entity.deleteTimer++
        } else {
            entity.deleteTimer = 0
        }
    }

    private fun deleteWorld(entity: ManagedWorldEntity) {
        val world = entity.world

        val dungeonWorldDeleteEvent = DungeonWorldDeleteEvent(world)
        EventGateway.apply(dungeonWorldDeleteEvent)

        PluginUtil.unloadWorld(world)
        managedWorldRepo.deleteById(world.name)

        val toDelete = File("$worldContainer/${world.name}")
        fileHelper.deleteFile(toDelete)

        managedWorldRepo.delete(entity)
    }
}