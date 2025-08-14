{
    "feature": "${modid}:${registryname}",
    "placement": [
        {
            "type": "minecraft:count",
            "count": ${data.frequencyPerChunks}
        },
        {
            "type": "minecraft:in_square"
        },
        {
            "type": "minecraft:height_range",
            "height": {
                "type": "minecraft:${data.generationShape?lower_case?replace("triangle", "trapezoid")}",
                "min_inclusive": {
                    "absolute": ${data.minGenerateHeight}
                },
                "max_inclusive": {
                    "absolute": ${data.maxGenerateHeight}
                }
            }
        },
        {
          "type": "minecraft:biome"
        }
    ]
}