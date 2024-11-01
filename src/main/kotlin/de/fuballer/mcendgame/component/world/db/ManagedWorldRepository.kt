package de.fuballer.mcendgame.component.world.db

import de.fuballer.mcendgame.framework.InMemoryMapRepository
import de.fuballer.mcendgame.framework.annotation.Service

@Service
class ManagedWorldRepository : InMemoryMapRepository<String, ManagedWorldEntity>()