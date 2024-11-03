package de.fuballer.mcendgame.component.trial

import de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory.TrialPartyInventorySlotType
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceEntity
import de.fuballer.mcendgame.component.trial.db.instance.TrialInstanceRepository
import de.fuballer.mcendgame.component.trial.db.party.TrialAllyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.EntityUtil
import de.fuballer.mcendgame.util.SchedulingUtil
import de.fuballer.mcendgame.util.extension.EntityExtension.setIsAlly
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getCustomEntityType
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack

@Service
class TrialService(
    private val trialInstanceRepo: TrialInstanceRepository,
    private val trialPartyRepo: TrialPartyRepository,
) : Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    fun on(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val block = event.clickedBlock ?: return
        if (block.type != TrialSettings.SPAWNER_MATERIAL) return

        val world = event.player.world
        val instance = trialInstanceRepo.findByWorld(world) ?: return
        if (instance.waveActive) return

        instance.waveActive = true

        val alliesWithSpawnLocation = getAlliesWithSpawnLocation(world, instance)
        createAllySpawnParticlesTask(alliesWithSpawnLocation.map { it.second })
        spawnAllies(alliesWithSpawnLocation, TrialSettings.ALLY_SPAWNING_TOTAL_PARTICLE_TIME)
    }

    private fun getAlliesWithSpawnLocation(
        world: World,
        instance: TrialInstanceEntity,
    ): List<Pair<TrialAllyEntity, Location>> {
        val allies = getToSpawnAllies(world)

        val possibleSpawnLocations = instance.allySpawnLocations.shuffled()
        val spawnLocations = List(allies.size) { possibleSpawnLocations[it % possibleSpawnLocations.size] }
        val locations = spawnLocations.map { it.location }.map { Location(world, it.x, it.y, it.z) }
        return allies.zip(locations)
    }

    private fun getToSpawnAllies(world: World) = world.players
        .mapNotNull { player -> trialPartyRepo.findById(player.uniqueId) }
        .flatMap { party -> getToSpawnAllies(party) }

    private fun getToSpawnAllies(party: TrialPartyEntity) = party.allies.values.filter { isSpawnableAlly(it) }

    private fun isSpawnableAlly(ally: TrialAllyEntity) = ally.typeItem.getCustomEntityType() != null

    private fun spawnAllies(
        alliesWithSpawnLocation: List<Pair<TrialAllyEntity, Location>>,
        delay: Long
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

        entity.isCustomNameVisible = true
        entity.setIsAlly()
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

    private fun createAllySpawnParticlesTask(
        locations: List<Location>,
    ) {
        SchedulingUtil.scheduleSyncRepeatingTask(0, TrialSettings.ALLY_SPAWNING_PARTICLE_PERIOD, TrialSettings.ALLY_SPAWNING_PARTICLE_REPEATS) {
            spawnAllySpawnParticles(locations)
        }
    }

    private fun spawnAllySpawnParticles(
        locations: List<Location>,
    ) {
        locations.forEach {
            it.world.spawnParticle(Particle.PORTAL, it, 15, 0.2, 0.7, 0.2, 0.05)
        }
    }
}