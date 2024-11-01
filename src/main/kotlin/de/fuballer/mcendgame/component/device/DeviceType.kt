package de.fuballer.mcendgame.component.device

import de.fuballer.mcendgame.component.device.map_device.MapDeviceSettings
import de.fuballer.mcendgame.component.device.trial_device.TrialDeviceSettings
import org.bukkit.inventory.ItemStack

enum class DeviceType(
    val getBlockItem: () -> ItemStack,
) {
    MAP_DEVICE({ MapDeviceSettings.getBlockItem() }),
    TRIAL_DEVICE({ TrialDeviceSettings.getBlockItem() }),
}