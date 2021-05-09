<#include "../textures.ftl">
{
    "textures": {
        "particle": "${mappedSingleTexture(data.portalTexture, "blocks", modid)}",
        "portal": "${mappedSingleTexture(data.portalTexture, "blocks", modid)}"
    },
    "elements": [
      {
        "from": [
          6,
          0,
          0
        ],
        "to": [
          10,
          16,
          16
        ],
        "faces": {
          "east": {
            "uv": [
              0,
              0,
              16,
              16
            ],
            "texture": "#portal"
          },
          "west": {
            "uv": [
              0,
              0,
              16,
              16
            ],
            "texture": "#portal"
          }
        }
      }
    ]
}
