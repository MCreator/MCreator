{
    "multipart": [
      {
        "apply": {
          "model": "${modid}:block/${registryname}_post"
        }
      },
      {
        "when": {
          "north": true
        },
        "apply": {
          "model": "${modid}:block/${registryname}_side"
        }
      },
      {
        "when": {
          "east": true
        },
        "apply": {
          "model": "${modid}:block/${registryname}_side",
          "y": 90
        }
      },
      {
        "when": {
          "south": true
        },
        "apply": {
          "model": "${modid}:block/${registryname}_side_alt"
        }
      },
      {
        "when": {
          "west": true
        },
        "apply": {
          "model": "${modid}:block/${registryname}_side_alt",
          "y": 90
        }
      },
      {
        "when": {
          "north": false
        },
        "apply": {
          "model": "${modid}:block/${registryname}_noside"
        }
      },
      {
        "when": {
          "east": false
        },
        "apply": {
          "model": "${modid}:block/${registryname}_noside_alt"
        }
      },
      {
        "when": {
          "south": false
        },
        "apply": {
          "model": "${modid}:block/${registryname}_noside_alt",
          "y": 90
        }
      },
      {
        "when": {
          "west": false
        },
        "apply": {
          "model": "${modid}:block/${registryname}_noside",
          "y": 270
        }
      }
    ]
}