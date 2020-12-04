{
    "parent" : "block/lever",
    "textures": {
         <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",
         <#else> "particle": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}",</#if>
        "base": "${modid}:blocks/${data.texture}",
        "lever": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}"
    }
}
