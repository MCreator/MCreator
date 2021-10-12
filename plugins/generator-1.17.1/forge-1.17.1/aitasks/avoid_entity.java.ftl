<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1},
        new AvoidEntityGoal<>(this, ${field$entity?replace("CUSTOM:", "")}Entity.class, (float)${field$radius}, ${field$farspeed}, ${field$nearspeed})<@conditionCode field$condition/>);