<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${customBlockIndex+1}, new NearestAttackableTargetGoal<>(this, ${field$entity?replace("CUSTOM:", "")}Entity.class, ${field$insight?lower_case},
        ${field$nearby?lower_case})<@conditionCode field$condition/>);