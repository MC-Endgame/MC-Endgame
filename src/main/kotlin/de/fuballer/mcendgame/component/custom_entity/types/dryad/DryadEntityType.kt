package de.fuballer.mcendgame.component.custom_entity.types.dryad

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import org.bukkit.Material
import org.bukkit.entity.EntityType

object DryadEntityType : CustomEntityType {
    override val type = EntityType.SKELETON

    override val customName = "Dryad"
    override val canHaveWeapons = true
    override val isRanged = true
    override val canHaveArmor = true
    override val hideEquipment = false

    override val baseHealth = 15.0
    override val healthPerTier = 0.0
    override val baseDamage = 4.0
    override val damagePerTier = 2.0
    override val baseSpeed = 0.25
    override val speedPerTier = 0.0
    override val knockbackResistance = 0.0

    override val sounds = null
    override val abilities = null

    override val spawnEgg = Material.BOGGED_SPAWN_EGG
    override val description = "Converts dealt Damage to Healing. Heals self and nearest Ally."
}