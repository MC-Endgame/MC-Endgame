package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.trial.TrialAllySettings
import org.bukkit.inventory.ItemStack

enum class TrialPartyInventorySlotType(
    val slot: Int,
    val isValid: (item: ItemStack) -> Boolean,
) {
    TYPE_ITEM(0, {
        false //it.getCustomEntityType() != null
    }),
    MAIN_HAND(2, {
        TrialAllySettings.VALID_HAND_ITEMS.contains(it.type)
    }),
    OFF_HAND(3, {
        TrialAllySettings.VALID_HAND_ITEMS.contains(it.type)
    }),
    HELMET(4, {
        TrialAllySettings.VALID_HELMETS.contains(it.type)
    }),
    CHESTPLATE(5, {
        TrialAllySettings.VALID_CHESTPLATES.contains(it.type)
    }),
    LEGGINGS(6, {
        TrialAllySettings.VALID_LEGGINGS.contains(it.type)
    }),
    BOOTS(7, {
        TrialAllySettings.VALID_BOOTS.contains(it.type)
    });
    //EMPTY(-1, {
    //    false
    //});

    companion object {
        private val slotMap = entries.associateBy { it.slot }
        infix fun from(slot: Int) = slotMap[slot % 9]

        fun fromItem(item: ItemStack) = entries.filter { it.isValid(item) }
    }
}