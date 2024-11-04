package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.trial.TrialAllySettings
import de.fuballer.mcendgame.util.ItemCreator
import de.fuballer.mcendgame.util.TextComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

object TrialPartyInventorySettings {
    const val INVENTORY_SIZE = TrialAllySettings.MAX_ALLIES * 9
    val INVENTORY_TITLE = TextComponent.create("Trial Party")

    val VALID_SLOT_ITEM = ItemCreator.create(ItemStack(Material.GREEN_STAINED_GLASS_PANE), TextComponent.empty(), listOf())
    val TYPE_FILLER_ITEM = ItemCreator.create(
        ItemStack(Material.RED_STAINED_GLASS_PANE),
        Component.text("Empty", NamedTextColor.RED),
        listOf(Component.text("Click to select ally.", NamedTextColor.YELLOW))
    )
}