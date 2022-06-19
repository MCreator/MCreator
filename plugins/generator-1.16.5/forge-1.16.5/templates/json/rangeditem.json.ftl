<#include "../textures.ftl">
{
    "parent": "item/generated",
    "textures": {
      "layer0": "${mappedSingleTexture(data.texture, "items", modid)}"
    },
    "display": {
      "thirdperson_righthand": {
        "rotation": [
          -80,
          260,
          -40
        ],
        "translation": [
          -1,
          -2,
          2.5
        ],
        "scale": [
          1,
          1,
          1
        ]
      },
      "thirdperson_lefthand": {
        "rotation": [
          -80,
          -280,
          40
        ],
        "translation": [
          -1,
          -2,
          2.5
        ],
        "scale": [
          1,
          1,
          1
        ]
      },
      "firstperson_righthand": {
        "rotation": [
          0,
          -90,
          25
        ],
        "translation": [
          1.13,
          3.2,
          1.13
        ],
        "scale": [
          1,
          1,
          1
        ]
      },
      "firstperson_lefthand": {
        "rotation": [
          0,
          90,
          -25
        ],
        "translation": [
          1.13,
          3.2,
          1.13
        ],
        "scale": [
          1,
          1,
          1
        ]
      }
    }
}