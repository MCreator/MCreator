<#-- @formatter:off -->
{
  "resource_pack_name": "vanilla",
  "texture_name": "atlas.items",
  "texture_data": {
    <#assign textureMap = w.getItemTextureMap().entrySet()>
    <#list textureMap as entry>
      "${entry.getKey()}": {
        "textures": "textures/items/${entry.getValue()}"
      }<#if entry?has_next>,</#if>
    </#list>
  }
}
<#-- @formatter:on -->