package de.fuballer.mcendgame.component.custom_entity.types

import de.fuballer.mcendgame.component.custom_entity.EntitySoundData
import de.fuballer.mcendgame.component.custom_entity.ability.Ability
import de.fuballer.mcendgame.util.random.RandomOption
import org.bukkit.Material
import org.bukkit.entity.EntityType

interface CustomEntityType {
    val type: EntityType

    val customName: String?
    val canHaveWeapons: Boolean
    val isRanged: Boolean
    val canHaveArmor: Boolean
    val hideEquipment: Boolean

    val baseHealth: Double
    val healthPerTier: Double
    val baseDamage: Double
    val damagePerTier: Double
    val baseSpeed: Double
    val speedPerTier: Double
    val knockbackResistance: Double

    val sounds: EntitySoundData?
    val abilities: List<RandomOption<Ability>>?

    val spawnEgg: Material
    val description: String
}