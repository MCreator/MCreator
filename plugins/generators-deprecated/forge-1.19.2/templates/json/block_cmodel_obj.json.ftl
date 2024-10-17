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
          "#${texture.getKey()}": "${modid}:blocks/${texture.getValue()}"<#sep>,
        </#list>
        }
      </#if>
    }
  },
  "textures": {
    "particle": "${modid}:blocks/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  },
  "render_type": "${data.getRenderType()}"
}