package de.fuballer.mcendgame.component.trial.unlocks

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor

object TrialAllyUnlockSettings {
    const val UNLOCK_RATE = 0.5//0.01

    fun getProgressNeededForNextLevel(currentLevel: Int) = currentLevel * 5

    fun getNewUnlockMessage(customEntityType: CustomEntityType) =
        Component.text("You unlocked ", NamedTextColor.GREEN)
            .append(getCustomEntityTextComponent(customEntityType))
            .append(Component.text(" as an ally!", NamedTextColor.GREEN))

    fun getAllyLevelUpMessage(customEntityType: CustomEntityType, level: Int) =
        Component.text("Your ", NamedTextColor.GREEN)
            .append(getCustomEntityTextComponent(customEntityType))
            .append(Component.text(" is now level $level!", NamedTextColor.GREEN))

    private fun getCustomEntityTextComponent(customEntityType: CustomEntityType) =
        Component.text(customEntityType.customName ?: customEntityType.type.name, NamedTextColor.BLUE)
            .hoverEvent(HoverEvent.showText(Component.text(customEntityType.description, NamedTextColor.BLUE)))
}