{
<#if data.blockingRenderType == 0>
  "parent": "${modid}:item/handheld",
  "textures": {
    "layer0": "${modid}:items/${data.texture}"
  },
  "display": {
    "thirdperson_righthand": {
        "rotation": [ 45, -35, 0 ]
    },
    "thirdperson_lefthand": {
        "rotation": [ 45, -35, 0 ]
    },
    "firstperson_righthand": {
        "rotation": [ 0, 0, 5 ],
        "translation": [ -5, 2, -1 ]
    },
    "firstperson_lefthand": {
        "rotation": [ 0, 0, 5 ],
        "translation": [ -5, 2, -1 ]
    }
  }
}
<#elseif data.blockingRenderType == 1>
  "parent": "${modid}:custom/${data.blockingModelName.split(":")[0]}",
  "textures": {
    <@textures data.getBlockingTextureMap()/>
    "particle": "${modid}:items/${data.texture}"
  }
}
<#else>
  "forge_marker": 1,
  "parent": "forge:item/default",
  "loader": "forge:obj",
  "model": "${modid}:models/item/${data.blockingModelName.split(":")[0]}.obj",
  "textures": {
    <@textures data.getBlockingTextureMap()/>
    "particle": "${modid}:items/${data.texture}"
  }
}
</#if>

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}",
        </#list>
    </#if>
</#macro>