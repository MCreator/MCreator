{
  "parent": "block/${var_model}",
  "textures": {
    "all": "${modid}:block/signs/<#if data.blockBase == "HangingSign">hanging/</#if>${registryname}",
    "particle": "${data.getParticleTexture().format("%s:block/%s")}"
  }
}
