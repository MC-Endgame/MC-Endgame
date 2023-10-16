package de.fuballer.mcendgame.component.statitem

import de.fuballer.mcendgame.component.corruption.CorruptionSettings
import de.fuballer.mcendgame.domain.equipment.Equipment
import de.fuballer.mcendgame.domain.equipment.ItemEnchantment
import de.fuballer.mcendgame.framework.stereotype.Service
import de.fuballer.mcendgame.helper.AttributeHelper
import de.fuballer.mcendgame.random.RandomOption
import de.fuballer.mcendgame.random.RandomPick
import de.fuballer.mcendgame.random.SortableRandomOption
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Creature
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.inventory.PrepareAnvilEvent
import org.bukkit.event.inventory.PrepareSmithingEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import java.util.*

class StatItemService : Service {
    private val random = Random()

    fun setCreatureEquipment(creature: Creature, mapTier: Int, weapons: Boolean, ranged: Boolean, armor: Boolean) {
        val equipment = creature.equipment!!

        if (weapons) {
            createMainHandItem(mapTier, ranged)?.also {
                equipment.setItemInMainHand(it)
                equipment.itemInMainHandDropChance = 0f
            }
            createOffHandItem(mapTier)?.also {
                equipment.setItemInOffHand(it)
                equipment.itemInOffHandDropChance = 0f
            }
        }

        if (armor) {
            getSortableEquipment(mapTier, StatItemSettings.HELMETS, EquipmentSlot.HEAD)?.also {
                equipment.helmet = it
                equipment.helmetDropChance = 0f
            }
            getSortableEquipment(mapTier, StatItemSettings.CHESTPLATES, EquipmentSlot.CHEST)?.also {
                equipment.chestplate = it
                equipment.chestplateDropChance = 0f
            }
            getSortableEquipment(mapTier, StatItemSettings.LEGGINGS, EquipmentSlot.LEGS)?.also {
                equipment.leggings = it
                equipment.leggingsDropChance = 0f
            }
            getSortableEquipment(mapTier, StatItemSettings.BOOTS, EquipmentSlot.FEET)?.also {
                equipment.boots = it
                equipment.bootsDropChance = 0f
            }
        }
    }

    fun onEnchant(event: EnchantItemEvent) {
        val item = event.item
        val enchants = event.enchantsToAdd
        val damageAllTier = enchants[Enchantment.DAMAGE_ALL] ?: return

        item.addEnchantment(Enchantment.DAMAGE_ALL, damageAllTier)
        val meta = item.itemMeta ?: return
        if (!meta.hasLore()) return

        val equipment = StatItemSettings.MATERIAL_TO_EQUIPMENT[item.type] ?: return
        meta.lore = AttributeHelper.getAttributeLore(equipment, meta, false)
        item.itemMeta = meta
    }

    fun onAnvilPrepare(event: PrepareAnvilEvent) {
        val item = event.result ?: return
        val meta = item.itemMeta ?: return
        if (!meta.hasLore()) return
        val lore = meta.lore ?: return

        val equipment = StatItemSettings.MATERIAL_TO_EQUIPMENT[item.type] ?: return
        meta.lore = AttributeHelper.getAttributeLore(equipment, meta, lore.contains(CorruptionSettings.CORRUPTION_TAG_LORE[0]))
        item.itemMeta = meta
    }

    fun onSmithingPrepare(event: PrepareSmithingEvent) {
        val item = event.result ?: return
        val meta = item.itemMeta ?: return
        if (!meta.hasLore()) return
        val attributes = meta.attributeModifiers ?: return
        val smithingEquipment = StatItemSettings.SMITHING_MAP[item.type] ?: return

        if (!AttributeHelper.removeAttributeBaseStats(smithingEquipment, meta)) return

        val slot = attributes.values().first().slot
        val equipment = StatItemSettings.MATERIAL_TO_EQUIPMENT[item.type] ?: return
        AttributeHelper.addAttributeBaseStats(equipment, meta, slot)
        meta.lore = AttributeHelper.getAttributeLore(equipment, meta, false)
        item.itemMeta = meta
    }

    fun onGrindstoneUse(event: InventoryClickEvent) {
        val inventory = event.inventory
        if (inventory.type != InventoryType.GRINDSTONE || event.rawSlot != 2) return

        val item = inventory.getItem(2) ?: return
        val meta = item.itemMeta ?: return
        if (!meta.hasLore()) return

        val equipment = StatItemSettings.MATERIAL_TO_EQUIPMENT[item.type] ?: return
        meta.lore = AttributeHelper.getAttributeLore(equipment, meta, false)
        item.itemMeta = meta
    }


    private fun createMainHandItem(mapTier: Int, ranged: Boolean): ItemStack? {
        if (ranged) return createSkeletonMainHandItem(mapTier)

        val itemProbability = RandomPick.pick(StatItemSettings.MAINHAND_PROBABILITIES).option ?: return null
        return getSortableEquipment(mapTier, itemProbability, EquipmentSlot.HAND)
    }

    private fun createSkeletonMainHandItem(mapTier: Int): ItemStack? {
        val itemProbability = RandomPick.pick(StatItemSettings.RANGED_MAINHAND_PROBABILITIES).option
        return getSortableEquipment(mapTier, itemProbability, EquipmentSlot.HAND)
    }

    private fun createOffHandItem(mapTier: Int): ItemStack? {
        if (random.nextDouble() < StatItemSettings.OFFHAND_OTHER_OVER_MAINHAND_PROBABILITY) {
            return getUnsortableEquipment(mapTier, StatItemSettings.OTHER_ITEMS)
        }

        val itemProbability = RandomPick.pick(StatItemSettings.MAINHAND_PROBABILITIES).option ?: return null
        return getSortableEquipment(mapTier, itemProbability, EquipmentSlot.HAND)
    }

    private fun getSortableEquipment(
        mapTier: Int,
        equipmentProbabilities: List<SortableRandomOption<out Equipment?>>,
        slot: EquipmentSlot
    ): ItemStack? {
        val equipment = RandomPick.pick(equipmentProbabilities, StatItemSettings.calculateEquipmentRollTries(mapTier)).option ?: return null
        return getEquipment(mapTier, equipment, slot)
    }

    private fun getUnsortableEquipment(
        mapTier: Int,
        equipmentProbabilities: List<RandomOption<out Equipment>>
    ): ItemStack {
        val equipment = RandomPick.pick(equipmentProbabilities).option
        return getEquipment(mapTier, equipment, EquipmentSlot.OFF_HAND)
    }

    private fun getEquipment(
        mapTier: Int,
        equipment: Equipment,
        slot: EquipmentSlot
    ): ItemStack {
        val item = ItemStack(equipment.material)
        val itemMeta = item.itemMeta ?: return item

        enchantItem(mapTier, itemMeta, equipment.enchantOptions)
        addItemStats(mapTier, equipment, itemMeta, slot)

        item.itemMeta = itemMeta
        return item
    }

    private fun enchantItem(
        mapTier: Int,
        itemMeta: ItemMeta,
        enchants: List<RandomOption<ItemEnchantment>>
    ) {
        repeat(StatItemSettings.calculateEnchantTries(mapTier)) {
            val itemEnchantment = RandomPick.pick(enchants).option

            if (itemMeta.getEnchantLevel(itemEnchantment.enchantment) < itemEnchantment.level) {
                itemMeta.addEnchant(itemEnchantment.enchantment, itemEnchantment.level, true)
            }
        }
    }

    private fun addItemStats(
        mapTier: Int,
        equipment: Equipment,
        itemMeta: ItemMeta,
        slot: EquipmentSlot
    ) {
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        AttributeHelper.addAttributeBaseStats(equipment, itemMeta, slot)

        val rolledAttributesCopy = equipment.rolledAttributes.toMutableList()

        val statAmount = RandomPick.pick(StatItemSettings.STAT_AMOUNTS, mapTier).option
        repeat(statAmount) {

            val rolledAttributeOption = RandomPick.pick(rolledAttributesCopy)
            val (attribute, value) = rolledAttributeOption.option

            val random = random.nextDouble()
            val scaling = StatItemSettings.calculateStatValueScaling(random, mapTier)
            val realValue = value * scaling

            itemMeta.addAttributeModifier(
                attribute,
                AttributeModifier(
                    UUID.randomUUID(),
                    attribute.key.key,
                    realValue,
                    AttributeModifier.Operation.ADD_NUMBER,
                    slot
                )
            )

            rolledAttributesCopy.remove(rolledAttributeOption)
        }

        itemMeta.lore = AttributeHelper.getAttributeLore(equipment, itemMeta, false)
    }
}
