{   "parent": "block/honey_block",
    "textures": {
         <#if data.particleTexture?has_content>"particle": "${modid}:blocks${data.particleTexture}",
         <#else> "particle": "${modid}:blocks${data.texture}",</#if>
        "down": "${modid}:blocks${data.texture}",
        "up": "${modid}:blocks${data.textureTop}",
        "side": "${modid}:blocks${data.textureFront}"
    }
}
