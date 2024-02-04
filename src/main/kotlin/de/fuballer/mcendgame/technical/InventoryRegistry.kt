package de.fuballer.mcendgame.technical

import de.fuballer.mcendgame.domain.CustomInventoryType
import org.bukkit.inventory.Inventory

object InventoryRegistry {
    private val map = HashMap<Inventory, CustomInventoryType>();

    fun register(inventory: Inventory, type: CustomInventoryType) {
        map[inventory] = type
    }

    fun isType(inventory: Inventory, type: CustomInventoryType) = map[inventory] == type
}