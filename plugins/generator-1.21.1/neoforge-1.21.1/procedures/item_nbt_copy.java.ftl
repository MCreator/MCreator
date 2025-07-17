<#if !field$ignoredefaults??><#assign field$ignoredefaults = "FALSE"></#if>
<#include "mcitems.ftl">
${mappedMCItemToItemStackCode(input$b, 1)}.applyComponents(${mappedMCItemToItemStackCode(input$a, 1)}.getComponents<#if field$ignoredefaults== "TRUE">Patch</#if>());
