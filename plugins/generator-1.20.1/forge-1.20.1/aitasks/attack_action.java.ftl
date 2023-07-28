<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${cbi+1}, new HurtByTargetGoal(this)<@conditionCode field$condition/>
		<#if field$callhelp?lower_case == "true">.setAlertOthers()</#if>);