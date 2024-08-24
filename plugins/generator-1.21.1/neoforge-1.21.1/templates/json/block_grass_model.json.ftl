{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${data.texture.format("%s:block/%s")}",
    "top": "${data.textureTop().format("%s:block/%s")}",
    "side": "${data.textureFront().format("%s:block/%s")}",
    "overlay": "${data.textureLeft().format("%s:block/%s")}",
    "particle": "${data.getParticleTexture().format("%s:block/%s")}"
  },
  "render_type": "${data.getRenderType()}"
}