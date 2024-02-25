<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new MeleeAttackGoal(this, ${field$speed}, ${field$longmemory?lower_case})<@conditionCode field$condition/>);