package de.fuballer.mcendgame.component.dungeon.enemy.generation

import de.fuballer.mcendgame.component.dungeon.enemy.equipment.EquipmentGenerationService
import de.fuballer.mcendgame.component.dungeon.generation.DungeonGenerationSettings
import de.fuballer.mcendgame.component.dungeon.generation.data.LayoutTile
import de.fuballer.mcendgame.domain.entity.CustomEntityType
import de.fuballer.mcendgame.domain.technical.persistent_data.TypeKeys
import de.fuballer.mcendgame.event.DungeonEnemySpawnedEvent
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.PersistentDataUtil
import de.fuballer.mcendgame.util.PluginUtil
import de.fuballer.mcendgame.util.WorldUtil
import de.fuballer.mcendgame.util.random.RandomOption
import de.fuballer.mcendgame.util.random.RandomUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityPotionEffectEvent
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.awt.Point
import java.util.*

@Component
class EnemyGenerationService(
    private val equipmentGenerationService: EquipmentGenerationService
) : Listener {
    private val random = Random()

    @EventHandler
    fun onEntityPotionEffect(event: EntityPotionEffectEvent) {
        if (event.cause != EntityPotionEffectEvent.Cause.EXPIRATION) return
        val effect = event.oldEffect ?: return
        if (effect.type != PotionEffectType.LUCK) return

        val entity = event.entity as? LivingEntity ?: return
        if (WorldUtil.isNotDungeonWorld(entity.world)) return

        entity.health = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)!!.value
    }

    fun summonMonsters(
        randomEntityTypes: List<RandomOption<CustomEntityType>>,
        specialEntityTypes: List<RandomOption<CustomEntityType>>,
        layoutTiles: Array<Array<LayoutTile>>,
        startPoint: Point,
        mapTier: Int,
        world: World
    ) {
        val tileList = layoutTiles.indices
            .flatMap { x ->
                layoutTiles[0].indices.map { y ->
                    Point(x, y)
                }
            }
            .filter { startPoint.x != it.x || startPoint.y != it.y }
            .onEach {
                PluginUtil.scheduleTask {
                    val mobCount = EnemyGenerationSettings.calculateMobCount(random)
                    spawnMobs(randomEntityTypes, mobCount, -it.x * 16.0 - 8, -it.y * 16.0 - 8, mapTier, world)
                }
            }

        tileList.shuffled()
            .take(EnemyGenerationSettings.SPECIAL_MOB_COUNT)
            .forEach {
                PluginUtil.scheduleTask {
                    spawnMobs(specialEntityTypes, 1, -it.x * 16.0 - 8, -it.y * 16.0 - 8, mapTier, world, special = true)
                }
            }
    }

    private fun spawnMobs(
        randomEntityTypes: List<RandomOption<CustomEntityType>>,
        amount: Int,
        x: Double,
        z: Double,
        mapTier: Int,
        world: World,
        special: Boolean = false
    ) {
        val entities = mutableSetOf<LivingEntity>()
        for (i in 0 until amount) {
            val entityType = RandomUtil.pick(randomEntityTypes).option
            val entity = EntityUtil.spawnCustomEntity(
                entityType,
                Location(
                    world,
                    x + EnemyGenerationSettings.MOB_XZ_SPREAD * (random.nextDouble() * 2 - 1),
                    DungeonGenerationSettings.MOB_Y_POS,
                    z + EnemyGenerationSettings.MOB_XZ_SPREAD * (random.nextDouble() * 2 - 1)
                ),
                mapTier
            ) as LivingEntity

            equipmentGenerationService.setCreatureEquipment(entity, mapTier, entityType.canHaveWeapons, entityType.isRanged, entityType.canHaveArmor)

            addEffectUntilLoad(entity)
            addTemporarySlowfalling(entity)
            val canBeInvisible = !entityType.hideEquipment
            addEffectsToEntity(entity, mapTier, canBeInvisible)

            if (special) PersistentDataUtil.setValue(entity, TypeKeys.IS_SPECIAL, true)

            entities.add(entity)
        }

        val event = DungeonEnemySpawnedEvent(world, entities)
        EventGateway.apply(event)
    }

    private fun addEffectUntilLoad(entity: LivingEntity) {
        val effect = PotionEffect(PotionEffectType.LUCK, 1, 0, false, false)
        entity.addPotionEffect(effect)
    }

    private fun addTemporarySlowfalling(entity: LivingEntity) {
        val effect = PotionEffect(PotionEffectType.SLOW_FALLING, 40, 0, false, false)
        entity.addPotionEffect(effect)
    }

    fun addEffectsToEntity(
        entity: LivingEntity,
        mapTier: Int,
        canBeInvisible: Boolean,
    ) {
        val effects = mutableListOf(
            RandomUtil.pick(EnemyGenerationSettings.STRENGTH_EFFECTS, mapTier).option,
            RandomUtil.pick(EnemyGenerationSettings.RESISTANCE_EFFECTS, mapTier).option,
            RandomUtil.pick(EnemyGenerationSettings.SPEED_EFFECTS, mapTier).option,
            RandomUtil.pick(EnemyGenerationSettings.FIRE_RESISTANCE_EFFECT, mapTier).option,
        )
        if (canBeInvisible) {
            effects.add(RandomUtil.pick(EnemyGenerationSettings.INVISIBILITY_EFFECT).option)
        }

        val potionEffects = effects.filterNotNull().map { it.getPotionEffect() }

        entity.addPotionEffects(potionEffects)
    }
}
