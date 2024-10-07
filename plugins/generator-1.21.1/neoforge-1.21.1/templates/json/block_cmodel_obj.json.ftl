{
  "parent": "neoforge:item/default",
  "loader": "neoforge:composite",
  "children": {
    "part1": {
      "loader": "neoforge:obj",
      "render_type": "${data.getRenderType()}",
      "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
      "emissive_ambient": true
      <#if data.getTextureMap()??>,
        "textures": {
        <#list data.getTextureMap().entrySet() as texture>
          "#${texture.getKey()}": "${texture.getValue().format("%s:block/%s")}"<#sep>,
        </#list>
        }
      </#if>
    }
  },
  "textures": {
    "particle": "${data.getParticleTexture().format("%s:block/%s")}"
  },
  "render_type": "${data.getRenderType()}"
}