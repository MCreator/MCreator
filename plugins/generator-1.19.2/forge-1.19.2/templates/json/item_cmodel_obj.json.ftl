{
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
<#if var_item??> <#-- used by armor where item type is specified (helmet, body, ...) -->
  "model": "${modid}:models/item/${data.getItemCustomModelNameFor(var_item)}.obj",
  "textures": {
    <@textures data.getItemModelTextureMap(var_item)/>
    "particle": "${modid}:items/${data.getItemTextureFor(var_item)}"
  }
<#else>
  "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
  "textures": {
    <@textures data.getTextureMap()/>
    "particle": "${modid}:items/${data.texture}"
  }
</#if>
    <#if data.getModElement().getTypeString() == "item" && data.modelsMap?has_content>,
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

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
</#macro>
