{
    "parent": "block/template_glass_pane_post",
    "textures": {
        <#if data.particleTexture?has_content>"particle": "${modid}:block/${data.particleTexture}",</#if>
        "edge": "${modid}:block/${data.textureTop?has_content?then(data.textureTop, data.texture)}",
        "pane": "${modid}:block/${data.texture}"
    },
    "render_type": "${data.getRenderType()}"
}