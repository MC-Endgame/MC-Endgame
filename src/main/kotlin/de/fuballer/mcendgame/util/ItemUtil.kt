package de.fuballer.mcendgame.util

import de.fuballer.mcendgame.component.corruption.CorruptionSettings
import de.fuballer.mcendgame.component.stat_item.StatItemSettings
import de.fuballer.mcendgame.domain.attribute.ApplicableAttributeType
import de.fuballer.mcendgame.domain.attribute.RolledAttribute
import de.fuballer.mcendgame.domain.equipment.Equipment
import de.fuballer.mcendgame.domain.persistent_data.DataTypeKeys
import org.bukkit.ChatColor
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

object ItemUtil {
    fun addItemAttribute(
        itemMeta: ItemMeta,
        attribute: Attribute,
        value: Double,
        operation: AttributeModifier.Operation
    ) {
        itemMeta.addAttributeModifier(
            attribute,
            AttributeModifier(
                attribute.key.key,
                value,
                operation
            )
        )
    }

    fun isCorrupted(item: ItemStack): Boolean {
        val itemMeta = item.itemMeta ?: return false

        return PersistentDataUtil.getBooleanValue(itemMeta, DataTypeKeys.CORRUPTED)
    }

    fun setCorrupted(item: ItemStack, corrupted: Boolean = true) {
        val newItemMeta = item.itemMeta ?: return

        PersistentDataUtil.setValue(newItemMeta, DataTypeKeys.CORRUPTED, corrupted)
        item.itemMeta = newItemMeta
    }

    fun hasCustomAttributes(item: ItemStack): Boolean {
        val itemMeta = item.itemMeta ?: return false

        val attributes = PersistentDataUtil.getValue(itemMeta, DataTypeKeys.ATTRIBUTES) ?: return false
        return attributes.isNotEmpty()
    }

    fun isVanillaItem(item: ItemStack): Boolean {
        val itemMeta = item.itemMeta ?: return true
        return !itemMeta.hasLore()
    }

    fun updateAttributesAndLore(item: ItemStack) {
        val itemMeta = item.itemMeta ?: return

        val equipment = StatItemSettings.MATERIAL_TO_EQUIPMENT[item.type] ?: return
        val slotLore = equipment.lore
        val baseAttributes = equipment.baseAttributes
        val extraAttributes = PersistentDataUtil.getValue(itemMeta, DataTypeKeys.ATTRIBUTES) ?: listOf()

        val attributes = baseAttributes + extraAttributes
        updateAttributes(itemMeta, attributes)
        updateLore(item, itemMeta, baseAttributes, extraAttributes, slotLore)

        item.itemMeta = itemMeta
    }

    private fun updateAttributes(itemMeta: ItemMeta, attributes: List<RolledAttribute>) {
        val attributeModifiers = itemMeta.attributeModifiers ?: return
        attributeModifiers.forEach { attribute, _ -> itemMeta.removeAttributeModifier(attribute) }

        attributes.filter { it.type.applicableAttributeType != null }
            .forEach {
                addItemAttribute(
                    itemMeta,
                    it.type.applicableAttributeType!!.attribute,
                    it.roll,
                    it.type.applicableAttributeType.scaleType
                )
            }
    }

    private fun updateLore(
        item: ItemStack,
        itemMeta: ItemMeta,
        baseAttributes: List<RolledAttribute>,
        extraAttributes: List<RolledAttribute>,
        slotLore: String
    ) {
        val lore = mutableListOf<String>()

        if (baseAttributes.isNotEmpty()) {
            lore.add(slotLore)
        }
        baseAttributes.forEach {
            val attributeLine = getAttributeLine(itemMeta, it, true)
            lore.add(attributeLine)
        }
        if (extraAttributes.isNotEmpty()) {
            lore.add(Equipment.GENERIC_SLOT_LORE)
        }
        extraAttributes.forEach {
            val attributeLine = getAttributeLine(itemMeta, it, false)
            lore.add(attributeLine)
        }
        if (isCorrupted(item)) {
            lore.add(CorruptionSettings.CORRUPTION_TAG_LORE)
        }
        if (lore.isNotEmpty()) {
            lore.add(0, "")
        }

        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        itemMeta.lore = lore
    }

    private fun getAttributeLine(itemMeta: ItemMeta, attribute: RolledAttribute, isBaseAttribute: Boolean): String {
        var attributeLore = attribute.type.lore(attribute.roll)
        if (!isBaseAttribute) return "${ChatColor.BLUE}$attributeLore"

        val applicableAttribute = attribute.type.applicableAttributeType
            ?: return "${ChatColor.BLUE}$attributeLore"

        if (isNotPlayerBaseAttribute(applicableAttribute)) return "${ChatColor.BLUE}$attributeLore"

        if (applicableAttribute.attribute == Attribute.GENERIC_ATTACK_DAMAGE
            && itemMeta.hasEnchant(Enchantment.DAMAGE_ALL)
        ) {
            val damageIncrease = 0.5 + 0.5 * itemMeta.getEnchantLevel(Enchantment.DAMAGE_ALL)
            attributeLore = attribute.type.lore(attribute.roll + damageIncrease)
        }
        attributeLore = attributeLore.replaceFirstChar { " " }
        return "${ChatColor.DARK_GREEN}$attributeLore"
    }

    private fun isNotPlayerBaseAttribute(applicableAttribute: ApplicableAttributeType) =
        applicableAttribute.attribute != Attribute.GENERIC_ATTACK_DAMAGE
                && applicableAttribute.attribute != Attribute.GENERIC_ATTACK_SPEED
}