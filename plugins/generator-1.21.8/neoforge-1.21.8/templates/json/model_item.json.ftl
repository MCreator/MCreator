<#if data.hasGUITexture?? && data.hasGUITexture()>
{
  "model": {
    "type": "minecraft:select",
    "property": "minecraft:display_context",
    "cases": [
      {
        "when": ["gui", "fixed", "ground"],
        "model": {
          "type": "minecraft:model",
          "model": "${modid}:item/${registryname}_gui"
        }
      }
    ],
    "fallback": {
      <@defaultItemModel/>
    }
  }
}
<#else>
{
  "model": {
    <@defaultItemModel/>
  }
}
</#if>

<#macro defaultItemModel>
  <#assign models = (data.getModels??)?then(data.getModels(), [])>
  <#if models?has_content>
    "type": "${modid}:legacy_overrides",
    "overrides": [
      <#list models as model>
      {
        "predicate": [
          <#list model.stateMap.entrySet() as entry>
          {
            "property": {
            <#if entry.getKey().getName() == "damage">
              "property": "minecraft:damage",
              "normalize": true
            <#elseif entry.getKey().getName() == "lefthanded">
              "property": "${modid}:lefthanded"
            <#else>
              "property": "${generator.map(entry.getKey().getPrefixedName(registryname + "/"), "itemproperties")}"
            </#if>
            },
            "value": ${entry.getValue()}
          }<#sep>,
          </#list>
        ],
        "model": {
          <@modelRef model, "_" + model?index, model?index/>
        }
      }<#sep>,
      </#list>
    ],
    "fallback": {
      <@modelRef data/>
    }
  <#else>
    <@modelRef data, var_sufix!""/>
  </#if>
</#macro>

<#macro modelRef model suffix="" itemIndex=-1>
  <#if model.hasCustomJAVAModel?? && model.hasCustomJAVAModel()>
    "type": "minecraft:special",
    "base": "${modid}:item/${registryname}${suffix}",
    "model": {
      "type": "${modid}:${registryname}"
      <#if (itemIndex >= 0)>,
      "index": ${itemIndex}
      </#if>
    }
  <#else>
    "type": "minecraft:model",
    "model": "${modid}:item/${registryname}${suffix}"
  </#if>
</#macro>