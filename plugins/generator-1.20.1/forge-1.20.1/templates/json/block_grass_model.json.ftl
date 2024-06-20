{
  "parent": "block/grass_block",
  "textures": {
    "bottom": "${modid}:block/${data.texture}",
    "top": "${modid}:block/${data.textureTop()}",
    "side": "${modid}:block/${data.textureFront()}",
    "overlay": "${modid}:block/${data.textureLeft()}",
    "particle": "${modid}:block/${data.getParticleTexture()}"
  },
  "render_type": "${data.getRenderType()}"
}