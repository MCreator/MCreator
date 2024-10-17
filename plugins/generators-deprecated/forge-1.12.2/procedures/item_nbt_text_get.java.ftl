<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$item, 1)}.hasTagCompound()? ${mappedMCItemToItemStackCode(input$item, 1)}.getTagCompound().getString(${input$tagName}):"")