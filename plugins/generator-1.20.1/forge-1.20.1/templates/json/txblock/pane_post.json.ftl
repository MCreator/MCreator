{
    "parent": "block/template_glass_pane_post",
    "textures": {
        <#if data.particleTexture?? && !data.particleTexture.isEmpty()>"particle": "${data.particleTexture.format("%s:block/%s")}",</#if>
        "edge": "${data.textureTop().format("%s:block/%s")}",
        "pane": "${data.texture.format("%s:block/%s")}"
    },
    "render_type": "${data.getRenderType()}"
}