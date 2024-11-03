package de.fuballer.mcendgame.component.trial

import org.bukkit.Material

object TrialSettings {
    val SPAWNER_MATERIAL = Material.TRIAL_SPAWNER

    const val ALLY_SPAWNING_PARTICLE_PERIOD = 5L // ticks
    const val ALLY_SPAWNING_TOTAL_PARTICLE_TIME = 100L // ticks
    val ALLY_SPAWNING_PARTICLE_REPEATS = (ALLY_SPAWNING_TOTAL_PARTICLE_TIME / ALLY_SPAWNING_PARTICLE_PERIOD).toInt()
}