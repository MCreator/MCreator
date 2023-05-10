"noise_router": {
  "barrier": {
    "type": "minecraft:noise",
    "noise": "minecraft:aquifer_barrier",
    "xz_scale": 1,
    "y_scale": 0.5
  },
  "fluid_level_floodedness": {
    "type": "minecraft:noise",
    "noise": "minecraft:aquifer_fluid_level_floodedness",
    "xz_scale": 1,
    "y_scale": 0.67
  },
  "fluid_level_spread": {
    "type": "minecraft:noise",
    "noise": "minecraft:aquifer_fluid_level_spread",
    "xz_scale": 1,
    "y_scale": 0.7142857142857143
  },
  "lava": {
    "type": "minecraft:noise",
    "noise": "minecraft:aquifer_lava",
    "xz_scale": 1,
    "y_scale": 1
  },
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
  "continents": "minecraft:overworld/continents",
  "erosion": "minecraft:overworld/erosion",
  "depth": "minecraft:overworld/depth",
  "ridges": "minecraft:overworld/ridges",
  "initial_density_without_jaggedness": {
    "type": "minecraft:add",
    "argument1": 0.1171875,
    "argument2": {
      "type": "minecraft:mul",
      "argument1": {
        "type": "minecraft:y_clamped_gradient",
        "from_y": -64,
        "to_y": -40,
        "from_value": 0,
        "to_value": 1
      },
      "argument2": {
        "type": "minecraft:add",
        "argument1": -0.1171875,
        "argument2": {
          "type": "minecraft:add",
          "argument1": -0.078125,
          "argument2": {
            "type": "minecraft:mul",
            "argument1": {
              "type": "minecraft:y_clamped_gradient",
              "from_y": 240,
              "to_y": 256,
              "from_value": 1,
              "to_value": 0
            },
            "argument2": {
              "type": "minecraft:add",
              "argument1": 0.078125,
              "argument2": {
                "type": "minecraft:clamp",
                "input": {
                  "type": "minecraft:add",
                  "argument1": -0.703125,
                  "argument2": {
                    "type": "minecraft:mul",
                    "argument1": 4,
                    "argument2": {
                      "type": "minecraft:quarter_negative",
                      "argument": {
                        "type": "minecraft:mul",
                        "argument1": "minecraft:overworld/depth",
                        "argument2": {
                          "type": "minecraft:cache_2d",
                          "argument": "minecraft:overworld/factor"
                        }
                      }
                    }
                  }
                },
                "min": -64,
                "max": 64
              }
            }
          }
        }
      }
    }
  },
  "final_density": {
    "type": "minecraft:min",
    "argument1": {
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
              "argument1": 0.1171875,
              "argument2": {
                "type": "minecraft:mul",
                "argument1": {
                  "type": "minecraft:y_clamped_gradient",
                  "from_y": -64,
                  "to_y": -40,
                  "from_value": 0,
                  "to_value": 1
                },
                "argument2": {
                  "type": "minecraft:add",
                  "argument1": -0.1171875,
                  "argument2": {
                    "type": "minecraft:add",
                    "argument1": -0.078125,
                    "argument2": {
                      "type": "minecraft:mul",
                      "argument1": {
                        "type": "minecraft:y_clamped_gradient",
                        "from_y": 240,
                        "to_y": 256,
                        "from_value": 1,
                        "to_value": 0
                      },
                      "argument2": {
                        "type": "minecraft:add",
                        "argument1": 0.078125,
                        "argument2": {
                          "type": "minecraft:range_choice",
                          "input": "minecraft:overworld/sloped_cheese",
                          "min_inclusive": -1000000,
                          "max_exclusive": 1.5625,
                          "when_in_range": {
                            "type": "minecraft:min",
                            "argument1": "minecraft:overworld/sloped_cheese",
                            "argument2": {
                              "type": "minecraft:mul",
                              "argument1": 5,
                              "argument2": "minecraft:overworld/caves/entrances"
                            }
                          },
                          "when_out_of_range": {
                            "type": "minecraft:max",
                            "argument1": {
                              "type": "minecraft:min",
                              "argument1": {
                                "type": "minecraft:min",
                                "argument1": {
                                  "type": "minecraft:add",
                                  "argument1": {
                                    "type": "minecraft:mul",
                                    "argument1": 4,
                                    "argument2": {
                                      "type": "minecraft:square",
                                      "argument": {
                                        "type": "minecraft:noise",
                                        "noise": "minecraft:cave_layer",
                                        "xz_scale": 1,
                                        "y_scale": 8
                                      }
                                    }
                                  },
                                  "argument2": {
                                    "type": "minecraft:add",
                                    "argument1": {
                                      "type": "minecraft:clamp",
                                      "input": {
                                        "type": "minecraft:add",
                                        "argument1": 0.27,
                                        "argument2": {
                                          "type": "minecraft:noise",
                                          "noise": "minecraft:cave_cheese",
                                          "xz_scale": 1,
                                          "y_scale": 0.6666666666666666
                                        }
                                      },
                                      "min": -1,
                                      "max": 1
                                    },
                                    "argument2": {
                                      "type": "minecraft:clamp",
                                      "input": {
                                        "type": "minecraft:add",
                                        "argument1": 1.5,
                                        "argument2": {
                                          "type": "minecraft:mul",
                                          "argument1": -0.64,
                                          "argument2": "minecraft:overworld/sloped_cheese"
                                        }
                                      },
                                      "min": 0,
                                      "max": 0.5
                                    }
                                  }
                                },
                                "argument2": "minecraft:overworld/caves/entrances"
                              },
                              "argument2": {
                                "type": "minecraft:add",
                                "argument1": "minecraft:overworld/caves/spaghetti_2d",
                                "argument2": "minecraft:overworld/caves/spaghetti_roughness_function"
                              }
                            },
                            "argument2": {
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
                  }
                }
              }
            }
          }
        }
      }
    },
    "argument2": "minecraft:overworld/caves/noodle"
  },
  "vein_toggle": {
    "type": "minecraft:interpolated",
    "argument": {
      "type": "minecraft:range_choice",
      "input": "minecraft:y",
      "min_inclusive": -60,
      "max_exclusive": 51,
      "when_in_range": {
        "type": "minecraft:noise",
        "noise": "minecraft:ore_veininess",
        "xz_scale": 1.5,
        "y_scale": 1.5
      },
      "when_out_of_range": 0
    }
  },
  "vein_ridged": {
    "type": "minecraft:add",
    "argument1": -0.07999999821186066,
    "argument2": {
      "type": "minecraft:max",
      "argument1": {
        "type": "minecraft:abs",
        "argument": {
          "type": "minecraft:interpolated",
          "argument": {
            "type": "minecraft:range_choice",
            "input": "minecraft:y",
            "min_inclusive": -60,
            "max_exclusive": 51,
            "when_in_range": {
              "type": "minecraft:noise",
              "noise": "minecraft:ore_vein_a",
              "xz_scale": 4,
              "y_scale": 4
            },
            "when_out_of_range": 0
          }
        }
      },
      "argument2": {
        "type": "minecraft:abs",
        "argument": {
          "type": "minecraft:interpolated",
          "argument": {
            "type": "minecraft:range_choice",
            "input": "minecraft:y",
            "min_inclusive": -60,
            "max_exclusive": 51,
            "when_in_range": {
              "type": "minecraft:noise",
              "noise": "minecraft:ore_vein_b",
              "xz_scale": 4,
              "y_scale": 4
            },
            "when_out_of_range": 0
          }
        }
      }
    }
  },
  "vein_gap": {
    "type": "minecraft:noise",
    "noise": "minecraft:ore_gap",
    "xz_scale": 1,
    "y_scale": 1
  }
}