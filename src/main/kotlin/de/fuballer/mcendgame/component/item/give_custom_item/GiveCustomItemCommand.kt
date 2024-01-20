package de.fuballer.mcendgame.component.item.give_custom_item

import de.fuballer.mcendgame.domain.item.CustomItemType
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.framework.stereotype.CommandHandler
import de.fuballer.mcendgame.helper.CommandHelper
import de.fuballer.mcendgame.util.ItemUtil
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

@Component
class GiveCustomItemCommand(
    private val commandHelper: CommandHelper
) : CommandHandler {
    override fun initialize(plugin: JavaPlugin) = plugin.getCommand(GiveCustomItemSettings.COMMAND_NAME)!!.setExecutor(this)

    override fun onCommand(
        sender: CommandSender,
        command: Command,
        label: String,
        args: Array<out String>
    ): Boolean {
        if (sender !is Player) return false
        if (args.size < 2) return false

        val targetPlayer = commandHelper.getOnlinePlayer(sender, args[0]) ?: return true
        val type = CustomItemType.REGISTRY[args[1]] ?: return false

        val rollString = args.getOrNull(2)
        val rollInt = rollString?.toIntOrNull()
            ?.also { if (it !in 0..100) return false }

        val roll = rollInt?.let { it / 100.0 }
        val customItem = ItemUtil.createCustomItem(type, roll)

        targetPlayer.inventory.addItem(customItem)
        return true
    }
}