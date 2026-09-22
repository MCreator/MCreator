{
  <#if data.hasGenerationConditions()> <#-- If we have generation conditions, we wrap the original feature with our custom feature -->
  "type": "${modid}:${registryname}",
  "feature": {
  </#if>
  "type": "${generator.map(featuretype, "features", 2)?replace("@modid",modid)}"
  ${configurationcode}
  <#if data.hasGenerationConditions()>}</#if>
}