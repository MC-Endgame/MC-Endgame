package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.trial.TrialAllySettings
import de.fuballer.mcendgame.util.ItemCreator
import de.fuballer.mcendgame.util.TextComponent
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object TrialPartyInventorySettings {
    const val INVENTORY_SIZE = TrialAllySettings.MAX_ALLIES * 9

    val VALID_SLOT_ITEM = ItemCreator.create(ItemStack(Material.GREEN_STAINED_GLASS_PANE), TextComponent.empty(), listOf())
}