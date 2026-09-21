{
  "type": <#if data.hasGenerationConditions()>"${modid}:${registryname}"<#else>"${generator.map(featuretype, "features", 2)?replace("@modid",modid)}"</#if>,
  "config": ${configurationcode}
}