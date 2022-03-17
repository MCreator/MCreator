"noise_router": {
  "barrier": 0,
  "fluid_level_floodedness": 0,
  "fluid_level_spread": 0,
  "lava": 0,
  "temperature": 0,
  "vegetation": 0,
  "continents": 0,
  "erosion": 0,
  "depth": 0,
  "ridges": 0,
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