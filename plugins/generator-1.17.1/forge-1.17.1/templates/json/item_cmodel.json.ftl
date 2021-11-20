{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    <#if data.getTextureMap()??>
        <#list data.getTextureMap().entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
    "particle": "${modid}:items/${data.texture}"
  }
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