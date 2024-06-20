{
    "parent": "block/button_pressed",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "texture": "${modid}:block/${data.texture}"
    },
    "render_type": "${data.getRenderType()}"
}