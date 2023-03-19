<#-- @formatter:off -->
{
    "parent": "minecraft:item/generated",
    "textures": {
        "layer0": "${modid}:<#if data.itemTexture?has_content>items/${data.itemTexture}<#else>blocks/${data.signTexture}</#if>"
    }
}
<#-- @formatter:on -->