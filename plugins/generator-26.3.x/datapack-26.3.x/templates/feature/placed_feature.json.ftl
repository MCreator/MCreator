{
  "feature": <#if featuretype == "configured_feature_reference">${configurationcode}<#else>"${modid}:${registryname}"</#if>,
  "placement": [
    ${placementcode?remove_ending(",")}
  ]
}