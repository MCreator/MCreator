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
  "type": "minecraft:condition",
  "on_false": {
    "type": "minecraft:model",
    "model": "${modid}:item/${registryname}"
  },
  "on_true": {
    "type": "minecraft:model",
    "model": "${modid}:item/${registryname}_blocking"
  },
  "property": "minecraft:using_item"
</#macro>