package de.fuballer.mcendgame.component.device

import de.fuballer.mcendgame.component.device.db.DeviceEntity
import de.fuballer.mcendgame.component.device.db.DeviceRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.PluginUtil
import de.fuballer.mcendgame.util.extension.BlockExtension.getDeviceType
import de.fuballer.mcendgame.util.extension.EventExtension.cancel
import de.fuballer.mcendgame.util.extension.ItemStackExtension.getDeviceType
import org.bukkit.*
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.meta.Damageable
import org.bukkit.plugin.java.JavaPlugin

@Service
class DeviceBlockService(
    private val deviceRepo: DeviceRepository,
    private val deviceService: DeviceService,
    private val plugin: JavaPlugin
) : Listener {
    @EventHandler
    fun on(event: BlockPlaceEvent) {
        val placedItem = event.itemInHand
        val deviceType = placedItem.getDeviceType() ?: return

        val block = event.block
        val fixedMetadataValue = PluginUtil.createFixedMetadataValue(deviceType.toString())
        block.setMetadata(DeviceSettings.DEVICE_BLOCK_METADATA_KEY, fixedMetadataValue)

        val entity = DeviceEntity(block.location, deviceType)
        deviceRepo.save(entity)

        deviceRepo.flush()
    }

    @EventHandler
    fun on(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block

        val deviceType = block.getDeviceType() ?: return

        block.removeMetadata(DeviceSettings.DEVICE_BLOCK_METADATA_KEY, plugin)

        val location = block.location
        val entity = deviceRepo.findByLocation(location) ?: return

        deviceService.closeRemainingPortals(entity)
        deviceRepo.deleteByLocation(location)
        deviceRepo.flush()

        if (player.gameMode != GameMode.SURVIVAL) return

        useToolInMainhand(player)

        player.setStatistic(Statistic.MINE_BLOCK, block.type, player.getStatistic(Statistic.MINE_BLOCK, block.type) + 1)
        block.type = Material.AIR

        val blockItem = deviceType.getBlockItem()
        block.world.dropItemNaturally(block.location, blockItem)

        event.cancel()
    }

    private fun useToolInMainhand(player: Player) {
        if (player.gameMode == GameMode.CREATIVE) return
        val equipment = player.equipment
        val tool = equipment.itemInMainHand
        val toolMeta = tool.itemMeta as? Damageable ?: return
        val toolType = tool.type

        player.setStatistic(Statistic.USE_ITEM, toolType, player.getStatistic(Statistic.USE_ITEM, toolType) + 1)
        if (toolMeta.hasEnchant(Enchantment.UNBREAKING)) {
            val damageProbability = 1.0 / (toolMeta.getEnchantLevel(Enchantment.UNBREAKING) + 1)
            if (Math.random() < damageProbability) return
        }

        if (toolMeta.damage + 1 > tool.type.maxDurability) {
            equipment.setItemInMainHand(null)
            player.world.playSound(player.location, Sound.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1f, 1f)
            player.setStatistic(Statistic.BREAK_ITEM, toolType, player.getStatistic(Statistic.BREAK_ITEM, toolType) + 1)
        }

        toolMeta.damage += 1
        tool.itemMeta = toolMeta
    }
}