package de.fuballer.mcendgame.component.device.trial_device

import de.fuballer.mcendgame.event.DiscoverRecipeAddEvent
import de.fuballer.mcendgame.event.EventGateway
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.framework.stereotype.LifeCycleListener
import de.fuballer.mcendgame.util.PluginUtil
import org.bukkit.plugin.java.JavaPlugin

@Service
class TrialDeviceRecipeService : LifeCycleListener {
    override fun initialize(plugin: JavaPlugin) {
        val key = PluginUtil.createNamespacedKey(TrialDeviceSettings.TRIAL_DEVICE_ITEM_KEY)
        val recipe = TrialDeviceSettings.getTrialDeviceCraftingRecipe(key)

        val discoverRecipeAddEvent = DiscoverRecipeAddEvent(key, recipe)
        EventGateway.apply(discoverRecipeAddEvent)
    }
}