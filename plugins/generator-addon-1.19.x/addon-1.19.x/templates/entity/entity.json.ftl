<#-- @formatter:off -->
{
  "format_version": "1.13.0",
  "minecraft:entity": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "is_spawnable": ${data.spawnThisMob},
      "is_summonable": ${data.hasSpawnEgg},
      "is_experimental": false
    },
    "component_groups": {
      "${modid}:${registryname}": {
      }
    },
    "components": {
      "minecraft:nameable": {
      },
      "minecraft:type_family": {
        "family": [ "${modid}:${registryname}", "${registryname}", "mob" <#if data.mobBehaviourType == "Mob">, "monster"</#if> ]
      },
      <#if (data.xpAmount > 0)>
      "minecraft:experience_reward": {
        "on_death": "query.last_hit_by_player ? ${data.xpAmount} : 0"
      },
       </#if>
      <#if data.hasDrop()>
      "minecraft:loot": {
        "table": "loot_tables/entities/${modid}_${registryname}.json"
      },
      </#if>
      <#if !data.waterMob && !data.immuneToDrowning>
      "minecraft:breathable": {
        "totalSupply": 15,
        "suffocateTime": 0
      },
      </#if>
      <#if data.mobBehaviourType == "Mob">
      "minecraft:burns_in_daylight": {
      },
      </#if>
      "minecraft:collision_box": {
        "width": ${data.modelWidth},
        "height": ${data.modelHeight}
      },
      "minecraft:health": {
        "value": ${data.health},
        "max": ${data.health}
      },
      "minecraft:attack": {
        "damage": ${data.attackStrength}
      },
      "minecraft:movement": {
        "value": ${data.movementSpeed}
      },
      <#if !data.waterMob>
      "minecraft:navigation.walk": {
        "can_path_over_water": true
      },
      </#if>
      <#if data.immuneToFallDamage>
      "minecraft:damage_sensor": {
        "triggers": {
          "cause": "fall",
          "deals_damage": false
        }
      },
      </#if>
      <#if data.flyingMob>
      "minecraft:can_fly": {
      },
      "minecraft:flying_speed": {
        "value": ${data.movementSpeed}
      },
      <#else>
      "minecraft:jump.static": {
      },
      "minecraft:physics": {
      },
      </#if>
      <#if !data.immuneToFire>
      "minecraft:hurt_on_condition": {
        "damage_conditions": [
          {
            "filters": {
              "test": "in_lava",
              "subject": "self",
              "operator": "==",
              "value": true
            },
            "cause": "lava",
            "damage_per_tick": 4
          }
        ]
      },
      <#else>
      "minecraft:fire_immune": true,
      </#if>
      <#if !data.disableCollisions>
      "minecraft:pushable": {
        "is_pushable": true,
        "is_pushable_by_piston": true
      },
      </#if>
      ${aicode}
      "minecraft:follow_range": {
        "value": ${data.trackingRange}
      }
    },
    "events": {
    }
  }
}
<#-- @formatter:on -->