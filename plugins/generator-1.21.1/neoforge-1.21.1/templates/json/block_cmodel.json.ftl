{
  "parent": "${modid}:custom/${data.customModelName.split(":")[0]}",
  "textures": {
    "all": "${data.texture.format("%s:block/%s")}",
    "particle": "${(parent???then(data.getParticleTexture(parent.getParticleTexture()), data.getParticleTexture())).format("%s:block/%s")}"
      <#if data.getTextureMap()??>
        <#list data.getTextureMap().entrySet() as texture>,
          "${texture.getKey()}": "${texture.getValue().format("%s:block/%s")}"
        </#list>
      </#if>
  },
  "render_type": "${(parent???then(parent, data)).getRenderType()}"
}