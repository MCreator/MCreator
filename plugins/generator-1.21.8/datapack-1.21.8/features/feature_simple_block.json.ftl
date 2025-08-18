<#include "mcitems.ftl">
{
  "to_place": ${mappedBlockToBlockStateProvider(input$block)}
  <#if (field$schedule_tick!"FALSE") == "TRUE">, "schedule_tick": true</#if>
}