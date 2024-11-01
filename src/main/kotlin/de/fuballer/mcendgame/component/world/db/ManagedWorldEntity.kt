package de.fuballer.mcendgame.component.world.db

import de.fuballer.mcendgame.framework.stereotype.Entity
import org.bukkit.World

open class ManagedWorldEntity(
    override var id: String,

    var world: World,
    var deleteTimer: Int = 0
) : Entity<String>