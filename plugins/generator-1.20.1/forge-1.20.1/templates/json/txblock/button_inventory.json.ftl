{
    "parent": "block/button_inventory",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "texture": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}