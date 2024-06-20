{
  "parent": "forge:item/default",
  "loader": "forge:composite",
  "children": {
    "part1": {
      "loader": "forge:obj",
      "model": "${modid}:models/item/${data.customModelName.split(":")[0]}.obj",
      "emissive_ambient": true
      <#if data.getTextureMap()??>,
        "textures": {
        <#list data.getTextureMap().entrySet() as texture>
          "#${texture.getKey()}": "${modid}:block/${texture.getValue()}"<#sep>,
        </#list>
        }
      </#if>
    }
  },
  "textures": {
    "particle": "${modid}:block/${data.getParticleTexture()}"
  },
  "render_type": "${data.getRenderType()}"
}