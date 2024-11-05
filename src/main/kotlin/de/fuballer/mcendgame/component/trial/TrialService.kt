package de.fuballer.mcendgame.component.trial

import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceRepository
import de.fuballer.mcendgame.component.trial.entity.TrialEntitySpawnService
import de.fuballer.mcendgame.component.trial.entity.TrialResetService
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.event.TrialEnemyDeathEvent
import de.fuballer.mcendgame.event.TrialWaveCompletedEvent
import de.fuballer.mcendgame.event.TrialWaveSpawnedEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.extension.EntityExtension.isAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.isEnemy
import de.fuballer.mcendgame.util.extension.WorldExtension.isTrialWorld
import org.bukkit.*
import org.bukkit.entity.Creature
import org.bukkit.entity.Firework
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.random.Random

@Service
class TrialService(
    private val trialInstanceRepo: TrialInstanceRepository,
    private val trialEntitySpawnService: TrialEntitySpawnService,
    private val trialResetService: TrialResetService,
) : Listener {
    @EventHandler
    fun on(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val block = event.clickedBlock ?: return
        if (block.type != TrialSettings.SPAWNER_MATERIAL) return

        val world = event.player.world
        val instance = trialInstanceRepo.findByWorld(world) ?: return
        if (instance.waveActive) return

        trialResetService.resetBeforeNewWave(world)
        instance.spawner = event.clickedBlock
        instance.waveActive = true

        val random = Random
        trialEntitySpawnService.spawnEntities(world, instance, random)

        throwWaveSpawnedEvent(world, TrialSettings.TOTAL_WAVE_SPAWNING_TIME)

        world.playSound(block.location, Sound.AMBIENT_CAVE, SoundCategory.AMBIENT, 1f, 1f)
    }

    @EventHandler
    fun on(event: EntityDeathEvent) {
        val entity = event.entity
        val world = entity.world
        if (!world.isTrialWorld()) return

        val instance = trialInstanceRepo.findByWorld(world) ?: return

        if (entity.isEnemy()) {
            val trialEnemyDeathEvent = TrialEnemyDeathEvent(world, entity, instance, event)
            EventGateway.apply(trialEnemyDeathEvent)
        }

        val remainingEnemies = world.livingEntities.count { it.isEnemy() }
        if (remainingEnemies > 0) return

        val waveCompletedEvent = TrialWaveCompletedEvent(world, instance)
        EventGateway.apply(waveCompletedEvent)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun on(event: TrialWaveCompletedEvent) {
        val instance = event.trialInstance

        instance.level++
        instance.waveActive = false

        val world = event.world
        sendWaveCompletedMessage(world, instance.level)
        spawnFireworks(world, instance.spawner!!.location)
    }

    private fun sendWaveCompletedMessage(world: World, level: Int) {
        val message = TrialSettings.getWaveCompletedMessage(level)
        for (player in world.players) {
            player.sendMessage(message)
        }
    }

    private fun spawnFireworks(world: World, spawnerLocation: Location) {
        val location = spawnerLocation.clone().add(0.0, 0.8, 0.05)

        for (i in 0 until TrialSettings.FIREWORK_COUNT) {
            SchedulingUtil.runTaskLater(i * TrialSettings.FIREWORK_STEP_DELAY) {
                val firework = world.spawn(location, Firework::class.java)

                val meta = firework.fireworkMeta
                val effect = FireworkEffect.builder()
                    .withColor(TrialSettings.FIREWORK_COLORS.random())
                    .withFade(TrialSettings.FIREWORK_COLORS.random())
                    .with(TrialSettings.FIREWORK_TYPES.random())
                    .trail(Random.nextBoolean())
                    .flicker(Random.nextBoolean())
                    .build()
                meta.addEffect(effect)

                meta.power = TrialSettings.getFireworkPower()
                firework.fireworkMeta = meta
            }
        }
    }

    private fun throwWaveSpawnedEvent(
        world: World,
        delay: Long,
    ) {
        SchedulingUtil.runTaskLater(delay) {
            val players = world.players
            val livingEntities = world.livingEntities.filterIsInstance<Creature>()
            val allies = livingEntities.filter { it.isAlly() }
            val enemies = livingEntities.filter { it.isEnemy() }

            val event = TrialWaveSpawnedEvent(world, players, allies, enemies)
            EventGateway.apply(event)
        }
    }
}