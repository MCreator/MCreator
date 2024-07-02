{
<#if data.blockingRenderType == 0>
    "parent": "item/handheld",
    "textures": {
        "layer0": "${data.texture.format("%s:item/%s")}"
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
<#elseif data.blockingRenderType == 1>
    "parent": "${modid}:custom/${data.blockingModelName.split(":")[0]}",
    "textures": {
        <@textures data.getBlockingTextureMap()/>
        "particle": "${data.texture.format("%s:item/%s")}"
    }
<#elseif data.blockingRenderType == 2>
    "forge_marker": 1,
    "parent": "neoforge:item/default",
    "loader": "neoforge:obj",
    "model": "${modid}:models/item/${data.blockingModelName.split(":")[0]}.obj",
    "textures": {
        <@textures data.getBlockingTextureMap()/>
        "particle": "${data.texture.format("%s:item/%s")}"
    }
</#if>
}

<#macro textures textureMap>
    <#if textureMap??>
        <#list textureMap.entrySet() as texture>
            "${texture.getKey()}": "${texture.getValue().format("%s:block/%s")}",
        </#list>
    </#if>
</#macro>