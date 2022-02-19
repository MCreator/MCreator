<#if data.breedable>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new FollowParentGoal(this, ${field$speed})<@conditionCode field$condition/>);
</#if>