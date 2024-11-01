package de.fuballer.mcendgame.component.device

import de.fuballer.mcendgame.component.device.db.DeviceEntity
import de.fuballer.mcendgame.component.device.db.DeviceRepository
import de.fuballer.mcendgame.component.device.map_device.MapDeviceInventoryService
import de.fuballer.mcendgame.component.device.trial_device.TrialDeviceInventoryService
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.BlockExtension.getDeviceType
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.PlayerExtension.setLastDevice
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

@Service
class DeviceBlockInventoryService(
    private val deviceRepo: DeviceRepository,
    private val mapDeviceInventoryService: MapDeviceInventoryService,
    private val trialDeviceInventoryService: TrialDeviceInventoryService,
) : Listener {

    @EventHandler
    fun on(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return
        val block = event.clickedBlock ?: return

        val deviceType = block.getDeviceType() ?: return

        event.cancel()

        val player = event.player
        val location = block.location

        val entity = deviceRepo.findByLocation(location)
            ?: DeviceEntity(location, deviceType).apply { deviceRepo.save(this) }
        player.setLastDevice(entity.id)

        when (deviceType) {
            DeviceType.MAP_DEVICE -> mapDeviceInventoryService.openInventory(player)
            DeviceType.TRIAL_DEVICE -> trialDeviceInventoryService.openInventory(player)
        }
    }
}