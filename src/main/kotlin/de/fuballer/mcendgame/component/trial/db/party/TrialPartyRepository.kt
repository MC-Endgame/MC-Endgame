package de.fuballer.mcendgame.component.trial.db.party

import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.technical.PersistentMapRepository
import java.util.*

@Service
class TrialPartyRepository : PersistentMapRepository<UUID, TrialPartyEntity>()