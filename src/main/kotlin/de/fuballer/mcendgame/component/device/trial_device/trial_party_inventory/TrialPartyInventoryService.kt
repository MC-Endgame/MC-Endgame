package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.device.DeviceSettings
import de.fuballer.mcendgame.component.device.trial_device.TrialDeviceSettings
import de.fuballer.mcendgame.component.inventory.CustomInventoryType
import de.fuballer.mcendgame.component.trial.TrialAllySettings
import de.fuballer.mcendgame.component.trial.db.party.TrialAllyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.InventoryUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.InventoryExtension.getCustomType
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Service
class TrialPartyInventoryService(
    private val trialPartyRepo: TrialPartyRepository,
) : Listener {
    fun openPartyInventory(player: Player) {
        val inventory = createPartyInventory(player)
        player.openInventory(inventory)
    }

    @EventHandler
    fun on(event: InventoryDragEvent) {
        if (event.inventory.getCustomType() != CustomInventoryType.TRIAL_PARTY) return
        event.cancel()
    }

    @EventHandler
    fun on(event: InventoryClickEvent) {
        if (event.inventory.getCustomType() != CustomInventoryType.TRIAL_PARTY) return

        changeHighlights(event.inventory, event.whoClicked.itemOnCursor, false)

        when (event.action) {
            InventoryAction.SWAP_WITH_CURSOR -> onSwapWithCursor(event)
            InventoryAction.PICKUP_ALL -> onPickUpAll(event)
            InventoryAction.PLACE_ALL -> {}
            else -> event.cancel()
        }

        SchedulingUtil.runTaskLater(1) {
            changeHighlights(event.inventory, event.whoClicked.itemOnCursor, true)
        }
    }

    private fun onSwapWithCursor(event: InventoryClickEvent) {
        if (!isClickedSlotInInventory(event)) return
        event.cancel()

        val cursorItem = event.cursor
        val slotType = (TrialPartyInventorySlotType from event.rawSlot) ?: return
        if (!slotType.isValid(cursorItem)) return

        event.whoClicked.setItemOnCursor(null)
        event.inventory.setItem(event.rawSlot, cursorItem)

        saveParty(event.inventory, event.whoClicked as Player)
    }

    private fun onPickUpAll(event: InventoryClickEvent) {
        if (!isClickedSlotInInventory(event)) return
        event.cancel()

        val item = event.currentItem
        if (item == DeviceSettings.FILLER_ITEM) return

        event.whoClicked.setItemOnCursor(item)
        event.inventory.setItem(event.rawSlot, DeviceSettings.FILLER_ITEM)

        saveParty(event.inventory, event.whoClicked as Player)
    }

    private fun isClickedSlotInInventory(event: InventoryClickEvent) = event.clickedInventory == event.inventory

    private fun changeHighlights(inventory: Inventory, item: ItemStack, highlight: Boolean) {
        val validSlotTypes = TrialPartyInventorySlotType.fromItem(item)

        for (row in 0 until TrialAllySettings.MAX_ALLIES) {
            for (slotType in validSlotTypes) {
                val slot = row * 9 + slotType.slot

                val slotItem = inventory.getItem(slot)
                if (slotItem != null && slotType.isValid(slotItem)) continue

                val newSlotItem = if (highlight) TrialPartyInventorySettings.VALID_SLOT_ITEM else DeviceSettings.FILLER_ITEM
                inventory.setItem(slot, newSlotItem)
            }
        }
    }

    private fun saveParty(inventory: Inventory, player: Player) {
        val allies: MutableMap<Int, TrialAllyEntity> = mutableMapOf()

        for (row in 0 until TrialAllySettings.MAX_ALLIES) {
            val rowStartIndex = row * 9

            val ally = TrialAllyEntity()
            allies[row] = ally

            val gear = mutableMapOf<TrialPartyInventorySlotType, ItemStack>()
            for (slot in 1..8) {
                val slotType = (TrialPartyInventorySlotType from slot) ?: continue
                val item = inventory.getItem(rowStartIndex + slot) ?: continue
                if (!slotType.isValid(item)) continue

                gear[slotType] = item
            }
            ally.gear = gear

            val typeItem = inventory.getItem(rowStartIndex) ?: continue
            if (!TrialPartyInventorySlotType.TYPE_ITEM.isValid(typeItem)) continue
            ally.typeItem = typeItem
        }

        val party = TrialPartyEntity(player.uniqueId, allies)
        trialPartyRepo.save(party)
    }

    private fun createPartyInventory(player: Player): Inventory {
        val inventory = InventoryUtil.createInventory(
            TrialPartyInventorySettings.INVENTORY_SIZE,
            TrialDeviceSettings.TRIAL_PARTY_INVENTORY_TITLE,
            CustomInventoryType.TRIAL_PARTY
        )

        fillPartyInventory(inventory, player)

        return inventory
    }

    private fun fillPartyInventory(inventory: Inventory, player: Player) {
        for (slot in 0 until TrialPartyInventorySettings.INVENTORY_SIZE) {
            inventory.setItem(slot, DeviceSettings.FILLER_ITEM)
        }

        val partyEntity = trialPartyRepo.findById(player.uniqueId) ?: return

        for (orderedAlly in partyEntity.allies) {
            val rowStartIndex = orderedAlly.key * 9

            val ally = orderedAlly.value

            for (gearPiece in ally.gear) {
                val slot = rowStartIndex + gearPiece.key.slot
                inventory.setItem(slot, gearPiece.value)
            }

            val typeItem = ally.typeItem
            if (typeItem.type == Material.AIR) continue
            inventory.setItem(rowStartIndex, typeItem)
        }
    }
}