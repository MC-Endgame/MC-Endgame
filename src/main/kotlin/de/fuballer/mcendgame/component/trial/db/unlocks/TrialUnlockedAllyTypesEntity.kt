package de.fuballer.mcendgame.component.trial.db.unlocks

import de.fuballer.mcendgame.component.custom_entity.types.CustomEntityType
import de.fuballer.mcendgame.framework.stereotype.Entity
import java.util.*

data class TrialUnlockedAllyTypesEntity(
    override var id: UUID,

    var unlockedTypes: MutableMap<CustomEntityType, TrialUnlockedAllyLevel>,
) : Entity<UUID>