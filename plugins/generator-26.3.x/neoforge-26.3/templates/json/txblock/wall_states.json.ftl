{
  "multipart": [
    {
      "when": {
        "up": "true"
      },
      "apply": {
        "model": "${modid}:block/${registryname}_post"
      }
    },
    {
      "when": {
        "north": "low"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "uvlock": true
      }
    },
    {
      "when": {
        "east": "low"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 90,
        "uvlock": true
      }
    },
    {
      "when": {
        "south": "low"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 180,
        "uvlock": true
      }
    },
    {
      "when": {
        "west": "low"
      },
      "apply": {
        "model": "${modid}:block/${registryname}",
        "y": 270,
        "uvlock": true
      }
    },
    {
      "when": {
        "north": "tall"
      },
      "apply": {
        "model": "${modid}:block/${registryname}_side_tall",
        "uvlock": true
      }
    },
    {
      "when": {
        "east": "tall"
      },
      "apply": {
        "model": "${modid}:block/${registryname}_side_tall",
        "y": 90,
        "uvlock": true
      }
    },
    {
      "when": {
        "south": "tall"
      },
      "apply": {
        "model": "${modid}:block/${registryname}_side_tall",
        "y": 180,
        "uvlock": true
      }
    },
    {
      "when": {
        "west": "tall"
      },
      "apply": {
        "model": "${modid}:block/${registryname}_side_tall",
        "y": 270,
        "uvlock": true
      }
    }
  ]
}