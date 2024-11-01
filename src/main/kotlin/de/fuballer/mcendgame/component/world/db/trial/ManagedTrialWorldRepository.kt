package de.fuballer.mcendgame.component.world.db.trial

import de.fuballer.mcendgame.component.world.db.ManagedWorldRepository
import de.fuballer.mcendgame.framework.annotation.Service
import de.fuballer.mcendgame.framework.stereotype.Repository

@Service
class ManagedTrialWorldRepository(
    private val managedWorldRepo: ManagedWorldRepository
) : Repository<String, ManagedTrialWorldEntity> {
    override fun findAll(): List<ManagedTrialWorldEntity> = managedWorldRepo.findAll().filterIsInstance<ManagedTrialWorldEntity>()

    override fun delete(entity: ManagedTrialWorldEntity) = findById(entity.id)?.also { managedWorldRepo.delete(it) }

    override fun deleteById(id: String) = findById(id)?.also { managedWorldRepo.delete(it) }

    override fun save(entity: ManagedTrialWorldEntity) = managedWorldRepo.save(entity) as ManagedTrialWorldEntity

    override fun exists(id: String) = managedWorldRepo.findById(id) is ManagedTrialWorldEntity

    override fun getById(id: String) = findById(id)!!

    override fun findById(id: String) = managedWorldRepo.findById(id) as? ManagedTrialWorldEntity
}