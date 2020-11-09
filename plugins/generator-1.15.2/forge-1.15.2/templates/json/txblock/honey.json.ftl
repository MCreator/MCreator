{   "parent": "block/honey_block",
    "textures": {
         <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",
         <#else> "particle": "${modid}:blocks/${data.texture?has_content?then( data.texture)}",</#if>
        "texture": "${modid}:blocks/${data.texture}"
    }
}