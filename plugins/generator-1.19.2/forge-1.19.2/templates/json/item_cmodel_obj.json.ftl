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
  }
    <#if data.modelsMap?has_content>,
    "overrides": [
    <#list data.modelsMap.entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().split(",") as state>
                "${state.split("=")[0]}": ${(state.split("=")[1]?number?float * 1000)?int / 1000}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${registryname}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}