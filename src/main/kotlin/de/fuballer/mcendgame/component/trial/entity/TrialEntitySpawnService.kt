package de.fuballer.mcendgame.component.trial.entity

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import de.fuballer.mcendgame.component.dungeon.enemy.EnemyHealingService.Companion.healOnLoad
import de.fuballer.mcendgame.component.dungeon.enemy.equipment.EquipmentGenerationService
import de.fuballer.mcendgame.component.dungeon.enemy.generation.EnemyGenerationService
import de.fuballer.mcendgame.component.dungeon.generation.data.SpawnLocation
import de.fuballer.mcendgame.component.trial.TrialSettings
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyTypesRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.extension.EntityExtension.setIsAlly
import de.fuballer.mcendgame.util.extension.EntityExtension.setIsEnemy
import de.fuballer.mcendgame.util.random.RandomOption
import de.fuballer.mcendgame.util.random.RandomUtil
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.attribute.Attribute
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

@Service
class TrialEntitySpawnService(
    private val trialPartyRepo: TrialPartyRepository,
    private val trialUnlockedAllyTypesRepo: TrialUnlockedAllyTypesRepository,
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
        spawnEnemies(enemyLocations, instance.level, TrialSettings.ENEMY_TYPES, TrialSettings.ENEMY_SPAWNING_TOTAL_PARTICLE_TIME, random)
    }

    private fun getAlliesWithSpawnLocation(
        world: World,
        instance: TrialInstanceEntity,
    ): List<Pair<TrialPlayerOwnedEntity, Location>> {
        val allies = getToSpawnAllies(world)

        val locations = getRandomLocations(world, instance.allySpawnLocations, allies.size)
        return allies.zip(locations)
    }

    private fun getToSpawnAllies(world: World): MutableList<TrialPlayerOwnedEntity> {
        val toSpawnAllies = mutableListOf<TrialPlayerOwnedEntity>()

        for (player in world.players) {
            val party = trialPartyRepo.findById(player.uniqueId) ?: continue
            for (ally in getToSpawnAllies(party)) {
                toSpawnAllies.add(TrialPlayerOwnedEntity(player, ally.type!!, ally.gear))
            }
        }

        return toSpawnAllies
    }

    private fun getToSpawnAllies(party: TrialPartyEntity) = party.allies.values.filter { it.type != null }

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
        alliesWithSpawnLocation: List<Pair<TrialPlayerOwnedEntity, Location>>,
        delay: Long,
    ) {
        SchedulingUtil.runTaskLater(delay) {
            alliesWithSpawnLocation.forEach {
                spawnAlly(it.first, it.second)
            }
        }
    }

    private fun spawnAlly(
        ally: TrialPlayerOwnedEntity,
        location: Location
    ) {
        val allyType = ally.customEntityType
        val allyLevel = getPlayerAllyLevel(ally.player, allyType)

        val entity = EntityUtil.spawnCustomEntity(allyType, location, allyLevel) as? LivingEntity ?: return
        addGearToAlly(entity, ally.gear)

        EntityUtil.setAttribute(entity, Attribute.GENERIC_FOLLOW_RANGE, 100.0)
        entity.isCustomNameVisible = true
        entity.setIsAlly()
        entity.setIsEnemy(false)
    }

    private fun getPlayerAllyLevel(player: Player, customEntityType: CustomEntityType): Int {
        val unlockedAllyTypes = trialUnlockedAllyTypesRepo.findById(player.uniqueId)?.unlockedTypes ?: return 1
        val allyLevel = unlockedAllyTypes[customEntityType] ?: return 1
        return allyLevel.level
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