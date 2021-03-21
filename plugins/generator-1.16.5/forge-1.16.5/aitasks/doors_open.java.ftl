<#if !data.flyingMob>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new OpenDoorGoal(this, true)<@conditionCode field$condition/>);
</#if>