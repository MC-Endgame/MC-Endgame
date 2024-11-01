package de.fuballer.mcendgame.component.custom_entity.types.cerberus

import de.fuballer.mcendgame.component.custom_entity.EntitySoundData
import de.fuballer.mcendgame.component.custom_entity.ability.Ability
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.ApplyDarknessAbility
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.FireCascadeAbility
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.FlameBlastAbility
import de.fuballer.mcendgame.component.custom_entity.ability.abilities.KnockbackAbility
import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.util.random.RandomOption
import org.bukkit.Material
import org.bukkit.entity.EntityType

object CerberusEntityType : CustomEntityType {
    override val type = EntityType.RAVAGER

    override val customName = "Cerberus"
    override val canHaveWeapons = false
    override val isRanged = false
    override val canHaveArmor = false
    override val hideEquipment = true

    override val baseHealth = 250.0
    override val healthPerTier = 10.0
    override val baseDamage = 15.0
    override val damagePerTier = 2.5
    override val baseSpeed = 0.30
    override val speedPerTier = 0.003
    override val knockbackResistance = 0.8

    override val sounds = EntitySoundData.create("cerberus")
    override val abilities: List<RandomOption<Ability>> = listOf(
        RandomOption(35, FireCascadeAbility),
        RandomOption(10, KnockbackAbility),
        RandomOption(15, ApplyDarknessAbility),
        RandomOption(35, FlameBlastAbility),
    )

    override val spawnEgg = Material.MAGMA_CUBE_SPAWN_EGG
    override val description = "Melee attacks. Fire spells."
}