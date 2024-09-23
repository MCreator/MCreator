<#include "mcelements.ftl">
{
  "default": ${toPlacedFeature(input_id$default, input$default)},
  "features": [
    <#list input_list$feature as feature>
    {
      "chance": ${field_list$chance[feature?index]},
      "feature": ${toPlacedFeature(input_id_list$feature[feature?index], feature)}
    }
    <#sep>,</#list>
  ]
}