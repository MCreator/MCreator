{
    "parent": "item/generated",
    "textures": {
        <#if var_item??>
            "layer0": "${modid}:item/${data.getItemTextureFor(var_item)}"
        <#else>
            "layer0": "${modid}:item/${data.texture}"
        </#if>
    }
}