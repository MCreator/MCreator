<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new AvoidEntityGoal(this, ${generator.map(field$entity, "entities")}.class,
            (float)${field$radius}, ${field$farspeed}, ${field$nearspeed})<@conditionCode field$condition/>);