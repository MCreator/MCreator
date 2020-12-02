<#if data.tameable>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new OwnerHurtTargetGoal(this)<@conditionCode field$condition/>);
</#if>