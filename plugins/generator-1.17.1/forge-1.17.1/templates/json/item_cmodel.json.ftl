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
    <#list data.modelsMap.entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().entrySet() as state>
                "${state.getKey()}": ${state.getValue()}<#sep>,
            </#list>
            },
            "model": "${modid}:item/${model.getValue().modelName}_${model?index}"
        }<#sep>,
    </#list>
    ]
    </#if>
}