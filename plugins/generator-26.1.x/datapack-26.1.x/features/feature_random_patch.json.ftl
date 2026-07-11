<#include "mcelements_json.ftl">
{
  "features": [
    ${toPlacedFeature(input_id$feature, input$feature, patchFeaturePlacement(field$tries, field$xzSpread, field$ySpread))}
  ]
}