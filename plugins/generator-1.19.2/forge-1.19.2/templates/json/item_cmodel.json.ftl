{
<#if var_item??> <#-- used by armor where item type is specified (helmet, body, ...) -->
  "parent": "${modid}:custom/${data.getItemCustomModelNameFor(var_item)}",
  "textures": {
    <@textures data.getItemModelTextureMap(var_item)/>
    "particle": "${modid}:items/${data.getItemTextureFor(var_item)}"
  }
<#else>
  <#assign source = (item??)?then(item, data)>
  "parent": "${modid}:custom/${source.customModelName.split(":")[0]}",
  "textures": {
    <@textures (item??)?then(item.getTextureMap(w.getWorkspace()), data.getTextureMap())/>
    "particle": "${modid}:items/${source.texture}"
  }
</#if>
    <#if data?? && data.getModElement().getTypeString() == "item" && data.filterModels()?has_content>,
    "overrides": [
        <#list data.filterModels().entrySet() as model>
        {
            "predicate": {
            <#list model.getKey().split(",") as state>
                "${generator.map(state.split("=")[0], "itemproperties")}": ${state.split("=")[1]}<#sep>,
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
