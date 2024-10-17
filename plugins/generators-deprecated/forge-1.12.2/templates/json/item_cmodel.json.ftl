{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
  <#if data.getTextureMap()??>
      <#list data.getTextureMap().entrySet() as texture>
          "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}"<#if texture?has_next>,</#if>
      </#list>
  </#if>
  }
}