{
    "parent" : "block/lever_on",
    "textures": {
         <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
        "base": "${modid}:blocks/${data.texture}",
        "lever": "${modid}:blocks/${data.textureTop?has_content?then(data.textureTop, data.texture)}"
    }
}
