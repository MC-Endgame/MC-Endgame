package de.fuballer.mcendgame.component.trial.db

import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.technical.PersistentMapRepository
import java.util.*

@Service
class TrialPartyRepository : PersistentMapRepository<UUID, TrialPartyEntity>()