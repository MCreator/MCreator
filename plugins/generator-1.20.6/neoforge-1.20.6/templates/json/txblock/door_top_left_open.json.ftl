{
    "parent": "block/door_top_left_open",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "bottom": "${data.texture.format("%s:block/%s")}",
        "top": "${data.textureTop().format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}
