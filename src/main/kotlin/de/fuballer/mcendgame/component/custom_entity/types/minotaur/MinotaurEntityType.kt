package de.fuballer.mcendgame.component.custom_entity.types.minotaur

import de.fuballer.mcendgame.component.custom_entity.EntitySoundData
import de.fuballer.mcendgame.component.custom_entity.ability.Ability
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.ApplySpeedAbility
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.FlameBlastAbility
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.LeapAbility
import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.util.random.RandomOption
import org.bukkit.entity.EntityType

object MinotaurEntityType : CustomEntityType {
    override val type = EntityType.IRON_GOLEM

    override val customName = "Minotaur"
    override val canHaveWeapons = false
    override val isRanged = false
    override val canHaveArmor = false
    override val hideEquipment = true

    override val baseHealth = 100.0
    override val healthPerTier = 5.0
    override val baseDamage = 12.0
    override val damagePerTier = 3.0
    override val baseSpeed = 0.4
    override val speedPerTier = 0.0

    override val sounds = EntitySoundData.create("minotaur")
    override val abilities: List<RandomOption<Ability>> = listOf(
        RandomOption(20, ApplySpeedAbility),
        RandomOption(50, LeapAbility),
        RandomOption(25, FlameBlastAbility),
    )
}