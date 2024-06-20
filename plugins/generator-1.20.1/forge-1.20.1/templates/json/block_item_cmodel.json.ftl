<#-- @formatter:off -->
{
    "parent": "item/handheld",
    "textures": {
        <#if data.itemTexture?? && !data.itemTexture.isEmpty()>
        "layer0": "${modid}:item/${data.itemTexture}"
        <#else>
        "layer0": "${modid}:block/${data.texture}"
        </#if>
    }
}
<#-- @formatter:on -->