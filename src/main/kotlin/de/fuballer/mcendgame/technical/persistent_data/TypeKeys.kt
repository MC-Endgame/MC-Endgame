package de.fuballer.mcendgame.technical.persistent_data

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.device.DeviceType
import de.fuballer.mcendgame.component.device.map_device.DeviceAction
import de.fuballer.mcendgame.component.item.custom_item.CustomItemType
import de.fuballer.mcendgame.technical.persistent_data.types.*
import de.fuballer.mcendgame.technical.persistent_data.types.attribute.PersistentCustomAttribute
import de.fuballer.mcendgame.technical.persistent_data.types.generic.PersistentEnum
import de.fuballer.mcendgame.technical.persistent_data.types.generic.PersistentList
import de.fuballer.mcendgame.technical.persistent_data.types.generic.PersistentObjectClass
import de.fuballer.mcendgame.util.PluginUtil.createNamespacedKey
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType

object TypeKeys {
    // entities
    val DISABLE_DROP_EQUIPMENT = TypeKey(createNamespacedKey("disable_drop_equipment"), PersistentDataType.BOOLEAN)
    val MAP_TIER = TypeKey(createNamespacedKey("map_tier"), PersistentDataType.INTEGER)
    val IS_MINION = TypeKey(createNamespacedKey("is_minion"), PersistentDataType.BOOLEAN)
    val HIDE_EQUIPMENT = TypeKey(createNamespacedKey("hide_equipment"), PersistentDataType.BOOLEAN)
    val CUSTOM_ENTITY_TYPE = TypeKey(createNamespacedKey("custom_entity_type"), PersistentObjectClass(CustomEntityType::class))
    val IS_ENEMY = TypeKey(createNamespacedKey("is_enemy"), PersistentDataType.BOOLEAN)
    val IS_ALLY = TypeKey(createNamespacedKey("is_enemy"), PersistentDataType.BOOLEAN)
    val IS_LOOT_GOBLIN = TypeKey(createNamespacedKey("is_loot_goblin"), PersistentDataType.BOOLEAN)
    val IS_ELITE = TypeKey(createNamespacedKey("is_elite"), PersistentDataType.BOOLEAN)
    val MINION_IDS = TypeKey(createNamespacedKey("minions"), PersistentList(PersistentUUID))
    val ACTIVE_SUPPORT_WOLF = TypeKey(createNamespacedKey("active_support_wolf"), PersistentActiveSupportWolf)
    val IS_PORTAL = TypeKey(createNamespacedKey("is_portal"), PersistentDataType.BOOLEAN)
    val IS_BOSS = TypeKey(createNamespacedKey("is_boss"), PersistentDataType.BOOLEAN)
    val CAN_USE_ABILITIES = TypeKey(createNamespacedKey("can_use_abilities"), PersistentDataType.BOOLEAN)
    val PORTAL_LOCATION = TypeKey(createNamespacedKey("portal_location"), PersistentLocation)
    val MODIFIERS = TypeKey(createNamespacedKey("modifiers"), PersistentList(PersistentModifier))
    val IS_FORCED_VEHICLE = TypeKey(createNamespacedKey("is_forced_vehicle"), PersistentDataType.BOOLEAN)
    val HIT_COUNT_BASED_HEALTH = TypeKey(createNamespacedKey("hit_count_based_health"), PersistentDataType.INTEGER)
    val CUSTOM_ENTITY_ATTRIBUTES = TypeKey(createNamespacedKey("custom_entity_attributes"), PersistentList(PersistentCustomAttribute))

    // player
    val LAST_DEVICE = TypeKey(createNamespacedKey("last_device"), PersistentUUID)
    val TOTEMS = TypeKey(createNamespacedKey("totems"), PersistentList(PersistentTotem))
    val HEAL_ON_BLOCK_ACTIVATION = TypeKey(createNamespacedKey("heal_on_block_activation"), PersistentDataType.LONG)

    // items
    val CUSTOM_ITEM_TYPE = TypeKey(createNamespacedKey("custom_item_type"), PersistentObjectClass(CustomItemType::class))
    val CUSTOM_ATTRIBUTES = TypeKey(createNamespacedKey("custom_attributes"), PersistentList(PersistentCustomAttribute))
    val UNMODIFIABLE = TypeKey(createNamespacedKey("unmodifiable"), PersistentDataType.BOOLEAN)
    val CRAFTING_ITEM = TypeKey(createNamespacedKey("crafting_item"), PersistentDataType.BOOLEAN)
    val CORRUPTION_ROUNDS = TypeKey(createNamespacedKey("corruption_rounds"), PersistentDataType.INTEGER)
    val ROLL_SACRIFICE_CRAFTING_ITEM = TypeKey(createNamespacedKey("roll_sacrifice_crafting_item"), PersistentDataType.BOOLEAN)
    val REFORGE_CRAFTING_ITEM = TypeKey(createNamespacedKey("reforge_crafting_item"), PersistentDataType.BOOLEAN)
    val ROLL_SHUFFLE_CRAFTING_ITEM = TypeKey(createNamespacedKey("roll_shuffle_crafting_item"), PersistentDataType.BOOLEAN)
    val DUPLICATION_CRAFTING_ITEM = TypeKey(createNamespacedKey("duplication_crafting_item"), PersistentDataType.BOOLEAN)
    val ROLL_RANDOMIZATION_CRAFTING_ITEM = TypeKey(createNamespacedKey("roll_randomization_crafting_item"), PersistentDataType.BOOLEAN)
    val DEVICE = TypeKey(createNamespacedKey("device"), PersistentEnum(DeviceType::class))
    val DEVICE_ACTION = TypeKey(createNamespacedKey("device_action"), PersistentEnum(DeviceAction::class))
    val TOTEM = TypeKey(createNamespacedKey("totem"), PersistentTotem)
    val ALLY_ITEM = TypeKey(createNamespacedKey("ally_item"), PersistentDataType.BOOLEAN)

    class TypeKey<T>(
        val key: NamespacedKey,
        val dataType: PersistentDataType<*, T>
    )
}