<#if (data.tameable && data.breedable)>
<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${cbi+1}, new OwnerHurtTargetGoal(this)<@conditionCode field$condition/>);
</#if>