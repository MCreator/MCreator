"noise_router": {
  "barrier": 0,
  "fluid_level_floodedness": 0,
  "fluid_level_spread": 0,
  "lava": 0,
  "temperature": {
    "type": "minecraft:shifted_noise",
    "noise": "minecraft:temperature",
    "xz_scale": 0.25,
    "y_scale": 0,
    "shift_x": "minecraft:shift_x",
    "shift_y": 0,
    "shift_z": "minecraft:shift_z"
  },
  "vegetation": {
    "type": "minecraft:shifted_noise",
    "noise": "minecraft:vegetation",
    "xz_scale": 0.25,
    "y_scale": 0,
    "shift_x": "minecraft:shift_x",
    "shift_y": 0,
    "shift_z": "minecraft:shift_z"
  },
  "continents": 0,
  "erosion": 0,
  "depth": 0,
  "ridges": 0,
  "initial_density_without_jaggedness": 0,
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
            "argument1": 2.5,
            "argument2": {
              "type": "minecraft:mul",
              "argument1": {
                "type": "minecraft:y_clamped_gradient",
                "from_y": -8,
                "to_y": 24,
                "from_value": 0,
                "to_value": 1
              },
              "argument2": {
                "type": "minecraft:add",
                "argument1": -2.5,
                "argument2": {
                  "type": "minecraft:add",
                  "argument1": 0.9375,
                  "argument2": {
                    "type": "minecraft:mul",
                    "argument1": {
                      "type": "minecraft:y_clamped_gradient",
                      "from_y": 104,
                      "to_y": 128,
                      "from_value": 1,
                      "to_value": 0
                    },
                    "argument2": {
                      "type": "minecraft:add",
                      "argument1": -0.9375,
                      "argument2": "minecraft:nether/base_3d_noise"
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