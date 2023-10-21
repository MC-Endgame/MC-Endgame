package de.fuballer.mcendgame.component.dungeon.enemy.custom_entity.summoner.db

import de.fuballer.mcendgame.framework.AbstractMapRepository
import de.fuballer.mcendgame.framework.annotation.Component
import java.util.*

@Component
class MinionRepository : AbstractMapRepository<UUID, MinionsEntity>()