<#include "aiconditions.java.ftl">
this.targetSelector.addGoal(${customBlockIndex+1},
        new NearestAttackableTargetGoal(this, ${generator.map(field$entity, "entities")}.class, ${field$insight?lower_case},
        ${field$nearby?lower_case})<@conditionCode field$condition/>);