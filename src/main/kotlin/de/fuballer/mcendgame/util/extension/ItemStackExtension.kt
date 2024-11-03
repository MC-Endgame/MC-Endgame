package de.fuballer.mcendgame.util.extension

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.DeviceType
import de.fuballer.mcendgame.component.device.map_device.DeviceAction
import de.fuballer.mcendgame.component.item.attribute.data.CustomAttribute
import de.fuballer.mcendgame.component.item.custom_item.CustomItemType
import de.fuballer.mcendgame.component.totem.data.Totem
import de.fuballer.mcendgame.technical.persistent_data.TypeKeys
import de.fuballer.mcendgame.util.PersistentDataUtil
import org.bukkit.inventory.ItemStack

object ItemStackExtension {
    fun ItemStack.setCustomItemType(value: CustomItemType) = setPersistentData(this, TypeKeys.CUSTOM_ITEM_TYPE, value)
    fun ItemStack.getCustomItemType() = getPersistentData(this, TypeKeys.CUSTOM_ITEM_TYPE)
    fun ItemStack.isCustomItemType() = getCustomItemType() != null

    fun ItemStack.setCustomAttributes(value: List<CustomAttribute>) = setPersistentData(this, TypeKeys.CUSTOM_ATTRIBUTES, value)
    fun ItemStack.getCustomAttributes() = getPersistentData(this, TypeKeys.CUSTOM_ATTRIBUTES)
    fun ItemStack.setUnmodifiable(value: Boolean = true) = setPersistentData(this, TypeKeys.UNMODIFIABLE, value)
    fun ItemStack.isUnmodifiable() = getPersistentDataBoolean(this, TypeKeys.UNMODIFIABLE)
    fun ItemStack.setCorruptionRounds(value: Int) = setPersistentData(this, TypeKeys.CORRUPTION_ROUNDS, value)
    fun ItemStack.getCorruptionRounds() = getPersistentData(this, TypeKeys.CORRUPTION_ROUNDS)
    fun ItemStack.setRollSacrificeCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.ROLL_SACRIFICE_CRAFTING_ITEM, value)
    fun ItemStack.isRollSacrificeCraftingItem() = getPersistentDataBoolean(this, TypeKeys.ROLL_SACRIFICE_CRAFTING_ITEM)
    fun ItemStack.setReforgeCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.REFORGE_CRAFTING_ITEM, value)
    fun ItemStack.isReforgeCraftingItem() = getPersistentDataBoolean(this, TypeKeys.REFORGE_CRAFTING_ITEM)
    fun ItemStack.setRollShuffleCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.ROLL_SHUFFLE_CRAFTING_ITEM, value)
    fun ItemStack.isRollShuffleCraftingItem() = getPersistentDataBoolean(this, TypeKeys.ROLL_SHUFFLE_CRAFTING_ITEM)
    fun ItemStack.setDuplicationCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.DUPLICATION_CRAFTING_ITEM, value)
    fun ItemStack.isDuplicationCraftingItem() = getPersistentDataBoolean(this, TypeKeys.DUPLICATION_CRAFTING_ITEM)
    fun ItemStack.setRollRandomizationCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.ROLL_RANDOMIZATION_CRAFTING_ITEM, value)
    fun ItemStack.isRollRandomizationCraftingItem() = getPersistentDataBoolean(this, TypeKeys.ROLL_RANDOMIZATION_CRAFTING_ITEM)
    fun ItemStack.setCraftingItem(value: Boolean = true) = setPersistentData(this, TypeKeys.CRAFTING_ITEM, value)
    fun ItemStack.isCraftingItem() = getPersistentDataBoolean(this, TypeKeys.CRAFTING_ITEM)
    fun ItemStack.setDeviceType(value: DeviceType) = setPersistentData(this, TypeKeys.DEVICE, value)
    fun ItemStack.getDeviceType() = getPersistentData(this, TypeKeys.DEVICE)
    fun ItemStack.setDeviceAction(value: DeviceAction) = setPersistentData(this, TypeKeys.DEVICE_ACTION, value)
    fun ItemStack.getDeviceAction() = getPersistentData(this, TypeKeys.DEVICE_ACTION)
    fun ItemStack.setTotem(value: Totem) = setPersistentData(this, TypeKeys.TOTEM, value)
    fun ItemStack.getTotem() = getPersistentData(this, TypeKeys.TOTEM)
    fun ItemStack.setCustomEntityType(value: CustomEntityType) = setPersistentData(this, TypeKeys.CUSTOM_ENTITY_TYPE, value)
    fun ItemStack.getCustomEntityType() = getPersistentData(this, TypeKeys.CUSTOM_ENTITY_TYPE)

    private fun <T : Any> setPersistentData(item: ItemStack, key: TypeKeys.TypeKey<T>, value: T) {
        val itemMeta = item.itemMeta!!
        PersistentDataUtil.setValue(itemMeta, key, value)
        item.itemMeta = itemMeta
    }

    private fun <T : Any> getPersistentData(item: ItemStack, key: TypeKeys.TypeKey<T>): T? {
        val itemMeta = item.itemMeta ?: return null
        return PersistentDataUtil.getValue(itemMeta, key)
    }

    private fun getPersistentDataBoolean(
        item: ItemStack,
        key: TypeKeys.TypeKey<Boolean>,
        default: Boolean = false
    ): Boolean {
        val itemMeta = item.itemMeta ?: return default
        return PersistentDataUtil.getBooleanValue(itemMeta, key, default)
    }
}