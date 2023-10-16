package de.fuballer.mcendgame.component.artifact

import de.fuballer.mcendgame.component.artifact.db.Artifact
import de.fuballer.mcendgame.component.artifact.db.ArtifactRepository
import de.fuballer.mcendgame.component.artifact.db.ArtifactType
import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.Keys
import de.fuballer.mcendgame.component.dungeon.world.db.WorldManageRepository
import de.fuballer.mcendgame.framework.stereotype.Service
import de.fuballer.mcendgame.helper.WorldHelper
import de.fuballer.mcendgame.random.RandomPick
import org.bukkit.entity.Monster
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.text.DecimalFormat
import java.util.*

class ArtifactService(
    private val artifactRepo: ArtifactRepository,
    private val worldManageRepo: WorldManageRepository
) : Service {
    private val random = Random()
    private val format = DecimalFormat("0.#")

    fun getArtifactAsItem(artifact: Artifact): ItemStack {
        val item = ItemStack(ArtifactSettings.ARTIFACT_BASE_TYPE)
        val meta = item.itemMeta ?: return item

        meta.setDisplayName("" + ArtifactSettings.ARTIFACT_TIER_COLORS[artifact.tier] + artifact.type.displayName)
        meta.lore = artifact.type.values[artifact.tier]?.let { getLoreWithValues(artifact.type.displayLore, it) }

        item.itemMeta = meta
        return item
    }

    private fun getLoreWithValues(lore: List<String>, values: List<Double>): List<String> {
        val loreWithVal = lore.toMutableList()

        for (i in values.indices) {
            for (l in lore.indices) {
                loreWithVal[l] = loreWithVal[l].replace("($i)", format.format(values[i]))
            }
        }

        return loreWithVal
    }

    fun onInventoryClick(event: InventoryClickEvent) {
        if (!event.view.title.contains(ArtifactSettings.ARTIFACTS_WINDOW_TITLE, true)) return

        if (WorldHelper.isDungeonWorld(event.whoClicked.world)) {
            event.whoClicked.sendMessage(ArtifactSettings.CANNOT_CHANGE_ARTIFACTS_MESSAGE)
            event.isCancelled = true
            return
        }

        if ((event.currentItem?.let { itemIsArtifact(it) } == false) && event.action != InventoryAction.PLACE_ALL) {
            event.isCancelled = true
            return
        }

        when (event.action) {
            InventoryAction.MOVE_TO_OTHER_INVENTORY -> onMoveToOtherInv(event)
            InventoryAction.PICKUP_ALL -> onItemPickup(event)
            InventoryAction.PLACE_ALL -> onItemPlace(event)
            else -> event.isCancelled = true
        }

        artifactRepo.flush()
    }

    fun onInventoryDrag(event: InventoryDragEvent) {
        if (!event.view.title.contains(ArtifactSettings.ARTIFACTS_WINDOW_TITLE, true)) return
        event.isCancelled = true
    }

    fun onEntityDeath(event: EntityDeathEvent) {
        if (WorldHelper.isNotDungeonWorld(event.entity.world)) return
        if (event.entity !is Monster) return

        if (event.entity.persistentDataContainer.has(Keys.DROP_BASE_LOOT, PersistentDataType.BOOLEAN)) {
            val dropArtifact = event.entity.persistentDataContainer.get(Keys.DROP_BASE_LOOT, PersistentDataType.BOOLEAN)
            if (dropArtifact != null && !dropArtifact) return
        }

        if (random.nextDouble() > ArtifactSettings.ARTIFACT_DROP_CHANCE) return

        val worldName = event.entity.world.name
        val mapTier = worldManageRepo.findById(worldName)?.mapTier ?: 1

        val type = RandomPick.pick(ArtifactSettings.ARTIFACT_TYPES).option
        val tier = RandomPick.pick(ArtifactSettings.ARTIFACT_TIERS, mapTier).option

        val artifactItem = getArtifactAsItem(Artifact(type, tier))
        event.entity.world.dropItemNaturally(event.entity.location, artifactItem)
    }

    private fun onMoveToOtherInv(event: InventoryClickEvent) {
        if ((event.rawSlot < ArtifactSettings.ARTIFACTS_WINDOW_SIZE && event.whoClicked.inventory.firstEmpty() == -1)
            || (event.rawSlot >= ArtifactSettings.ARTIFACTS_WINDOW_SIZE && event.inventory.firstEmpty() == -1)
        ) {
            event.isCancelled = true
            return
        }

        val player = event.whoClicked.uniqueId
        val entity = artifactRepo.findById(player) ?: return

        if (event.rawSlot < ArtifactSettings.ARTIFACTS_WINDOW_SIZE) {
            event.currentItem?.let { item ->
                itemToArtifact(item)?.also {
                    entity.artifacts.remove(it)
                }
            }
        } else {
            event.currentItem?.let { item ->
                itemToArtifact(item)?.also {
                    entity.artifacts.add(it)
                }
            }
        }

        artifactRepo.save(entity)
    }

    private fun onItemPickup(event: InventoryClickEvent) {
        if (event.currentItem?.let { itemIsArtifact(it) } == false) {
            event.isCancelled = true
            return
        }

        if (event.rawSlot >= ArtifactSettings.ARTIFACTS_WINDOW_SIZE) return
        val player = event.whoClicked.uniqueId
        val entity = artifactRepo.findById(player) ?: return


        event.currentItem?.let { item ->
            itemToArtifact(item)?.let {
                entity.artifacts.remove(it)
                artifactRepo.save(entity)
            }
        }
    }

    private fun onItemPlace(event: InventoryClickEvent) {
        if (event.rawSlot >= ArtifactSettings.ARTIFACTS_WINDOW_SIZE) return
        val player = event.whoClicked.uniqueId
        val entity = artifactRepo.findById(player) ?: return

        event.cursor?.let { item ->
            itemToArtifact(item)?.also {
                entity.artifacts.add(it)
                artifactRepo.save(entity)
            }
        }
    }

    private fun itemIsArtifact(item: ItemStack): Boolean {
        if (!item.hasItemMeta()) return false
        if (!item.itemMeta?.hasLore()!!) return false
        return item.itemMeta?.lore?.get(0)?.contains(ArtifactSettings.ARTIFACT_LORE_FIRST_LINE) == true
    }

    private fun itemToArtifact(item: ItemStack): Artifact? {
        val displayName = item.itemMeta?.displayName ?: return null

        var artifactType: ArtifactType? = null
        for (type in ArtifactType.values()) {
            if (displayName.contains(type.displayName)) {
                artifactType = type
                break
            }
        }

        for (i in 0 until ArtifactSettings.ARTIFACT_TIER_COLORS.size) {
            if (displayName.contains("" + ArtifactSettings.ARTIFACT_TIER_COLORS[i])) {
                return artifactType?.let { Artifact(it, i) }
            }
        }

        return artifactType?.let { Artifact(it, 0) }
    }

    fun highestArtifactLevel(player: UUID, artifactType: ArtifactType): Int? {
        val entity = artifactRepo.findById(player) ?: return null

        return entity.artifacts
            .filter { it.type == artifactType }
            .maxByOrNull { it.tier }?.tier
    }
}
