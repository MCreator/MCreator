<#if !data.flyingMob && !data.waterMob>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new BreakDoorGoal(this, e -> true)<@conditionCode field$condition/>);
</#if>