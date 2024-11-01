package de.fuballer.mcendgame.component.device

import de.fuballer.mcendgame.component.device.db.DeviceEntity
import de.fuballer.mcendgame.component.device.db.DeviceRepository
import de.fuballer.mcendgame.component.portal.PortalService
import de.fuballer.mcendgame.event.PortalFailedEvent
import de.fuballer.mcendgame.event.PortalUsedEvent
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.framework.stereotype.LifeCycleListener
import de.fuballer.mcendgame.util.MathUtil
import de.fuballer.mcendgame.util.ThreadUtil.bukkitSync
import org.bukkit.Location
import org.bukkit.block.data.type.RespawnAnchor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import kotlin.math.min

@Service
class DeviceService(
    private val deviceRepo: DeviceRepository,
    private val portalService: PortalService,
) : Listener, LifeCycleListener {
    override fun terminate() {
        deviceRepo.findAll()
            .forEach { entity ->
                entity.portals.forEach { it.close() }
                entity.portals.clear()

                deviceRepo.save(entity)
            }
    }

    @EventHandler
    fun on(event: PortalUsedEvent) {
        val portal = event.portal
        val mapDevice = deviceRepo.findByPortal(portal) ?: return

        mapDevice.portals.remove(portal)
        deviceRepo.save(mapDevice)

        updateMapDeviceVisual(mapDevice)
    }

    @EventHandler
    fun on(event: PortalFailedEvent) {
        val mapDevice = deviceRepo.findByPortal(event.portal) ?: return

        closeRemainingPortals(mapDevice)
    }

    fun closeRemainingPortals(entity: DeviceEntity) {
        entity.portals.forEach { it.close() }
        entity.portals.clear()

        deviceRepo.save(entity)
        updateMapDeviceVisual(entity)
    }

    fun openPortals(mapDevice: DeviceEntity, target: Location) {
        val deviceCenter = mapDevice.location.clone().add(0.5, 0.0, 0.5)

        mapDevice.portals = DeviceSettings.PORTAL_OFFSETS
            .map { deviceCenter.clone().add(it) }
            .onEach { it.yaw = MathUtil.calculateYawToFacingLocation(it, deviceCenter) + 180 }
            .map { portalService.createPortal(it, target, isSingleUse = true) }
            .toMutableList()

        deviceRepo.save(mapDevice)
    }

    fun updateMapDeviceVisual(mapDevice: DeviceEntity) {
        val block = mapDevice.location.block
        val anchor = block.blockData as RespawnAnchor

        anchor.charges = min(4, mapDevice.portals.size)
        bukkitSync { block.blockData = anchor }
    }
}