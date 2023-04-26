{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:items/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:items/${data.texture}"
        </#if>
    }
}