{
    "parent": "item/handheld",
    "textures": {
        <#if var_item??>
            "layer0": "${data.getItemTextureFor(var_item).format("%s:item/%s")}"
        <#else>
            "layer0": "${data.texture.format("%s:item/%s")}"
        </#if>
    }
}