package de.fuballer.mcendgame.component.device.trial_device.trial_party_inventory

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.component.trial.db.unlocks.TrialUnlockedAllyLevel
import de.fuballer.mcendgame.component.trial.unlocks.TrialAllyUnlockSettings
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.util.TextComponent
import de.fuballer.mcendgame.util.extension.ItemStackExtension.setCustomEntityType
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.inventory.ItemStack

@Service
class TrialAllyTypeItemService {
    fun createAllyTypeItem(
        customEntityType: CustomEntityType,
        allyLevel: TrialUnlockedAllyLevel
    ): ItemStack {
        val item = ItemStack(customEntityType.spawnEgg)
        val itemMeta = item.itemMeta

        val name = customEntityType.customName ?: customEntityType.type.name
        itemMeta.displayName(TextComponent.create(name, NamedTextColor.GREEN))
        val progressForNextLevel = TrialAllyUnlockSettings.getProgressNeededForNextLevel(allyLevel.level)

        val level = allyLevel.level
        val lore = listOf(
            TextComponent.create(customEntityType.description, NamedTextColor.GREEN),
            TextComponent.create("${customEntityType.baseHealth + level * customEntityType.healthPerTier}❤", NamedTextColor.RED)
                .append { TextComponent.create("  ${customEntityType.baseDamage + level * customEntityType.damagePerTier}⚔", NamedTextColor.BLUE) },
            TextComponent.create("Level: $level (${allyLevel.progress}/$progressForNextLevel)", NamedTextColor.GREEN)
        )
        itemMeta.lore(lore)
        item.itemMeta = itemMeta

        item.setCustomEntityType(customEntityType)
        return item
    }
}