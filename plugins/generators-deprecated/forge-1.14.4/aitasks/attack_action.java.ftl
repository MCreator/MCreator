<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${customBlockIndex+1}, new HurtByTargetGoal(this)<@conditionCode field$condition/>
    <#if field$callhelp?lower_case == "true">.setCallsForHelp(this.getClass())</#if>);