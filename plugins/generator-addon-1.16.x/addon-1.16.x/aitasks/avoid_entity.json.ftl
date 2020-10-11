"minecraft:behavior.avoid_mob_type": {
    "priority": ${customBlockIndex+1},
    "max_dist": ${field$radius},
    "walk_speed_multiplier": ${field$farspeed},
    "sprint_speed_multiplier": ${field$nearspeed},
    "entity_types": [
        {
            "filters": {
                "any_of": [
                    { "test" :  "is_family", "subject" : "other", "value" :  "${generator.map(field$entity, "entities")}"}
                ]
            },
            "max_dist": ${field$radius}
        }
    ]
},