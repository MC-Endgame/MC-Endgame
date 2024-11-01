package de.fuballer.mcendgame.component.device.db

import de.fuballer.mcendgame.component.device.DeviceType
import de.fuballer.mcendgame.component.portal.db.Portal
import de.fuballer.mcendgame.framework.stereotype.Entity
import org.bukkit.Location
import java.util.*

data class DeviceEntity(
    override var id: UUID,

    val worldName: String,
    val x: Int,
    val y: Int,
    val z: Int,

    val type: DeviceType,

    @Transient
    var location: Location,
    @Transient
    var portals: MutableList<Portal> = mutableListOf(),
) : Entity<UUID> {
    constructor(location: Location, type: DeviceType) : this(
        UUID.randomUUID(),
        location.world!!.name,
        location.blockX,
        location.blockY,
        location.blockZ,
        type,
        location
    )
}
