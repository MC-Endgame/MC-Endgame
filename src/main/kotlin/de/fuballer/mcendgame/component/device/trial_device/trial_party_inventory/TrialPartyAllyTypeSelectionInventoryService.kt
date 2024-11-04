package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.inventory.CustomInventoryType
import de.fuballer.mcendgame.component.trial.db.party.TrialAllyEntity
import de.fuballer.mcendgame.component.trial.db.party.TrialPartyRepository
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyTypesRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.InventoryUtil
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.InventoryExtension.getCustomType
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getCustomEntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

@Service
class TrialPartyAllyTypeSelectionInventoryService(
    private val trialUnlockedAllyTypesRepo: TrialUnlockedAllyTypesRepository,
    private val trialPartyRepo: TrialPartyRepository,
    private val trialAllyTypeItemService: TrialAllyTypeItemService,
) : Listener {
    private var trialPartyInventoryService: TrialPartyInventoryService? = null

    fun openAllyTypeSelectionInventory(
        player: Player,
        allyIndex: Int,
        trialPartyInventoryService: TrialPartyInventoryService
    ) {
        this.trialPartyInventoryService = trialPartyInventoryService

        val holder = TrialPartyAllyTypeSelectionInventoryHolder(allyIndex)
        val inventory = createInventory(player, holder)
        holder.heldInventory = inventory

        player.openInventory(inventory)
    }

    class TrialPartyAllyTypeSelectionInventoryHolder(
        val allyIndex: Int,
        var heldInventory: Inventory? = null,
    ) : InventoryHolder {
        override fun getInventory(): Inventory {
            return heldInventory!!
        }
    }

    @EventHandler
    fun on(event: InventoryClickEvent) {
        val inventory = event.inventory
        if (inventory.getCustomType() != CustomInventoryType.TRIAL_ALLY_TYPE_SELECTION) return
        event.cancel()

        val player = event.whoClicked as? Player ?: return
        val holder = inventory.holder as? TrialPartyAllyTypeSelectionInventoryHolder ?: return

        val party = trialPartyRepo.findById(player.uniqueId) ?: return
        val ally = party.allies[holder.allyIndex] ?: TrialAllyEntity()
        ally.type = event.currentItem?.getCustomEntityType()
        party.allies[holder.allyIndex] = ally
        trialPartyRepo.save(party)

        trialPartyInventoryService!!.openPartyInventory(player)
    }

    private fun createInventory(player: Player, holder: InventoryHolder): Inventory {
        val inventory = InventoryUtil.createInventory(
            holder,
            TrialPartyTypeSelectionInventorySettings.INVENTORY_SIZE,
            TrialPartyTypeSelectionInventorySettings.INVENTORY_TITLE,
            CustomInventoryType.TRIAL_ALLY_TYPE_SELECTION
        )

        fillInventory(inventory, player)

        return inventory
    }

    private fun fillInventory(inventory: Inventory, player: Player) {
        val unlockedAllyTypesEntity = trialUnlockedAllyTypesRepo.findById(player.uniqueId) ?: return
        val unlockedAllyTypes = unlockedAllyTypesEntity.unlockedTypes

        val sortedAllyTypes = unlockedAllyTypes.keys.sortedBy { it.customName ?: it.type.name }

        for ((index, allyType) in sortedAllyTypes.withIndex()) {
            val item = trialAllyTypeItemService.createAllyTypeItem(allyType, unlockedAllyTypes[allyType]!!)
            inventory.setItem(index, item)
        }
    }
}