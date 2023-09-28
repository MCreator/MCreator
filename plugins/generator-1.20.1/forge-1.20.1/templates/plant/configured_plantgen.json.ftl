<#include "../mcitems.ftl">
{
    "type": "minecraft:random_patch",
    "config": {
        <#if data.patchSize != 128>
        "tries": ${data.patchSize},
        </#if>
        <#if data.plantType == "growapable">
        "feature": {
            "feature": {
                "type": "minecraft:block_column",
                "config": {
                    "allowed_placement": {
                        "type": "minecraft:matching_blocks",
                        "blocks": "minecraft:air"
                    },
                    "direction": "up",
                    "layers": [
                        {
                            "height": {
                                "type": "minecraft:biased_to_bottom",
                                "value": {
                                    "min_inclusive": 2,
                                    "max_inclusive": 4
                                }
                            },
                            "provider": {
                                "type": "minecraft:simple_state_provider",
                                "state": {
                                    "Name": "${modid}:${registryname}"
                                }
                            }
                        }
                    ],
                    "prioritize_tip": false
                }
            },
            "placement": [
                {
                    "type": "minecraft:block_predicate_filter",
                    "predicate": {
                        "type": "minecraft:all_of",
                        "predicates": [
                            {
                                "type": "minecraft:matching_blocks",
                                "blocks": "minecraft:air"
                            },
                            {
                                "type": "minecraft:would_survive",
                                "state": {
                                    "Name": "${modid}:${registryname}"
                                }
                            }
                        ]
                    }
                }
            ]
        }
        <#else>
        "feature": {
            "feature": {
                "type": "minecraft:simple_block",
                "config": {
                    "to_place": {
                        "type": "minecraft:simple_state_provider",
                        "state": {
                            "Name": "${modid}:${registryname}"
                        }
                    }
                }
            },
            "placement": [
                {
                    "type": "minecraft:block_predicate_filter",
                    "predicate": {
                        "type": "minecraft:matching_blocks",
                        "blocks": "minecraft:air"
                    }
                }
            ]
        }
        </#if>
    }
}