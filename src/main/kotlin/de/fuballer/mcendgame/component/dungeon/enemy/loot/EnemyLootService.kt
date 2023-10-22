package de.fuballer.mcendgame.component.dungeon.enemy.loot

import de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.data.DataTypeKeys
import de.fuballer.mcendgame.framework.annotation.Component
import de.fuballer.mcendgame.util.PersistentDataUtil
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent

@Component
class EnemyLootService : Listener {
    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (PersistentDataUtil.getValue(event.entity, DataTypeKeys.DROP_BASE_LOOT) != false) return

        event.drops.clear()
    }
}
