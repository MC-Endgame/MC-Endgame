package de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.necromancer

import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.CustomEntityType
import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.MinionRepository
import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.MinionsEntity
import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.summoner.SummonerService
import de.fuballer.mcendgame.framework.stereotype.Service
import org.bukkit.entity.Spellcaster
import org.bukkit.event.entity.EntitySpellCastEvent
import kotlin.math.min

class NecromancerService(
    private val minionRepo: MinionRepository,
    private val summonerService: SummonerService,
) : Service {
    private fun isNecromancer(event: EntitySpellCastEvent) =
        event.entityType == CustomEntityType.NECROMANCER.type
                && event.entity.customName == CustomEntityType.NECROMANCER.customName

    fun onEntitySpellCast(event: EntitySpellCastEvent) {
        if (!isNecromancer(event)) return

        if (event.spell == Spellcaster.Spell.SUMMON_VEX)
            onSummonVexSpell(event)
    }

    private fun onSummonVexSpell(event: EntitySpellCastEvent) {
        val minionsEntity = minionRepo.findById(event.entity.uniqueId)
            ?: MinionsEntity(event.entity.uniqueId)

        updateMinions(minionsEntity)

        val spawnAmount = min(NecromancerSettings.SPAWN_AMOUNT, NecromancerSettings.MAX_MINIONS - minionsEntity.minions.size)
        if (spawnAmount <= 0) {
            event.isCancelled = true
            return
        }

        summonerService.summonMinions(event.entity, CustomEntityType.SKELETON, spawnAmount, true, false, true, NecromancerSettings.MINION_HEALTH)
        event.isCancelled = true
    }

    private fun updateMinions(minionsEntity: MinionsEntity) {
        minionsEntity.minions.removeIf { it.isDead }
    }
}