package de.fuballer.mcendgame.component.trial.db

import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class TrialAllyEntity(
    var typeItem: ItemStack = ItemStack(Material.AIR),
    var gear: MutableMap<TrialPartyInventorySlotType, ItemStack> = mutableMapOf(),
)