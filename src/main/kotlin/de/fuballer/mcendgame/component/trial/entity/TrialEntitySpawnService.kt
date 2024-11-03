package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import de.fuballer.mcendgame.component.dungeon.enemy.EnemyHealingService.Companion.healOnLoad
import de.fuballer.mcendgame.component.dungeon.enemy.equipment.EquipmentGenerationService
import de.fuballer.mcendgame.component.dungeon.enemy.generation.EnemyGenerationService
import de.fuballer.mcendgame.component.dungeon.generation.data.SpawnLocation
import de.fuballer.mcendgame.component.trial.TrialSettings
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialAllyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.extension.EntityExtension.setIsAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.setIsEnemy
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getCustomEntityType
import de.fuballer.mcendgame.util.random.RandomOption
import de.fuballer.mcendgame.util.random.RandomUtil
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@Service
class TrialEntitySpawnService(
    private val trialPartyRepo: TrialPartyRepository,
    private val trialEntitySpawnParticleService: TrialEntitySpawnParticleService,
    private val enemyGenerationService: EnemyGenerationService,
    private val equipmentGenerationService: EquipmentGenerationService,
) {
    fun spawnEntities(world: World, instance: TrialInstanceEntity, random: Random) {
        spawnAllies(world, instance)
        spawnEnemies(world, instance, random)
    }

    fun spawnAllies(world: World, instance: TrialInstanceEntity) {
        val alliesWithSpawnLocation = getAlliesWithSpawnLocation(world, instance)
        trialEntitySpawnParticleService.createAllySpawnParticlesTask(alliesWithSpawnLocation.map { it.second })
        spawnAllies(alliesWithSpawnLocation, TrialSettings.ALLY_SPAWNING_TOTAL_PARTICLE_TIME)
    }

    fun spawnEnemies(world: World, instance: TrialInstanceEntity, random: Random) {
        val enemyLocations = getEnemySpawnLocations(world, instance)
        trialEntitySpawnParticleService.createEnemySpawnParticlesTask(enemyLocations)
        spawnEnemies(enemyLocations, instance.progress, TrialSettings.ENEMY_TYPES, TrialSettings.ENEMY_SPAWNING_TOTAL_PARTICLE_TIME, random)
    }

    private fun getAlliesWithSpawnLocation(
        world: World,
        instance: TrialInstanceEntity,
    ): List<Pair<TrialAllyEntity, Location>> {
        val allies = getToSpawnAllies(world)

        val locations = getRandomLocations(world, instance.allySpawnLocations, allies.size)
        return allies.zip(locations)
    }

    private fun getToSpawnAllies(world: World) = world.players
        .mapNotNull { player -> trialPartyRepo.findById(player.uniqueId) }
        .flatMap { party -> getToSpawnAllies(party) }

    private fun getToSpawnAllies(party: TrialPartyEntity) = party.allies.values.filter { isSpawnableAlly(it) }

    private fun isSpawnableAlly(ally: TrialAllyEntity) = ally.typeItem.getCustomEntityType() != null

    private fun getEnemySpawnLocations(
        world: World,
        instance: TrialInstanceEntity,
    ): List<Location> {
        val enemyCount = TrialSettings.getEnemyCount(world.playerCount)
        return getRandomLocations(world, instance.enemySpawnLocations, enemyCount)
    }

    private fun getRandomLocations(
        world: World,
        possibleLocations: List<SpawnLocation>,
        amount: Int,
    ): List<Location> {
        val shuffledLocations = possibleLocations.shuffled()
        val spawnLocations = List(amount) { shuffledLocations[it % shuffledLocations.size] }
        return spawnLocations.map { it.location }.map { Location(world, it.x, it.y, it.z) }
    }

    private fun spawnAllies(
        alliesWithSpawnLocation: List<Pair<TrialAllyEntity, Location>>,
        delay: Long,
    ) {
        SchedulingUtil.runTaskLater(delay) {
            alliesWithSpawnLocation.forEach {
                spawnAlly(it.first, it.second)
            }
        }
    }

    private fun spawnAlly(
        ally: TrialAllyEntity,
        location: Location
    ) {
        val typeItem = ally.typeItem
        if (typeItem.type == Material.AIR) return

        val customEntityType = typeItem.getCustomEntityType() ?: return

        val entity = EntityUtil.spawnCustomEntity(customEntityType, location, 1) as? LivingEntity ?: return
        addGearToAlly(entity, ally.gear)

        EntityUtil.setAttribute(entity, Attribute.GENERIC_FOLLOW_RANGE, 100.0)
        entity.isCustomNameVisible = true
        entity.setIsAlly()
        entity.setIsEnemy(false)
    }

    private fun addGearToAlly(
        entity: LivingEntity,
        gear: MutableMap<TrialPartyInventorySlotType, ItemStack>
    ) {
        val equipment = entity.equipment ?: return

        equipment.setItemInMainHand(gear[TrialPartyInventorySlotType.MAIN_HAND])
        equipment.setItemInOffHand(gear[TrialPartyInventorySlotType.OFF_HAND])
        equipment.helmet = gear[TrialPartyInventorySlotType.HELMET]
        equipment.chestplate = gear[TrialPartyInventorySlotType.CHESTPLATE]
        equipment.leggings = gear[TrialPartyInventorySlotType.LEGGINGS]
        equipment.boots = gear[TrialPartyInventorySlotType.BOOTS]
    }

    private fun spawnEnemies(
        locations: List<Location>,
        tier: Int,
        randomEntityTypes: List<RandomOption<CustomEntityType>>,
        delay: Long,
        random: Random,
    ) {
        SchedulingUtil.runTaskLater(delay) {
            locations.forEach {
                spawnEnemy(it, tier, randomEntityTypes, random)
            }
        }
    }

    private fun spawnEnemy(
        location: Location,
        tier: Int,
        randomEntityTypes: List<RandomOption<CustomEntityType>>,
        random: Random,
    ): LivingEntity {
        val entityType = RandomUtil.pick(randomEntityTypes, random).option
        val entity = EntityUtil.spawnCustomEntity(entityType, location, tier) as LivingEntity

        val canBeInvisible = !entityType.hideEquipment
        enemyGenerationService.addEffectsToEnemy(random, entity, tier, canBeInvisible)

        equipmentGenerationService.generate(random, entity, tier, entityType.canHaveWeapons, entityType.isRanged, entityType.canHaveArmor)

        EntityUtil.setAttribute(entity, Attribute.GENERIC_FOLLOW_RANGE, 100.0)
        entity.setIsEnemy()
        entity.healOnLoad()

        return entity
    }
}