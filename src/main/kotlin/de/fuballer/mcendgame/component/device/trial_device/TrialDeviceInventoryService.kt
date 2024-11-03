package de.fuballer.mcendgame.component.device.trial_device

import de.fuballer.mcendgame.component.device.DeviceService
import de.fuballer.mcendgame.component.device.DeviceSettings
import de.fuballer.mcendgame.component.device.db.DeviceRepository
import de.fuballer.mcendgame.component.device.map_device.DeviceAction
import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventoryService
import de.fuballer.mcendgame.component.inventory.CustomInventoryType
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.InventoryUtil
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.InventoryExtension.getCustomType
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getDeviceAction
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setDeviceAction
import de.fuballer.mcendgame.util.extension.PlayerExtension.getLastDevice
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Service
class TrialDeviceInventoryService(
    private val deviceRepo: DeviceRepository,
    private val deviceService: DeviceService,
    private val trialDeviceService: TrialDeviceService,
    private val trialPartyInventoryService: TrialPartyInventoryService,
    private val trialPartyRepository: TrialPartyRepository,
) : Listener {
    fun openInventory(player: Player) {
        val inventory = createTrialDeviceInventory(player)
        player.openInventory(inventory)
    }

    @EventHandler
    fun on(event: InventoryClickEvent) {
        if (event.inventory.getCustomType() != CustomInventoryType.TRIAL_DEVICE) return
        event.cancel()

        val inventory = event.inventory
        val clickedSlot = event.rawSlot
        if (clickedSlot !in 0 until inventory.size) return

        val clickedItem = inventory.getItem(clickedSlot) ?: return

        val player = event.whoClicked as Player
        val lastDeviceId = player.getLastDevice() ?: return
        val device = deviceRepo.findById(lastDeviceId) ?: return

        val action = clickedItem.getDeviceAction() ?: return
        player.closeInventory()

        when (action) {
            DeviceAction.OPEN -> trialDeviceService.openTrial(device)
            DeviceAction.CLOSE -> deviceService.closeRemainingPortals(device)
            DeviceAction.SHOW_TRIAL_PARTY -> trialPartyInventoryService.openPartyInventory(player)
        }
    }

    private fun createTrialDeviceInventory(player: Player): Inventory {
        val inventory = InventoryUtil.createInventory(
            InventoryType.HOPPER,
            TrialDeviceSettings.TRIAL_DEVICE_INVENTORY_TITLE,
            CustomInventoryType.TRIAL_DEVICE
        )

        inventory.setItem(0, DeviceSettings.OPEN_PORTALS_ITEM)
        inventory.setItem(1, DeviceSettings.FILLER_ITEM)
        inventory.setItem(2, getShowPartyItem(player))
        inventory.setItem(3, DeviceSettings.FILLER_ITEM)
        inventory.setItem(4, DeviceSettings.CLOSE_PORTALS_ITEM)

        return inventory
    }

    private fun getShowPartyItem(player: Player): ItemStack {
        val alliesSelected = getAlliesCount(player)
        val displayItem = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
        val openPortalsMeta = displayItem.itemMeta ?: return displayItem

        openPortalsMeta.setDisplayName("${ChatColor.GOLD}Configure Party")
        openPortalsMeta.lore = listOf("${ChatColor.BLUE}Allies: $alliesSelected/5")
        displayItem.itemMeta = openPortalsMeta

        displayItem.setDeviceAction(DeviceAction.SHOW_TRIAL_PARTY)
        return displayItem
    }

    private fun getAlliesCount(player: Player): Int {
        val party = trialPartyRepository.findById(player.uniqueId) ?: return 0

        return party.allies.values.count { it.typeItem.type != Material.AIR }
    }
}