<#if !data.flyingMob>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new BreakDoorGoal(this, e -> true)<@conditionCode field$condition/>);
</#if>