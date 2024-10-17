{
    "feature": "${modid}:${registryname}",
    "placement": [
        <#if data.frequencyOnChunks != 1>
        {
            "type": "minecraft:count",
            "count": ${data.frequencyOnChunks}
        },
        </#if>
        <#if ((data.plantType == "normal" || data.plantType == "double") && data.generationType == "Flower") || data.plantType == "growapable">
        {
            "type": "minecraft:rarity_filter",
            "chance": 32
        },
        </#if>
        {
            "type": "minecraft:in_square"
        },
        <#if data.generateAtAnyHeight>
        {
            "type": "minecraft:height_range",
            "height": {
                "type": "minecraft:uniform",
                "min_inclusive": {
                    "above_bottom": 0
                },
                "max_inclusive": {
                    "below_top": 0
                }
            }
        },
        <#else>
        {
            "type": "minecraft:heightmap",
            <#if ((data.plantType == "normal" || data.plantType == "double") && data.generationType == "Grass") || data.plantType == "growapable">
            "heightmap": "MOTION_BLOCKING"
            <#else>
            "heightmap": "WORLD_SURFACE_WG"
            </#if>
        },
        </#if>
        {
            "type": "minecraft:biome"
        }
    ]
}