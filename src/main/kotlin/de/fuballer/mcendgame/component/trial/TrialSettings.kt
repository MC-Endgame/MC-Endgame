package de.fuballer.mcendgame.component.trial

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.custom_entity.types.bogged.BoggedEntityType
import de.fuballer.mcendgame.component.custom_entity.types.husk.HuskEntityType
import de.fuballer.mcendgame.component.custom_entity.types.melee_skeleton.MeleeSkeletonEntityType
import de.fuballer.mcendgame.component.custom_entity.types.piglin_brute.PiglinBruteEntityType
import de.fuballer.mcendgame.component.custom_entity.types.skeleton.SkeletonEntityType
import de.fuballer.mcendgame.component.custom_entity.types.stray.StrayEntityType
import de.fuballer.mcendgame.component.custom_entity.types.witch.WitchEntityType
import de.fuballer.mcendgame.component.custom_entity.types.wither_skeleton.WitherSkeletonEntityType
import de.fuballer.mcendgame.component.custom_entity.types.zombie.ZombieEntityType
import de.fuballer.mcendgame.util.random.RandomOption
import org.bukkit.Material
import kotlin.math.max

object TrialSettings {
    val SPAWNER_MATERIAL = Material.TRIAL_SPAWNER

    const val ALLY_SPAWNING_PARTICLE_PERIOD = 5L // ticks
    const val ALLY_SPAWNING_TOTAL_PARTICLE_TIME = 100L // ticks
    const val ALLY_SPAWNING_PARTICLE_REPEATS = (ALLY_SPAWNING_TOTAL_PARTICLE_TIME / ALLY_SPAWNING_PARTICLE_PERIOD).toInt()

    const val ENEMY_SPAWNING_PARTICLE_PERIOD = 5L // ticks
    const val ENEMY_SPAWNING_TOTAL_PARTICLE_TIME = 100L // ticks
    const val ENEMY_SPAWNING_PARTICLE_REPEATS = (ENEMY_SPAWNING_TOTAL_PARTICLE_TIME / ENEMY_SPAWNING_PARTICLE_PERIOD).toInt()

    val TOTAL_WAVE_SPAWNING_TIME = max(ALLY_SPAWNING_TOTAL_PARTICLE_TIME, ENEMY_SPAWNING_TOTAL_PARTICLE_TIME) + 1

    fun getEnemyCount(playerCount: Int) = playerCount * 10

    val ENEMY_TYPES: List<RandomOption<CustomEntityType>> = listOf(
        RandomOption(50, ZombieEntityType),
        RandomOption(25, SkeletonEntityType),
        RandomOption(30, HuskEntityType),
        RandomOption(20, StrayEntityType),
        RandomOption(20, MeleeSkeletonEntityType),
        RandomOption(20, PiglinBruteEntityType),
        RandomOption(10, WitherSkeletonEntityType),
        RandomOption(7, BoggedEntityType),
        RandomOption(5, WitchEntityType),
    )
}