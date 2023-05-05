"noise_router": {
  "barrier": 0,
  "fluid_level_floodedness": 0,
  "fluid_level_spread": 0,
  "lava": 0,
  "temperature": 0,
  "vegetation": 0,
  "continents": 0,
  "erosion": {
    "type": "minecraft:cache_2d",
    "argument": {
      "type": "minecraft:end_islands"
    }
  },
  "depth": 0,
  "ridges": 0,
  "initial_density_without_jaggedness": {
    "type": "minecraft:add",
    "argument1": -0.234375,
    "argument2": {
      "type": "minecraft:mul",
      "argument1": {
        "type": "minecraft:y_clamped_gradient",
        "from_y": 4,
        "to_y": 32,
        "from_value": 0,
        "to_value": 1
      },
      "argument2": {
        "type": "minecraft:add",
        "argument1": 0.234375,
        "argument2": {
          "type": "minecraft:add",
          "argument1": -23.4375,
          "argument2": {
            "type": "minecraft:mul",
            "argument1": {
              "type": "minecraft:y_clamped_gradient",
              "from_y": 56,
              "to_y": 312,
              "from_value": 1,
              "to_value": 0
            },
            "argument2": {
              "type": "minecraft:add",
              "argument1": 23.4375,
              "argument2": {
                "type": "minecraft:add",
                "argument1": -0.703125,
                "argument2": {
                  "type": "minecraft:cache_2d",
                  "argument": {
                    "type": "minecraft:end_islands"
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "final_density": {
    "type": "minecraft:squeeze",
    "argument": {
      "type": "minecraft:mul",
      "argument1": 0.64,
      "argument2": {
        "type": "minecraft:interpolated",
        "argument": {
          "type": "minecraft:blend_density",
          "argument": {
            "type": "minecraft:add",
            "argument1": -0.234375,
            "argument2": {
              "type": "minecraft:mul",
              "argument1": {
                "type": "minecraft:y_clamped_gradient",
                "from_y": 4,
                "to_y": 32,
                "from_value": 0,
                "to_value": 1
              },
              "argument2": {
                "type": "minecraft:add",
                "argument1": 0.234375,
                "argument2": {
                  "type": "minecraft:add",
                  "argument1": -23.4375,
                  "argument2": {
                    "type": "minecraft:mul",
                    "argument1": {
                      "type": "minecraft:y_clamped_gradient",
                      "from_y": 56,
                      "to_y": 312,
                      "from_value": 1,
                      "to_value": 0
                    },
                    "argument2": {
                      "type": "minecraft:add",
                      "argument1": 23.4375,
                      "argument2": "minecraft:end/sloped_cheese"
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  },
  "vein_toggle": 0,
  "vein_ridged": 0,
  "vein_gap": 0
}