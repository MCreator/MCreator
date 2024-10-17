<#if data.tameable>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new FollowOwnerGoal(this, ${field$speed}, (float) ${field$min_distance}, (float) ${field$max_distance})<@conditionCode field$condition/>);
</#if>