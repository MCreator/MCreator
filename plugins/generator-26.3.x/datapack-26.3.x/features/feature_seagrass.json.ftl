<#assign probability = (field$probability?number * 100)?round>
"features": [
  {
    "data": {
      "feature": {
        "type": "minecraft:simple_block",
        "to_place": {
          "id": "minecraft:tall_seagrass",
          "properties": {
            "half": "lower"
          }
        }
      },
      "placement": [
        {
          "type": "minecraft:block_predicate_filter",
          "predicate": {
            "type": "minecraft:matching_blocks",
            "blocks": "minecraft:water",
            "offset": [0, 1, 0]
          }
        }
      ]
    },
    "weight": ${probability}
  },
  {
    "data": {
      "feature": {
        "type": "minecraft:simple_block",
        "to_place": {
          "id": "minecraft:seagrass"
        }
      },
      "placement": []
    },
    "weight": ${100 - probability}
  }
]