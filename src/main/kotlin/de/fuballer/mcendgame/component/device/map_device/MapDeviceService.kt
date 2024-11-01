package de.fuballer.mcendgame.component.device.map_device

import de.fuballer.mcendgame.component.device.DeviceService
import de.fuballer.mcendgame.component.device.db.DeviceEntity
import de.fuballer.mcendgame.component.dungeon.generation.DungeonGenerationService
import de.fuballer.mcendgame.component.dungeon.progress.PlayerDungeonProgressService
import de.fuballer.mcendgame.event.DungeonOpenEvent
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.ThreadUtil.async
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.logging.Logger
import kotlin.time.measureTime

@Service
class MapDeviceService(
    private val dungeonGenerationService: DungeonGenerationService,
    private val playerDungeonProgressService: PlayerDungeonProgressService,
    private val deviceService: DeviceService,
    private val logger: Logger,
) : Listener {
    fun openDungeon(
        device: DeviceEntity,
        player: Player
    ) = async {
        val time = measureTime {
            val mapTier = playerDungeonProgressService.getPlayerDungeonLevel(player.uniqueId).tier
            val leaveLocation = device.location.clone().add(0.5, 1.0, 0.5)
            val startLocation = dungeonGenerationService.generateDungeon(player, mapTier, leaveLocation)

            val dungeonOpenEvent = DungeonOpenEvent(player, startLocation.world!!)
            EventGateway.apply(dungeonOpenEvent)

            deviceService.closeRemainingPortals(device)
            deviceService.openPortals(device, startLocation)
            deviceService.updateMapDeviceVisual(device)
        }

        logger.info("Created Dungeon in ${time.inWholeMilliseconds} ms")
    }
}