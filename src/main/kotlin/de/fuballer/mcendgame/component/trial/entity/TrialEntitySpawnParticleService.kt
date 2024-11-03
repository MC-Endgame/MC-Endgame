package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.component.trial.TrialSettings
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.SchedulingUtil
import org.bukkit.Location
import org.bukkit.Particle

@Service
class TrialEntitySpawnParticleService {
    fun createAllySpawnParticlesTask(
        locations: List<Location>,
    ) {
        SchedulingUtil.scheduleSyncRepeatingTask(0, TrialSettings.ALLY_SPAWNING_PARTICLE_PERIOD, TrialSettings.ALLY_SPAWNING_PARTICLE_REPEATS) {
            spawnAllySpawnParticles(locations)
        }
    }

    private fun spawnAllySpawnParticles(
        locations: List<Location>,
    ) {
        locations.forEach {
            it.world.spawnParticle(Particle.COMPOSTER, it, 15, 0.2, 0.7, 0.2, 0.05)
        }
    }

    fun createEnemySpawnParticlesTask(
        locations: List<Location>,
    ) {
        SchedulingUtil.scheduleSyncRepeatingTask(0, TrialSettings.ENEMY_SPAWNING_PARTICLE_PERIOD, TrialSettings.ENEMY_SPAWNING_PARTICLE_REPEATS) {
            spawnEnemySpawnParticles(locations)
        }
    }

    private fun spawnEnemySpawnParticles(
        locations: List<Location>,
    ) {
        locations.forEach {
            it.world.spawnParticle(Particle.SMOKE, it, 15, 0.2, 0.7, 0.2, 0.05)
        }
    }
}