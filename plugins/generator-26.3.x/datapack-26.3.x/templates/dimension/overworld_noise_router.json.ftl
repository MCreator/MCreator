"noise_router": {
  "temperature": "minecraft:overworld/temperature",
  "vegetation": "minecraft:overworld/vegetation",
  "continents": "minecraft:overworld/continents",
  "erosion": "minecraft:overworld/erosion",
  "depth": "minecraft:overworld/depth",
  "ridges": "minecraft:overworld/ridges",
  "chunk_surface_level": "minecraft:overworld/chunk_surface_level",
  "final_density": {
    "type": "minecraft:add",
    "left": {
      "type": "minecraft:min",
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
                  "from_coordinate": -64,
                  "from_value": 0,
                  "to_coordinate": -40,
                  "to_value": 1
                },
                "first": 0.1171875,
                "second": {
                  "type": "minecraft:lerp",
                  "alpha": {
                    "type": "minecraft:gradient",
                    "axis": "y",
                    "from_coordinate": 240,
                    "from_value": 1,
                    "to_coordinate": 256,
                    "to_value": 0
                  },
                  "first": -0.078125,
                  "second": {
                    "type": "minecraft:range_choice",
                    "input": "minecraft:overworld/sloped_cheese",
                    "min_inclusive": -1000000,
                    "max_exclusive": 1.5625,
                    "when_in_range": {
                      "type": "minecraft:min",
                      "left": "minecraft:overworld/sloped_cheese",
                      "right": {
                        "type": "minecraft:mul",
                        "left": "minecraft:overworld/caves/entrances",
                        "right": 5
                      }
                    },
                    "when_out_of_range": {
                      "type": "minecraft:max",
                      "left": {
                        "type": "minecraft:min",
                        "left": {
                          "type": "minecraft:min",
                          "left": {
                            "type": "minecraft:add",
                            "left": {
                              "type": "minecraft:mul",
                              "left": {
                                "type": "minecraft:square",
                                "input": {
                                  "type": "minecraft:noise",
                                  "noise": "minecraft:cave_layer",
                                  "xz_scale": 1,
                                  "y_scale": 8
                                }
                              },
                              "right": 4
                            },
                            "right": {
                              "type": "minecraft:add",
                              "left": {
                                "type": "minecraft:clamp",
                                "input": {
                                  "type": "minecraft:add",
                                  "left": {
                                    "type": "minecraft:noise",
                                    "noise": "minecraft:cave_cheese",
                                    "xz_scale": 1,
                                    "y_scale": 0.6666666666666666
                                  },
                                  "right": 0.27
                                },
                                "min": -1,
                                "max": 1
                              },
                              "right": {
                                "type": "minecraft:clamp",
                                "input": {
                                  "type": "minecraft:add",
                                  "left": {
                                    "type": "minecraft:mul",
                                    "left": "minecraft:overworld/sloped_cheese",
                                    "right": -0.64
                                  },
                                  "right": 1.5
                                },
                                "min": 0,
                                "max": 0.5
                              }
                            }
                          },
                          "right": "minecraft:overworld/caves/entrances"
                        },
                        "right": {
                          "type": "minecraft:add",
                          "left": "minecraft:overworld/caves/spaghetti_2d",
                          "right": "minecraft:overworld/caves/spaghetti_roughness_function"
                        }
                      },
                      "right": {
                        "type": "minecraft:range_choice",
                        "input": "minecraft:overworld/caves/pillars",
                        "min_inclusive": -1000000,
                        "max_exclusive": 0.03,
                        "when_in_range": -1000000,
                        "when_out_of_range": "minecraft:overworld/caves/pillars"
                      }
                    }
                  }
                }
              }
            },
            "right": 0.64
          }
        }
      },
      "right": "minecraft:overworld/caves/noodle"
    },
    "right": {
      "type": "minecraft:beardifier"
    }
  }
},
"debug_functions": [
  {
    "function": "minecraft:overworld/temperature",
    "label": "T"
  },
  {
    "function": "minecraft:overworld/vegetation",
    "label": "V"
  },
  {
    "function": "minecraft:overworld/continents",
    "label": "C"
  },
  {
    "function": "minecraft:overworld/erosion",
    "label": "E"
  },
  {
    "function": "minecraft:overworld/depth",
    "label": "D"
  },
  {
    "function": "minecraft:overworld/ridges",
    "label": "W"
  },
  {
    "function": {
      "type": "minecraft:mul",
      "left": {
        "type": "minecraft:add",
        "left": {
          "type": "minecraft:abs",
          "input": {
            "type": "minecraft:add",
            "left": {
              "type": "minecraft:abs",
              "input": "minecraft:overworld/ridges"
            },
            "right": -0.6666667
          }
        },
        "right": -0.33333334
      },
      "right": -3.0
    },
    "label": "PV"
  }
]
