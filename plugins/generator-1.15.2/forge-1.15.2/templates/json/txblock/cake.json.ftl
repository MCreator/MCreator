<#-- @formatter:off -->
{
    "textures": {
         <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",
         <#else> "particle": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}",</#if>
        "bottom": "${modid}:blocks/${data.texture}",
        "top": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
        "side": "${modid}:blocks/${data.textureFront?has_content?then(data.textureFront, data.texture)}"
    },
    "elements": [
        {   "from": [ 1, 0, 1 ],
            "to": [ 15, 8, 15 ],
            "faces": {
                "down":  { "texture": "#bottom", "cullface": "down" },
                "up":    { "texture": "#top" },
                "north": { "texture": "#side" },
                "south": { "texture": "#side" },
                "west":  { "texture": "#side" },
                "east":  { "texture": "#side" }
            }
        }
    ]
}

<#-- @formatter:on -->