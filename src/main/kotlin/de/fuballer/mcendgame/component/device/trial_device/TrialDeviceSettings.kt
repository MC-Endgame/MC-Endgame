package de.fuballer.mcendgame.component.device.trial_device

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

object TrialDeviceSettings {
    private val ITEM_NAME = TextComponent.create("Trial Device", NamedTextColor.DARK_PURPLE)
    private val ITEM_LORE =
        listOf(
            TextComponent.create("Opens portals to trials"),
            TextComponent.create("packed with monsters and treasures")
        )

    private val ITEM = ItemCreator.create(
        ItemStack(Material.RESPAWN_ANCHOR),
        ITEM_NAME,
        ITEM_LORE
    ).apply {
        setDeviceType(DeviceType.TRIAL_DEVICE)
        setUnmodifiable()
    }

    fun getBlockItem() = ITEM.clone()

    const val TRIAL_DEVICE_ITEM_KEY = "trial_device"

    fun getTrialDeviceCraftingRecipe(key: NamespacedKey) =
        ShapedRecipe(key, ITEM).apply {
            shape("OKO", "KCK", "OKO")
            setIngredient('O', Material.OBSIDIAN)
            setIngredient('K', Material.OMINOUS_TRIAL_KEY)
            setIngredient('C', Material.HEAVY_CORE)
        }

    val INVENTORY_TITLE = TextComponent.create("Trial Device")
}