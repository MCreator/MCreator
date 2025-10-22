<#include "aiconditions.java.ftl">
<#include "mcitems.ftl">
this.goalSelector.addGoal(${cbi+1}, new TemptGoal(this, ${field$speed}, Ingredient.of(${mappedMCItemToItem(input$item)}), ${field$scared?lower_case})<@conditionCode field$condition/>);