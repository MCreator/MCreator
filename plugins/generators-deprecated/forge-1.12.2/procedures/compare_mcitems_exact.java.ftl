<#include "mcitems.ftl">
(${mappedMCItemToItemStackCode(input$a,1)}.getItem()== ${mappedMCItemToItemStackCode(input$b,1)}.getItem()
        && ${mappedMCItemToItemStackCode(input$a,1)}.getMetadata()== ${mappedMCItemToItemStackCode(input$b,1)}.getMetadata())