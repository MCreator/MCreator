<#if data.tameable>
<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${customBlockIndex+1}, new OwnerHurtTargetGoal(this)<@conditionCode field$condition/>);
</#if>