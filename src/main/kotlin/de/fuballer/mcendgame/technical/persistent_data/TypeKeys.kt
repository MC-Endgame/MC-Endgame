package de.fuballer.mcendgame.technical.persistent_data

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.item.custom_item.CustomItemType
import de.fuballer.mcendgame.component.map_device.data.MapDeviceAction
import de.fuballer.mcendgame.technical.persistent_data.types.PersistentArtifact
import de.fuballer.mcendgame.technical.persistent_data.types.PersistentRolledAttribute
import de.fuballer.mcendgame.technical.persistent_data.types.PersistentUUID
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
    val MINION_IDS = TypeKey(createNamespacedKey("minions"), PersistentList(PersistentUUID))
    val IS_SPECIAL = TypeKey(createNamespacedKey("is_special"), PersistentDataType.BOOLEAN)
    val IS_PORTAL = TypeKey(createNamespacedKey("is_portal"), PersistentDataType.BOOLEAN)

    // projectile
    val PROJECTILE_BASE_DAMAGE = TypeKey(createNamespacedKey("projectile_base_damage"), PersistentDataType.DOUBLE)

    // player
    val LAST_MAP_DEVICE = TypeKey(createNamespacedKey("last_map_device"), PersistentUUID)
    val ARTIFACTS = TypeKey(createNamespacedKey("artifacts"), PersistentList(PersistentArtifact))
    val HEAL_ON_BLOCK_ARTIFACT_ACTIVATION = TypeKey(createNamespacedKey("heal_on_block_artifact_activation"), PersistentDataType.LONG)

    // items
    val CUSTOM_ITEM_TYPE = TypeKey(createNamespacedKey("custom_entity_type"), PersistentObjectClass(CustomItemType::class))
    val ATTRIBUTES = TypeKey(createNamespacedKey("attributes"), PersistentList(PersistentRolledAttribute))
    val UNMODIFIABLE = TypeKey(createNamespacedKey("unmodifiable"), PersistentDataType.BOOLEAN)
    val CORRUPTION_ROUNDS = TypeKey(createNamespacedKey("corruption_rounds"), PersistentDataType.INTEGER)
    val MAP_DEVICE = TypeKey(createNamespacedKey("map_device"), PersistentDataType.BOOLEAN)
    val MAP_DEVICE_ACTION = TypeKey(createNamespacedKey("map_device_action"), PersistentEnum(MapDeviceAction::class))
    val ARTIFACT = TypeKey(createNamespacedKey("artifact"), PersistentArtifact)

    class TypeKey<T>(
        val key: NamespacedKey,
        val dataType: PersistentDataType<*, T>
    )
}