{
  "parent": "block/${var_model}",
  "textures": {
    "${var_txname}": "${modid}:block/${data.texture}",
    "particle": "${modid}:block/${data.getParticleTexture()}"
  }
  <#if !(data.blockBase?has_content && data.blockBase == "Leaves")>
  ,"render_type": "${data.getRenderType()}"
  </#if>
}