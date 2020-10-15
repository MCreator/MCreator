{
    "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
    "textures": {
      "all": "${modid}:blocks/${data.texture}",
      "particle": "${modid}:blocks/${data.texture}"
        <#if data.getTextureMap()??>
          <#list data.getTextureMap().entrySet() as texture>
              ,
              "${texture.getKey()}": "${modid}:blocks/${texture.getValue()}"
          </#list>
        </#if>
    }
}