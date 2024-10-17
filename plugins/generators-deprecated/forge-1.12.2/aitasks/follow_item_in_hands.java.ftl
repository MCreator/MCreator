<#include "mcitems.ftl">
this.tasks.addTask(${customBlockIndex+1},new EntityAITempt(this, ${field$speed}, ${mappedMCItemToItem(input$item)}, ${field$scared?lower_case}));