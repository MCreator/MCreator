<#include "mcitems.ftl">
{
  "cap_provider": ${mappedBlockToBlockStateProvider(input$cap)},
  "stem_provider": ${mappedBlockToBlockStateProvider(input$stem)}
  <#if field$radius != "2">, "foliage_radius": ${field$radius}</#if>
}