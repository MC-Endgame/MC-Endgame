package de.fuballer.mcendgame.component.trial.db

import de.fuballer.mcendgame.framework.stereotype.Entity
import java.util.*

data class TrialPartyEntity(
    override var id: UUID,

    var allies: MutableMap<Int, TrialAllyEntity> = mutableMapOf()
) : Entity<UUID>