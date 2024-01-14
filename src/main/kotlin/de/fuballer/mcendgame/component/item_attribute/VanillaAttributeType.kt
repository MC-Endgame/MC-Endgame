package de.fuballer.mcendgame.component.item_attribute

import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier

data class VanillaAttributeType(
    val attribute: Attribute,
    val scaleType: AttributeModifier.Operation
)