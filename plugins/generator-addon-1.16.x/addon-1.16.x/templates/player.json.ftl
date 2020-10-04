<#-- @formatter:off -->
<#assign itemDamageValues = []>

<#list w.getElementsOfType("ITEM") as mod>
  <#assign ge = mod.getGeneratableElement()>
  <#if ge.enableMeleeDamage>
    <#assign itemDamageValues += [ ["${modid}:${mod.getRegistryName()}", ge.damageVsEntity] ]>
  </#if>
</#list>

<#list w.getElementsOfType("TOOL") as mod>
  <#assign ge = mod.getGeneratableElement()>
  <#assign itemDamageValues += [ ["${modid}:${mod.getRegistryName()}", ge.damageVsEntity] ]>
</#list>

{
  "format_version": "1.13.0",
  "minecraft:entity": {
    "description": {
      "identifier": "minecraft:player",
      "is_spawnable": false,
      "is_summonable": false,
      "is_experimental": false
    },

    "component_groups": {
      "minecraft:normal_player": {
        "minecraft:attack": {
          "damage": 1
        }
      },
      <#list itemDamageValues as item>
      "${item[0]}": {
        "minecraft:attack": {
          "damage": ${item[1]?round}
        }
      },
      </#list>
      "minecraft:add_bad_omen": {
        "minecraft:spell_effects": {
          "add_effects": [
            {
              "effect": "bad_omen",
              "duration": 6000,
              "display_on_screen_animation": true
            }
          ]
        },
        "minecraft:timer": {
          "time": [ 0.0, 0.0 ],
          "looping": false,
          "time_down_event": {
            "event": "minecraft:clear_add_bad_omen",
            "target": "self"
          }
        }
      },
      "minecraft:clear_bad_omen_spell_effect": {
        "minecraft:spell_effects": {
        }
      },
      "minecraft:raid_trigger": {
        "minecraft:raid_trigger": {
          "triggered_event": {
            "event": "minecraft:remove_raid_trigger",
            "target": "self"
          }
        },
        "minecraft:spell_effects": {
          "remove_effects": "bad_omen"
        }
      }
    },

    "components": {
      "minecraft:experience_reward": {
        "on_death": "Math.Min(query.player_level * 7, 100)"
      },
      "minecraft:type_family": {
        "family": [ "player" ]
      },
      "minecraft:is_hidden_when_invisible": {
      },
      "minecraft:loot": {
        "table": "loot_tables/empty.json"
      },
      "minecraft:collision_box": {
        "width": 0.6,
        "height": 1.8
      },
      "minecraft:can_climb": {
      },
      "minecraft:movement": {
        "value": 0.1
      },
      "minecraft:hurt_on_condition": {
        "damage_conditions": [
          {
            "filters": { "test": "in_lava", "subject": "self", "operator": "==", "value": true },
            "cause": "lava",
            "damage_per_tick": 4
          }
        ]
      },
      "minecraft:attack": {
        "damage": 1
      },
      "minecraft:player.saturation": {
        "value": 20
      },
      "minecraft:player.exhaustion": {
        "value": 0,
        "max": 4
      },
      "minecraft:player.level": {
        "value": 0,
        "max": 24791
      },
      "minecraft:player.experience": {
        "value": 0,
        "max": 1
      },
      "minecraft:breathable": {
        "total_supply": 15,
        "suffocate_time": -1,
        "inhale_time": 3.75,
        "generates_bubbles": false
      },
      "minecraft:nameable": {
        "always_show": true,
        "allow_name_tag_renaming": false
      },
      "minecraft:physics": {
      },
      "minecraft:pushable": {
        "is_pushable": false,
        "is_pushable_by_piston": true
      },
      "minecraft:insomnia": {
        "days_until_insomnia": 3
      },
      "minecraft:rideable": {
        "seat_count": 2,
        "family_types": [
          "parrot_tame"
        ],
        "pull_in_entities": true,
        "seats": [
          {
            "position": [ 0.4, -0.2, -0.1 ],
            "min_rider_count": 0,
            "max_rider_count": 0,
            "lock_rider_rotation": 0
          },
          {
            "position": [ -0.4, -0.2, -0.1 ],
            "min_rider_count": 1,
            "max_rider_count": 2,
            "lock_rider_rotation": 0
          }
        ]
      },
      "minecraft:scaffolding_climber": {},
      "minecraft:environment_sensor": {
        "triggers": [
          {
            "filters": {
              "all_of": [
                {
                  "test": "has_mob_effect",
                  "subject": "self",
                  "value": "bad_omen"
                },
                {
                  "test": "is_in_village",
                  "subject": "self",
                  "value": true
                }
              ]
            },
            "event": "minecraft:trigger_raid"
          },
          {
            "filters": {
              "all_of": [
                {
                  "test": "is_family",
                  "subject": "self",
                  "value": "player"
                }
              ]
            },
            "event": "minecraft:event_normal_player"
          }
          <#list itemDamageValues as item>
          ,{
            "filters": {
              "all_of": [
                {
                  "test": "has_equipment",
                  "subject": "self",
                  "domain": "hand",
                  "value": "${item[0]}"
                }
              ]
            },
            "event": "${item[0]}"
          }
          </#list>
        ]
      }
    },

    "events": {
      "minecraft:event_normal_player": {
        "add": {
          "component_groups": [
            "minecraft:normal_player"
          ]
        },
        "remove": {
          "component_groups": [
            <#list itemDamageValues as item>
            "${item[0]}"<#if item?has_next>,</#if>
            </#list>
          ]
        }
      },
      <#list itemDamageValues as item>
      "${item[0]}": {
        "add": {
          "component_groups": [
            "${item[0]}"
          ]
        }
      },
      </#list>
      "minecraft:gain_bad_omen": {
        "add": {
          "component_groups": [
            "minecraft:add_bad_omen"
          ]
        }
      },
      "minecraft:clear_add_bad_omen": {
        "remove": {
          "component_groups": [
            "minecraft:add_bad_omen"
          ]
        },
        "add": {
          "component_groups": [
            "minecraft:clear_bad_omen_spell_effect"
          ]
        }
      },
      "minecraft:trigger_raid": {
        "add": {
          "component_groups": [ "minecraft:raid_trigger" ]
        }
      },
      "minecraft:remove_raid_trigger": {
        "remove": {
          "component_groups": [ "minecraft:raid_trigger" ]
        }
      }
    }
  }
}
<#-- @formatter:on -->