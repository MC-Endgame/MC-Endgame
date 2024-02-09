package de.fuballer.mcendgame.component.damage.calculators

import de.fuballer.mcendgame.event.DamageCalculationEvent
import de.fuballer.mcendgame.util.DamageUtil
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent

object EntityAttackDamageCalculator : DamageCauseCalculator {
    override val damageType = EntityDamageEvent.DamageCause.ENTITY_ATTACK

    override fun buildDamageEvent(event: EntityDamageByEntityEvent): DamageCalculationEvent? {
        val damageEvent = super.buildDamageEvent(event) ?: return null

        val baseDamage = DamageUtil.getMeleeBaseDamage(damageEvent.player)
        val enchantDamage = DamageUtil.getMeleeEnchantDamage(damageEvent.player, damageEvent.damaged, damageEvent.isDungeonWorld)

        damageEvent.baseDamage.add(baseDamage)
        damageEvent.enchantDamage = enchantDamage
        damageEvent.isCritical = DamageUtil.isMeleeCritical(damageEvent.player)
        damageEvent.criticalRoll = 0.5

        return damageEvent
    }

    override fun getBaseDamage(event: DamageCalculationEvent): Double {
        var damage = DamageUtil.getRawBaseDamage(event) ?: return 0.0
        damage += DamageUtil.getStrengthDamage(event.player)

        if (event.isCritical) {
            damage *= 1 + event.criticalRoll
        }

        damage *= DamageUtil.getAttackCooldownMultiplier(event.player)

        var enchantDamage = event.enchantDamage
        enchantDamage *= DamageUtil.getEnchantAttackCooldownMultiplier(event.player)
        damage += enchantDamage

        return damage
    }
}