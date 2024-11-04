package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

data class TrialPlayerOwnedEntity(
    val player: Player,
    val customEntityType: CustomEntityType,
    val gear: MutableMap<TrialPartyInventorySlotType, ItemStack>,
)
