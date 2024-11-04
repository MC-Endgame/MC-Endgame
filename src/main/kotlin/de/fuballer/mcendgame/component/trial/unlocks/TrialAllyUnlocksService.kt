package de.fuballer.mcendgame.component.trial.unlocks

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyLevel
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyTypesEntity
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyTypesRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.EntityExtension.getCustomEntityType
import de.fuballer.mcendgame.util.extension.WorldExtension.isTrialWorld
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.random.Random

@Service
class TrialAllyUnlocksService(
    private val trialUnlockedAllyTypesRepo: TrialUnlockedAllyTypesRepository,
) : Listener {
    @EventHandler
    fun on(event: EntityDeathEvent) {
        val world = event.entity.world
        if (!world.isTrialWorld()) return
        if (Random.nextDouble() > TrialAllyUnlockSettings.UNLOCK_RATE) return

        val customType = event.entity.getCustomEntityType() ?: return
        for (player in world.players) {
            unlock(player, customType)
        }
    }

    private fun unlock(player: Player, customType: CustomEntityType) {
        val unlockEntity = trialUnlockedAllyTypesRepo.findById(player.uniqueId) ?: TrialUnlockedAllyTypesEntity(player.uniqueId, mutableMapOf())
        val unlockedTypes = unlockEntity.unlockedTypes
        val unlockLevel = unlockedTypes[customType] ?: TrialUnlockedAllyLevel()

        increaseUnlockProgress(unlockLevel, player, customType)
        unlockedTypes[customType] = unlockLevel
        trialUnlockedAllyTypesRepo.save(unlockEntity)
    }

    private fun increaseUnlockProgress(allyLevel: TrialUnlockedAllyLevel, player: Player, customEntityType: CustomEntityType) {
        allyLevel.progress++
        if (allyLevel.progress >= TrialAllyUnlockSettings.getProgressNeededForNextLevel(allyLevel.level)) {
            allyLevel.progress = 0
            allyLevel.level++

            sendUnlockMessage(player, customEntityType, allyLevel.level)
        }
    }

    private fun sendUnlockMessage(player: Player, customEntityType: CustomEntityType, level: Int) {
        if (level == 1) {
            player.sendMessage(TrialAllyUnlockSettings.getNewUnlockMessage(customEntityType))
            return
        }

        player.sendMessage(TrialAllyUnlockSettings.getAllyLevelUpMessage(customEntityType, level))
    }
}