{
  "multipart": [
    {
      "apply": {
        "model": "${modid}:block/${registryname}_post"
      }
    },
    {
      "when": {
        "north": "true"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "uvlock": true
      }
    },
    {
      "when": {
        "south": "true"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 180,
        "uvlock": true
      }
    },
    {
      "when": {
        "west": "true"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 270,
        "uvlock": true
      }
    },
    {
      "when": {
        "east": "true"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 90,
        "uvlock": true
      }
    }
  ]
}