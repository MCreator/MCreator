{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    "all": "${modid}:block/${data.texture}",
    "particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
      <#if data.getTextureMap()??>
        <#list data.getTextureMap().entrySet() as texture>,
          "${texture.getKey()}": "${modid}:block/${texture.getValue()}"
        </#list>
      </#if>
  },
  "render_type": "${data.getRenderType()}"
}