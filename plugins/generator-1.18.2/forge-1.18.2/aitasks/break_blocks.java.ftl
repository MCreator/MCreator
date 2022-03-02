<#include "mcitems.ftl">
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new RemoveBlockGoal(${mappedBlockToBlock(input$block)},
        this, ${field$speed}, (int) ${field$y_max})<@conditionCode field$condition/>);