package de.fuballer.mcendgame.component.trial.db.party

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import org.bukkit.inventory.ItemStack

data class TrialAllyEntity(
    var type: CustomEntityType? = null,
    var gear: MutableMap<TrialPartyInventorySlotType, ItemStack> = mutableMapOf(),
)