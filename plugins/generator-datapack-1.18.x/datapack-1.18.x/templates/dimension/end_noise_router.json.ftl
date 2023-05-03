"noise_router": {
  "barrier": 0,
  "fluid_level_floodedness": 0,
  "fluid_level_spread": 0,
  "lava": 0,
  "temperature": {
    "type": "minecraft:shifted_noise",
    "noise": "minecraft:temperature",
    "xz_scale": 1.5,
    "y_scale": 0,
    "shift_x": "minecraft:shift_x",
    "shift_y": 0,
    "shift_z": "minecraft:shift_z"
  },
  "vegetation": {
    "type": "minecraft:shifted_noise",
    "noise": "minecraft:vegetation",
    "xz_scale": 1.5,
    "y_scale": 0,
    "shift_x": "minecraft:shift_x",
    "shift_y": 0,
    "shift_z": "minecraft:shift_z"
  },
  "ridges": "minecraft:overworld/ridges",
  "continents": "minecraft:overworld/continents",
  "erosion": "minecraft:overworld/erosion",
  "depth": "minecraft:overworld/depth",
  "initial_density_without_jaggedness": {
    "type": "minecraft:cache_2d",
    "argument": {
      "type": "minecraft:end_islands"
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
            "type": "minecraft:slide",
            "argument": "minecraft:end/sloped_cheese"
          }
        }
      }
    }
  },
  "vein_toggle": 0,
  "vein_ridged": 0,
  "vein_gap": 0
}