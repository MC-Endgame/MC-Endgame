package de.fuballer.mcendgame.component.device.map_device

import de.fuballer.mcendgame.component.device.DeviceType
import de.fuballer.mcendgame.util.ItemCreator
import de.fuballer.mcendgame.util.TextComponent
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setDeviceType
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setUnmodifiable
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe

object MapDeviceSettings {
    private val ITEM_NAME = TextComponent.create("Map Device", NamedTextColor.DARK_PURPLE)
    private val ITEM_LORE =
        listOf(
            TextComponent.create("Opens portals to dungeons"),
            TextComponent.create("packed with monsters and treasures")
        )

    private val ITEM = ItemCreator.create(
        ItemStack(Material.RESPAWN_ANCHOR),
        ITEM_NAME,
        ITEM_LORE
    ).apply {
        setDeviceType(DeviceType.MAP_DEVICE)
        setUnmodifiable()
    }

    fun getBlockItem() = ITEM.clone()

    const val MAP_DEVICE_ITEM_KEY = "map_device"

    fun getMapDeviceCraftingRecipe(key: NamespacedKey) =
        ShapedRecipe(key, ITEM).apply {
            shape("ONO", "NSN", "ONO")
            setIngredient('O', Material.OBSIDIAN)
            setIngredient('N', Material.NETHERITE_INGOT)
            setIngredient('S', Material.NETHER_STAR)
        }

    val MAP_DEVICE_INVENTORY_TITLE = TextComponent.create("Map Device")
}