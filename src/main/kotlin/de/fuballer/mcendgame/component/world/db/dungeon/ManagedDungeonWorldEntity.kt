package de.fuballer.mcendgame.component.world.db.dungeon

import de.fuballer.mcendgame.component.world.db.ManagedWorldEntity
import org.bukkit.World
import org.bukkit.entity.Player

class ManagedDungeonWorldEntity(
    id: String,
    world: World,

    var player: Player,
) : ManagedWorldEntity(id, world)