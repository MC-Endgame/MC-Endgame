package de.fuballer.mcendgame.component.device.trial_device

import de.fuballer.mcendgame.component.device.DeviceService
import de.fuballer.mcendgame.component.device.db.DeviceEntity
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.ThreadUtil.async
import org.bukkit.event.Listener
import java.util.logging.Logger
import kotlin.time.measureTime

@Service
class TrialDeviceService(
    private val deviceService: DeviceService,
    private val logger: Logger,
) : Listener {
    fun openTrial(
        device: DeviceEntity,
    ) = async {
        val time = measureTime {
            val leaveLocation = device.location.clone().add(0.5, 1.0, 0.5)
            val startLocation = leaveLocation.clone() //TODO get from trial generation

            //val dungeonOpenEvent = DungeonOpenEvent(player, startLocation.world!!)
            //EventGateway.apply(dungeonOpenEvent)

            deviceService.closeRemainingPortals(device)
            deviceService.openPortals(device, startLocation)
            deviceService.updateMapDeviceVisual(device)
        }

        logger.info("Created Trial in ${time.inWholeMilliseconds} ms")
    }
}