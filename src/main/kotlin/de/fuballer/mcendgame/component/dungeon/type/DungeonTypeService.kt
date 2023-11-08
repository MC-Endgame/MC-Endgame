package de.fuballer.mcendgame.component.dungeon.type

import de.fuballer.mcendgame.component.dungeon.type.db.PlayerDungeonTypeEntity
import de.fuballer.mcendgame.component.dungeon.type.db.PlayerDungeonTypeRepository
import de.fuballer.mcendgame.event.DungeonCompleteEvent
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.util.random.RandomUtil
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

@Component
class DungeonTypeService(
    private val playerDungeonTypeRepo: PlayerDungeonTypeRepository
) : Listener {
    @EventHandler
    fun onDungeonComplete(event: DungeonCompleteEvent) {
        playerDungeonTypeRepo.delete(event.player.uniqueId)
    }

    fun getRandomDungeonType(player: Player): DungeonType {
        val playerId = player.uniqueId

        if (playerDungeonTypeRepo.exists(playerId)) {
            return playerDungeonTypeRepo.findById(playerId)!!.dungeonType
        }

        val dungeonType = RandomUtil.pick(DungeonTypeSettings.DUNGEON_TYPE_WEIGHTS).option
        var entity = PlayerDungeonTypeEntity(playerId, dungeonType)

        entity = playerDungeonTypeRepo.save(entity)
        return entity.dungeonType
    }
}
