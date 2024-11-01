package de.fuballer.mcendgame.component.world.db.dungeon

import de.fuballer.mcendgame.component.world.db.ManagedWorldRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.framework.stereotype.Repository

@Service
class ManagedDungeonWorldRepository(
    private val managedWorldRepo: ManagedWorldRepository
) : Repository<String, ManagedDungeonWorldEntity> {
    override fun findAll(): List<ManagedDungeonWorldEntity> = managedWorldRepo.findAll().filterIsInstance<ManagedDungeonWorldEntity>()

    override fun delete(entity: ManagedDungeonWorldEntity) = findById(entity.id)?.also { managedWorldRepo.delete(it) }

    override fun deleteById(id: String) = findById(id)?.also { managedWorldRepo.delete(it) }

    override fun save(entity: ManagedDungeonWorldEntity) = managedWorldRepo.save(entity) as ManagedDungeonWorldEntity

    override fun exists(id: String) = managedWorldRepo.findById(id) is ManagedDungeonWorldEntity

    override fun getById(id: String) = findById(id)!!

    override fun findById(id: String) = managedWorldRepo.findById(id) as? ManagedDungeonWorldEntity
}