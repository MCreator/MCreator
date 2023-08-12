<#include "mcelements.ftl">
{
  <#if field$tries != "128">"tries": ${field$tries},</#if>
  <#if field$xzSpread != "7">"xz_spread": ${field$xzSpread},</#if>
  <#if field$ySpread != "3">"y_spread": ${field$ySpread},</#if>
  "feature": ${toPlacedFeature(input_id$feature, input$feature)}
}