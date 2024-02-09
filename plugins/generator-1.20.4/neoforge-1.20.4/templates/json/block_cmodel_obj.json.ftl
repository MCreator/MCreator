{
  "parent": "neoforge:item/default",
  "loader": "neoforge:composite",
  "children": {
    "part1": {
      "loader": "neoforge:obj",
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
    "particle": "${modid}:block/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  },
  "render_type": "${data.getRenderType()}"
}