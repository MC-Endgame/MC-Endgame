package de.fuballer.mcendgame.util.extension

import de.fuballer.mcendgame.component.device.DeviceSettings
import de.fuballer.mcendgame.component.device.DeviceType
import org.bukkit.block.Block

object BlockExtension {
    fun Block.getDeviceType(): DeviceType? {
        if (!this.hasMetadata(DeviceSettings.DEVICE_BLOCK_METADATA_KEY)) return null
        return DeviceType.valueOf(this.getMetadata(DeviceSettings.DEVICE_BLOCK_METADATA_KEY).first().asString())
    }
}