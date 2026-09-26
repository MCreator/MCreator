<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new MeleeAttackGoal(this, ${field$speed}, ${field$longmemory?lower_case}) {

	@Override protected boolean canPerformAttack(LivingEntity entity) {
		return this.isTimeToAttack() && this.mob.distanceToSqr(entity) < (this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth())
			&& this.mob.getSensing().hasLineOfSight(entity);
	}

	<@conditionCode field$condition false/>

});