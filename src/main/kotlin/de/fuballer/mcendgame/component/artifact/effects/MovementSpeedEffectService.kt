package de.fuballer.mcendgame.component.artifact.effects

import de.fuballer.mcendgame.component.artifact.ArtifactType
import de.fuballer.mcendgame.event.PlayerDungeonJoinEvent
import de.fuballer.mcendgame.event.PlayerDungeonLeaveEvent
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.util.ArtifactUtil
import de.fuballer.mcendgame.util.WorldUtil
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@Component
class MovementSpeedEffectService : Listener {
    @EventHandler
    fun on(event: PlayerJoinEvent) {
        if (WorldUtil.isDungeonWorld(event.player.world)) {
            processJoin(event.player)
        } else {
            processLeave(event.player)
        }
    }

    @EventHandler
    fun on(event: PlayerDungeonJoinEvent) {
        val player = event.player
        processJoin(player)
    }

    @EventHandler
    fun on(event: PlayerDungeonLeaveEvent) {
        val player = event.player
        processLeave(player)
    }

    private fun processJoin(player: Player) {
        val tier = ArtifactUtil.getHighestTier(player, ArtifactType.MOVEMENT_SPEED) ?: return

        val (speedMultiplier) = ArtifactType.MOVEMENT_SPEED.values[tier]!!
        val realSpeedMultiplier = 1 + speedMultiplier / 100.0

        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.1 * realSpeedMultiplier
    }

    private fun processLeave(player: Player) {
        player.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)?.baseValue = 0.1
    }
}