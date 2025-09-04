{
  "textures": {
    "particle": "${data.portalTexture.format("%s:block/%s")}",
    "portal": "${data.portalTexture.format("%s:block/%s")}"
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
  ],
  "render_type": "translucent"
}