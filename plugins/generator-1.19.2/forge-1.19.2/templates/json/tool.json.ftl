{
    "parent": "item/handheld",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:items/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
<#if data.getModElement().getTypeString() == "tool" && data.toolType == "Shield">,
  "overrides": [
      {
          "predicate": {
              "blocking": 1
          },
          "model": "${modid}:item/${registryname}_blocking"
      }
  ]
</#if>
}