<#-- @formatter:off -->
{
    "parent": "item/handheld",
    "textures": {
        <#if data.itemTexture?has_content>
        "layer0": "${modid}:items/${data.itemTexture}"
        <#else>
        "layer0": "${modid}:block/${data.texture}"
        </#if>
    }
}
<#-- @formatter:on -->