{
  "parent": "block/cube",
  "textures": {
    "down": "${modid}:block/${data.texture}",
    "up": "${modid}:block/${data.textureTop()}",
    "north": "${modid}:block/${data.textureFront()}",
    "east": "${modid}:block/${data.textureLeft()}",
    "south": "${modid}:block/${data.textureBack()}",
    "west": "${modid}:block/${data.textureRight()}",
    "particle": "${modid}:block/${data.getParticleTexture()}"
  },
  "render_type": "${data.getRenderType()}"
}