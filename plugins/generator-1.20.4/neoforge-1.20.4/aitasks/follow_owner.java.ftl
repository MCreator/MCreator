<#if (data.tameable && data.breedable && (!data.waterMob || data.flyingMob))>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${cbi+1}, new FollowOwnerGoal(this, ${field$speed}, (float) ${field$min_distance}, (float) ${field$max_distance}, false)<@conditionCode field$condition/>);
</#if>