{
    "parent": "block/pressure_plate_down",
    "textures": {
      <#if data.particleTexture?has_content>"particle": "${modid}:blocks/${data.particleTexture}",</#if>
      "texture": "${modid}:blocks/${data.texture}"
    }
}