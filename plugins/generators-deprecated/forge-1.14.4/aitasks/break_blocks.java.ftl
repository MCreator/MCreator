<#include "mcitems.ftl">
<#include "aiconditions.java.ftl">
this.goalSelector.addGoal(${customBlockIndex+1}, new BreakBlockGoal(${mappedBlockToBlockStateCode(input$block)}.getBlock(),
        this, ${field$speed}, (int) ${field$y_max})<@conditionCode field$condition/>);