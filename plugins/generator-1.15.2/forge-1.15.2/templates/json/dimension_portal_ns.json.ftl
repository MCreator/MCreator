<#include "../textures.ftl">
{
    "textures": {
        "particle": "${mappedSingleTexture(data.portalTexture, "blocks", modid)}",
        "portal": "${mappedSingleTexture(data.portalTexture, "blocks", modid)}"
    },
    "elements": [
      {
        "from": [
          0,
          0,
          6
        ],
        "to": [
          16,
          16,
          10
        ],
        "faces": {
          "north": {
            "uv": [
              0,
              0,
              16,
              16
            ],
            "texture": "#portal"
          },
          "south": {
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