package de.fuballer.mcendgame.component.dungeon.boss

import de.fuballer.mcendgame.component.dungeon.boss.db.DungeonBossesRepository
import de.fuballer.mcendgame.component.dungeon.enemy.EnemyHealingService.Companion.healOnLoad
import de.fuballer.mcendgame.component.dungeon.modifier.ModifierUtil.addModifier
import de.fuballer.mcendgame.component.portal.PortalService
import de.fuballer.mcendgame.component.world.db.ManagedWorldRepository
import de.fuballer.mcendgame.component.world.db.dungeon.ManagedDungeonWorldEntity
import de.fuballer.mcendgame.event.DungeonCompleteEvent
import de.fuballer.mcendgame.event.DungeonEntityDeathEvent
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.extension.EntityExtension.getPortalLocation
import de.fuballer.mcendgame.util.extension.EntityExtension.isBoss
import de.fuballer.mcendgame.util.extension.WorldExtension.isDungeonWorld
import org.bukkit.Sound
import org.bukkit.entity.Creature
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetEvent
import org.bukkit.potion.PotionEffectType

@Service
class DungeonBossService(
    private val dungeonBossesRepo: DungeonBossesRepository,
    private val worldManageRepo: ManagedWorldRepository,
    private val portalService: PortalService
) : Listener {
    @EventHandler
    fun on(event: DungeonEntityDeathEvent) {
        val entity = event.entity
        if (!entity.isBoss()) return

        val bossWorld = entity.world
        val bossesEntity = dungeonBossesRepo.findByWorld(bossWorld) ?: return
        val dungeonWorld = worldManageRepo.getById(bossWorld.name) as ManagedDungeonWorldEntity

        val portalLocation = entity.getPortalLocation()!!
        portalLocation.world = entity.world
        portalService.createPortal(portalLocation, bossesEntity.leaveLocation)

        empowerOtherBosses(bossesEntity.bosses)

        if (bossesEntity.progressGranted) return

        bossesEntity.progressGranted = true
        dungeonBossesRepo.save(bossesEntity)

        bossWorld.players.forEach { it.playSound(it, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.5f, 1.3f) }

        val dungeonCompleteEvent = DungeonCompleteEvent(dungeonWorld.player, bossesEntity.mapTier, bossWorld)
        EventGateway.apply(dungeonCompleteEvent)
    }

    @EventHandler
    fun on(event: EntityTargetEvent) {
        val entity = event.entity
        if (!entity.world.isDungeonWorld()) return
        if (!entity.isBoss()) return

        val boss = entity as LivingEntity
        boss.removePotionEffect(PotionEffectType.SLOWNESS)
    }

    private fun empowerOtherBosses(bosses: List<Creature>) {
        bosses.filter { it.isValid }
            .forEach {
                DungeonBossSettings.empowerStats(it)

                it.addModifier(DungeonBossSettings.EMPOWERED_LOOT_MODIFIER)

                it.healOnLoad()
            }
    }
}