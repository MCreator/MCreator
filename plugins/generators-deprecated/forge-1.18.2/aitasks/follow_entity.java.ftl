<#include "aiconditions.java.ftl">
<#if !data.waterMob || data.flyingMob>
this.goalSelector.addGoal(${customBlockIndex+1}, new FollowMobGoal(this, (float)${field$speed}, ${field$maxrange}, ${field$followarea})<@conditionCode field$condition/>);
</#if>