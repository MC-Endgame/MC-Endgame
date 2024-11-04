package de.fuballer.mcendgame.component.trial.db.unlocks

import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.technical.PersistentMapRepository
import java.util.*

@Service
class TrialUnlockedAllyTypesRepository : PersistentMapRepository<UUID, TrialUnlockedAllyTypesEntity>()