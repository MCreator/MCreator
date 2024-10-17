{
    "multipart": [
      {
        "when": {
          "up": "true"
        },
        "apply": {
          "model": "${modid}:${registryname}_post"
        }
      },
      {
        "when": {
          "north": "true"
        },
        "apply": {
          "model": "${modid}:${registryname}",
          "uvlock": true
        }
      },
      {
        "when": {
          "south": "true"
        },
        "apply": {
          "model": "${modid}:${registryname}",
          "y": 180,
          "uvlock": true
        }
      },
      {
        "when": {
          "west": "true"
        },
        "apply": {
          "model": "${modid}:${registryname}",
          "y": 270,
          "uvlock": true
        }
      },
      {
        "when": {
          "east": "true"
        },
        "apply": {
          "model": "${modid}:${registryname}",
          "y": 90,
          "uvlock": true
        }
      }
    ]
}