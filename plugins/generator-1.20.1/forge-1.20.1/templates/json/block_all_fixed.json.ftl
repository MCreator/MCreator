{
  "parent": "block/cube",
  "textures": {
    "down": "${data.texture.format("%s:block/%s")}",
    "up": "${data.textureTop().format("%s:block/%s")}",
    "north": "${data.textureFront().format("%s:block/%s")}",
    "east": "${data.textureLeft().format("%s:block/%s")}",
    "south": "${data.textureBack().format("%s:block/%s")}",
    "west": "${data.textureRight().format("%s:block/%s")}",
    "particle": "${data.getParticleTexture().format("%s:block/%s")}"
  },
  "render_type": "${data.getRenderType()}"
}