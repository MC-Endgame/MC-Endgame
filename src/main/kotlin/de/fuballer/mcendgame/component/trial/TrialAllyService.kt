package de.fuballer.mcendgame.component.trial

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.TextComponent
import de.fuballer.mcendgame.util.extension.EntityExtension.getCustomEntityType
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setCustomEntityType
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

@Service
class TrialAllyService(

) : Listener {
    @EventHandler
    fun test(event: EntityDeathEvent) { //TODO: remove once the ally items are acquirable normally
        val entity = event.entity
        val customEntityType = entity.getCustomEntityType() ?: return

        val allyItem = createAllyItem(customEntityType)

        entity.world.dropItemNaturally(entity.location, allyItem)
    }

    fun createAllyItem(customEntityType: CustomEntityType): ItemStack {
        val item = ItemStack(customEntityType.spawnEgg)
        val itemMeta = item.itemMeta

        val name = customEntityType.customName ?: customEntityType.type.name
        itemMeta.displayName(TextComponent.create(name, NamedTextColor.GREEN))
        val lore = listOf(TextComponent.create(customEntityType.description, NamedTextColor.GREEN))
        itemMeta.lore(lore)

        item.itemMeta = itemMeta
        item.setCustomEntityType(customEntityType)
        return item
    }
}