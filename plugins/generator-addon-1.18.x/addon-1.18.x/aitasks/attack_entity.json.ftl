"minecraft:behavior.nearest_attackable_target": {
    "priority": ${customBlockIndex+1},
    "must_see":  ${field$insight?lower_case},
    "must_reach":  ${field$nearby?lower_case},
    "entity_types": [
        {
            "filters": {
                "any_of": [
                    { "test" :  "is_family", "subject" : "other", "value" :  "${generator.map(field$entity, "entities")}"}
                ]
            },
            "max_dist": 16
        }
    ]
},