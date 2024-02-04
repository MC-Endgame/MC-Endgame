package de.fuballer.mcendgame.component.dungeon.enemy.equipment

import de.fuballer.mcendgame.domain.attribute.RollableAttribute
import de.fuballer.mcendgame.domain.equipment.Equipment
import de.fuballer.mcendgame.domain.equipment.ItemEnchantment
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.technical.persistent_data.TypeKeys
import de.fuballer.mcendgame.util.ItemUtil
import de.fuballer.mcendgame.util.PersistentDataUtil
import de.fuballer.mcendgame.util.random.RandomOption
import de.fuballer.mcendgame.util.random.RandomUtil
import de.fuballer.mcendgame.util.random.SortableRandomOption
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import kotlin.random.Random

@Component
class EquipmentGenerationService {
    fun setCreatureEquipment(
        random: Random,
        livingEntity: LivingEntity,
        mapTier: Int,
        weapons: Boolean,
        ranged: Boolean,
        armor: Boolean,
    ) {
        val equipment = livingEntity.equipment!!

        if (weapons) {
            createMainHandItem(random, mapTier, ranged)?.also {
                equipment.setItemInMainHand(it)
                equipment.itemInMainHandDropChance = 0f
            }
            createOffHandItem(random, mapTier)?.also {
                equipment.setItemInOffHand(it)
                equipment.itemInOffHandDropChance = 0f
            }
        }

        if (armor) {
            createRandomSortableEquipment(random, mapTier, EquipmentGenerationSettings.HELMETS)?.also {
                equipment.helmet = it
                equipment.helmetDropChance = 0f
            }
            createRandomSortableEquipment(random, mapTier, EquipmentGenerationSettings.CHESTPLATES)?.also {
                equipment.chestplate = it
                equipment.chestplateDropChance = 0f
            }
            createRandomSortableEquipment(random, mapTier, EquipmentGenerationSettings.LEGGINGS)?.also {
                equipment.leggings = it
                equipment.leggingsDropChance = 0f
            }
            createRandomSortableEquipment(random, mapTier, EquipmentGenerationSettings.BOOTS)?.also {
                equipment.boots = it
                equipment.bootsDropChance = 0f
            }
        }
    }

    private fun createMainHandItem(
        random: Random,
        mapTier: Int,
        ranged: Boolean
    ): ItemStack? {
        if (ranged) return createRangedMainHandItem(random, mapTier)

        val itemProbability = RandomUtil.pick(EquipmentGenerationSettings.MAINHAND_PROBABILITIES, random).option ?: return null
        return createRandomSortableEquipment(random, mapTier, itemProbability)
    }

    private fun createRangedMainHandItem(
        random: Random,
        mapTier: Int
    ): ItemStack? {
        val itemProbability = RandomUtil.pick(EquipmentGenerationSettings.RANGED_MAINHAND_PROBABILITIES, random).option
        return createRandomSortableEquipment(random, mapTier, itemProbability)
    }

    private fun createOffHandItem(
        random: Random,
        mapTier: Int
    ): ItemStack? {
        if (random.nextDouble() < EquipmentGenerationSettings.OFFHAND_OTHER_OVER_MAINHAND_PROBABILITY) {
            return createRandomEquipment(random, mapTier, EquipmentGenerationSettings.OTHER_ITEMS)
        }

        val itemProbability = RandomUtil.pick(EquipmentGenerationSettings.MAINHAND_PROBABILITIES, random).option ?: return null
        return createRandomSortableEquipment(random, mapTier, itemProbability)
    }

    private fun createRandomSortableEquipment(
        random: Random,
        mapTier: Int,
        equipmentProbabilities: List<SortableRandomOption<out Equipment?>>
    ): ItemStack? {
        val rolls = EquipmentGenerationSettings.calculateEquipmentRollTries(mapTier)
        val equipment = RandomUtil.pick(equipmentProbabilities, rolls, random).option ?: return null

        return createEquipment(random, equipment, mapTier)
    }

    private fun createRandomEquipment(
        random: Random,
        mapTier: Int,
        equipmentProbabilities: List<RandomOption<out Equipment>>
    ): ItemStack {
        val equipment = RandomUtil.pick(equipmentProbabilities, random).option
        return createEquipment(random, equipment, mapTier)
    }

    private fun createEquipment(
        random: Random,
        equipment: Equipment,
        mapTier: Int
    ): ItemStack {
        val item = ItemStack(equipment.material)
        val itemMeta = item.itemMeta ?: return item

        addEnchants(random, mapTier, itemMeta, equipment.rollableEnchants)
        addCustomAttributes(random, mapTier, equipment, itemMeta)
        item.itemMeta = itemMeta

        ItemUtil.updateAttributesAndLore(item)

        return item
    }

    private fun addEnchants(
        random: Random,
        mapTier: Int,
        itemMeta: ItemMeta,
        enchants: List<RandomOption<ItemEnchantment>>
    ) {
        repeat(EquipmentGenerationSettings.calculateEnchantTries(mapTier)) {
            val itemEnchantment = RandomUtil.pick(enchants, random).option

            if (itemMeta.getEnchantLevel(itemEnchantment.enchantment) < itemEnchantment.level) {
                itemMeta.addEnchant(itemEnchantment.enchantment, itemEnchantment.level, true)
            }
        }
    }

    private fun addCustomAttributes(
        random: Random,
        mapTier: Int,
        equipment: Equipment,
        itemMeta: ItemMeta
    ) {
        val statAmount = RandomUtil.pick(EquipmentGenerationSettings.STAT_AMOUNTS, mapTier, random).option
        val rollableAttributesCopy = equipment.rollableAttributes.toMutableList()

        val pickedAttributes = mutableListOf<RollableAttribute>()
        repeat(statAmount) {
            if (rollableAttributesCopy.isEmpty()) return@repeat
            val pickedAttribute = RandomUtil.pick(rollableAttributesCopy, random)

            pickedAttributes.add(pickedAttribute.option)
            rollableAttributesCopy.remove(pickedAttribute)
        }

        val rolledAttributes = pickedAttributes.sortedBy { it.type.ordinal }
            .map { it.roll(mapTier) }

        PersistentDataUtil.setValue(itemMeta, TypeKeys.ATTRIBUTES, rolledAttributes)
    }
}