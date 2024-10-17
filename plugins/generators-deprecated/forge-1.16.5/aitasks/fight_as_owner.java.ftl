<#if (data.tameable && data.breedable)>
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new OwnerHurtTargetGoal(this)<@conditionCode field$condition/>);
</#if>