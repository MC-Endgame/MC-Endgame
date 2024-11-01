package de.fuballer.mcendgame.component.device

import de.fuballer.mcendgame.component.device.map_device.DeviceAction
import de.fuballer.mcendgame.util.ItemCreator
import de.fuballer.mcendgame.util.TextComponent
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setDeviceAction
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

object DeviceSettings {
    const val DEVICE_BLOCK_METADATA_KEY = "DEVICE"

    private val OPEN_PORTALS_ITEM_LINE = TextComponent.create("Open portals", NamedTextColor.GREEN)
    val OPEN_PORTALS_ITEM = ItemCreator.create(
        ItemStack(Material.LIME_STAINED_GLASS_PANE),
        OPEN_PORTALS_ITEM_LINE
    ).apply {
        setDeviceAction(DeviceAction.OPEN)
    }

    private val CLOSE_PORTALS_ITEM_LINE = TextComponent.create("Close portals", NamedTextColor.RED)
    val CLOSE_PORTALS_ITEM = ItemCreator.create(
        ItemStack(Material.RED_STAINED_GLASS_PANE),
        CLOSE_PORTALS_ITEM_LINE
    ).apply {
        setDeviceAction(DeviceAction.CLOSE)
    }

    val FILLER_ITEM = ItemCreator.create(ItemStack(Material.GRAY_STAINED_GLASS_PANE), TextComponent.empty(), listOf())

    val PORTAL_OFFSETS = listOf(
        Vector(-1.0, 0.0, 1.732),
        Vector(1.0, 0.0, 1.732),
        Vector(-1.0, 0.0, -1.732),
        Vector(1.0, 0.0, -1.732),
        Vector(2.0, 0.0, 0.0),
        Vector(-2.0, 0.0, 0.0),
    )
}