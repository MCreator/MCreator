<#if data.tameable>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new OwnerHurtByTargetGoal(this)<@conditionCode field$condition/>);
</#if>