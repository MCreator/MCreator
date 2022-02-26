<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new MeleeAttackGoal(this, ${field$speed}, ${field$longmemory?lower_case}) {

	@Override protected double getAttackReachSqr(LivingEntity entity) {
        return (double) (4.0 + entity.getBbWidth() * entity.getBbWidth());
    }

    <@conditionCode field$condition false/>

});