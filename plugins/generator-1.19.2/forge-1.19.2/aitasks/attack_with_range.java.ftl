<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new MeleeAttackGoal(this, ${field$speed}, ${field$longmemory?lower_case}) {

    @Override protected double getAttackReachSqr(LivingEntity entity) {
        return ${field$range?number * field$range?number};
    }

    <@conditionCode field$condition false/>

});
