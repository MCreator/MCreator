<#include "aiconditions.java.ftl">
<#include "mcitems.ftl">
this.goalSelector.addGoal(${cbi+1}, new RemoveBlockGoal(${mappedBlockToBlock(input$block)},
        this, ${field$speed}, (int) ${field$y_max})<@conditionCode field$condition/>);