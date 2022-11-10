{
  "parent": "block/${var_model}",
  "textures": {
    "${var_txname}": "${modid}:blocks/${data.texture}",
    "particle": "${modid}:blocks/${data.particleTexture?has_content?then(data.particleTexture, data.texture)}"
  }
  <#if !(data.blockBase?has_content && data.blockBase == "Leaves")>
  ,"render_type": "${data.getRenderType()}"
  </#if>
}