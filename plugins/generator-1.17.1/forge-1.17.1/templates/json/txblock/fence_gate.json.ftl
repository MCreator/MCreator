{
    "parent": "block/template_fence_gate",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
      "texture": "${modid}:blocks/${data.texture}"
    }
}