<#include "mcelements.ftl">
{
  "features": [
    <#list input_list$feature as feature>
      ${toPlacedFeature(input_type_list$feature[feature?index], feature)}
    <#sep>,</#list>
  ]
}