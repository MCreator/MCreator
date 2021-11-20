{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
  "textures": {
    <#if data.getTextureMap()?has_content>
        <#list data.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${data.texture}"
  }<#--,
  <#if var_type?? && var_type=="tool">
  "transform": "forge:default-tool"
  <#else>
  "transform": "forge:default-item"
  </#if>-->
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap as state, model>
        {
            "predicate": {
            <#list state as property, value>
                "${property}": ${value}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${model.modelName}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}