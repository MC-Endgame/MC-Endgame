package de.fuballer.mcendgame.component.device.map_device

import de.fuballer.mcendgame.component.device.DeviceService
import de.fuballer.mcendgame.component.device.DeviceSettings
import de.fuballer.mcendgame.component.device.db.DeviceRepository
import de.fuballer.mcendgame.component.dungeon.progress.PlayerDungeonProgressService
import de.fuballer.mcendgame.component.dungeon.progress.PlayerDungeonProgressSettings
import de.fuballer.mcendgame.component.inventory.CustomInventoryType
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.InventoryUtil
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.InventoryExtension.getCustomType
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getDeviceAction
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
class MapDeviceInventoryService(
    private val deviceRepo: DeviceRepository,
    private val deviceService: DeviceService,
    private val mapDeviceService: MapDeviceService,
    private val playerDungeonProgressService: PlayerDungeonProgressService,
) : Listener {
    fun openInventory(player: Player) {
        val inventory = createMapDeviceInventory(player)
        player.openInventory(inventory)
    }

    @EventHandler
    fun on(event: InventoryClickEvent) {
        if (event.inventory.getCustomType() != CustomInventoryType.MAP_DEVICE) return
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
            DeviceAction.OPEN -> mapDeviceService.openDungeon(device, player)
            DeviceAction.CLOSE -> deviceService.closeRemainingPortals(device)
            else -> return
        }
    }

    private fun createMapDeviceInventory(player: Player): Inventory {
        val inventory = InventoryUtil.createInventory(
            InventoryType.HOPPER,
            MapDeviceSettings.MAP_DEVICE_INVENTORY_TITLE,
            CustomInventoryType.MAP_DEVICE
        )

        inventory.setItem(0, DeviceSettings.OPEN_PORTALS_ITEM)
        inventory.setItem(1, DeviceSettings.FILLER_ITEM)
        inventory.setItem(2, getDungeonTierDisplayItem(player))
        inventory.setItem(3, DeviceSettings.FILLER_ITEM)
        inventory.setItem(4, DeviceSettings.CLOSE_PORTALS_ITEM)

        return inventory
    }

    private fun getDungeonTierDisplayItem(player: Player): ItemStack {
        val (_, tier, progress) = playerDungeonProgressService.getPlayerDungeonLevel(player.uniqueId)
        val displayItem = ItemStack(Material.BLUE_STAINED_GLASS_PANE)
        val openPortalsMeta = displayItem.itemMeta ?: return displayItem

        openPortalsMeta.setDisplayName("${ChatColor.GOLD}Tier: $tier")
        openPortalsMeta.lore = listOf("${ChatColor.BLUE}Progress: $progress/${PlayerDungeonProgressSettings.DUNGEON_LEVEL_INCREASE_THRESHOLD}")
        displayItem.itemMeta = openPortalsMeta

        return displayItem
    }
}