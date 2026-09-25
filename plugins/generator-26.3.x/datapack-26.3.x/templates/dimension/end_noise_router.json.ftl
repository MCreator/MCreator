"noise_router": {
  "temperature": 0,
  "vegetation": 0,
  "continents": 0,
  "erosion": "minecraft:end/islands",
  "depth": 0,
  "ridges": 0,
  "chunk_surface_level": 0,
  "final_density": {
    "type": "minecraft:add",
    "left": {
      "type": "minecraft:squeeze",
      "input": {
        "type": "minecraft:interpolated",
        "cell_size_xz": ${data.horizontalNoiseSize * 4},
        "cell_size_y": ${data.verticalNoiseSize * 4},
        "input": {
          "type": "minecraft:mul",
          "left": {
            "type": "minecraft:blend_density",
            "input": {
              "type": "minecraft:lerp",
              "alpha": {
                "type": "minecraft:gradient",
                "axis": "y",
                "from_coordinate": 4,
                "from_value": 0,
                "to_coordinate": 32,
                "to_value": 1
              },
              "first": -0.234375,
              "second": {
                "type": "minecraft:lerp",
                "alpha": {
                  "type": "minecraft:gradient",
                  "axis": "y",
                  "from_coordinate": 56,
                  "from_value": 1,
                  "to_coordinate": 312,
                  "to_value": 0
                },
                "first": -23.4375,
                "second": "minecraft:end/sloped_cheese"
              }
            }
          },
          "right": 0.64
        }
      }
    },
    "right": {
      "type": "minecraft:beardifier"
    }
  }
},
"debug_functions": [
  {
    "function": "minecraft:end/islands",
    "label": "IS"
  }
]
