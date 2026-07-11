{
  "textures": [
    <#assign tileCount = data.getTextureTileCount()>
    <#if (tileCount > 1)>
        <#list 1..tileCount as i>
            "${modid}:${registryname}_${i}"<#sep>,
        </#list>
    <#else>
        "${modid}:${registryname}"
    </#if>
  ]
}