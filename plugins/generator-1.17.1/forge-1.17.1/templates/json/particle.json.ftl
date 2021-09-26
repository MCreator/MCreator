{
  "textures": [
    <#assign tileCount = data.getTextureTileCount()>
    <#if (tileCount > 1)>
        <#list 1..tileCount as i>
            "${modid}:${registryname}_${i}"<#if i?has_next>,</#if>
        </#list>
    <#else>
        "${modid}:${registryname}"
    </#if>
  ]
}