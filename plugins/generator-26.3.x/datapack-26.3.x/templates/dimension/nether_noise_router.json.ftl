"noise_router": {
  "temperature": {
    "type": "minecraft:noise",
    "noise": "minecraft:nether/temperature",
    "xz_scale": 0.25,
    "y_scale": 0
  },
  "vegetation": {
    "type": "minecraft:noise",
    "noise": "minecraft:nether/vegetation",
    "xz_scale": 0.25,
    "y_scale": 0
  },
  "continents": 0,
  "erosion": 0,
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
                "from_coordinate": -8,
                "from_value": 0,
                "to_coordinate": 24,
                "to_value": 1
              },
              "first": 2.5,
              "second": {
                "type": "minecraft:lerp",
                "alpha": {
                  "type": "minecraft:gradient",
                  "axis": "y",
                  "from_coordinate": 104,
                  "from_value": 1,
                  "to_coordinate": 128,
                  "to_value": 0
                },
                "first": 0.9375,
                "second": "minecraft:nether/base_3d_noise"
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
    "function": {
      "type": "minecraft:noise",
      "noise": "minecraft:nether/temperature",
      "xz_scale": 0.25,
      "y_scale": 0
    },
    "label": "T"
  },
  {
    "function": {
      "type": "minecraft:noise",
      "noise": "minecraft:nether/vegetation",
      "xz_scale": 0.25,
      "y_scale": 0
    },
    "label": "V"
  }
]
