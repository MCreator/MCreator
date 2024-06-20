{
    "parent": "block/door_bottom_left_open",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "bottom": "${modid}:block/${data.texture}",
        "top": "${modid}:block/${data.textureTop()}"
    },
    "render_type": "${data.getRenderType()}"
}
