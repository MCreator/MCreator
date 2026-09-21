<#include "mcelements_json.ftl">
{
  "feature": ${toConfiguredFeature(input_id$feature, input$feature)},
  "placement": [
    /*@extra*/<#if statement$placement != "">,</#if> <#-- Marker for optional extra placement, used by random patch feature -->
    ${statement$placement?remove_ending(",")}
  ]
}