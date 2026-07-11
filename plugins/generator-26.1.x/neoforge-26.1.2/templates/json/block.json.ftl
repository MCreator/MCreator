{
  "parent": "block/${var_model}",
  "textures": {
    "${var_txname}": "${data.texture.format("%s:block/%s")}",
    "particle": "${(parent???then(data.getParticleTexture(parent.getParticleTexture()), data.getParticleTexture())).format("%s:block/%s")}"
  }
}