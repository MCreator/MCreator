<#-- @formatter:off -->
{
  "format_version": "1.13.0",
  "minecraft:entity": {
    "description": {
      "identifier": "${modid}:${registryname}",
      "is_spawnable": ${data.hasSpawnEgg},
      "is_summonable": ${data.isSummonable},
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
        "family": [ "${modid}:${registryname}", "${registryname}", "mob" <#if data.entityBehaviourType == "Mob">, "monster"</#if> ]
      },
      <#if (data.xpAmountOnDeath > 0)>
      "minecraft:experience_reward": {
        "on_death": "query.last_hit_by_player ? ${data.xpAmountOnDeath} : 0"
      },
       </#if>
      <#if data.hasDrop()>
      "minecraft:loot": {
        "table": "loot_tables/entities/${modid}_${registryname}.json"
      },
      </#if>
      <#if !data.waterEntity && !data.isImmuneToDrowning>
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
        "width": ${data.collisionBoxWidth},
        "height": ${data.collisionBoxHeight}
      },
      "minecraft:health": {
        "value": ${data.healthValue},
        "max": ${data.healthValue}
      },
      "minecraft:attack": {
        "damage": ${data.attackDamage}
      },
      "minecraft:movement": {
        "value": ${data.speedValue}
      },
      <#if !data.waterEntity>
      "minecraft:navigation.walk": {
        "can_path_over_water": true
      },
      </#if>
      <#if data.isImmuneToFallDamage>
      "minecraft:damage_sensor": {
        "triggers": {
          "cause": "fall",
          "deals_damage": false
        }
      },
      </#if>
      <#if data.canFly>
      "minecraft:can_fly": {
      },
      "minecraft:flying_speed": {
        "value": ${data.flyingSpeedValue}
      },
      <#else>
      "minecraft:jump.static": {
      },
      "minecraft:physics": {
      },
      </#if>
      <#if !data.isImmuneToFire>
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
      <#if data.isPushable || data.isPushableByPiston>
      "minecraft:pushable": {
        "is_pushable": ${data.isPushable},
        "is_pushable_by_piston": ${data.isPushableByPiston}
      },
      </#if>
      ${aicode}
      "minecraft:follow_range": {
        "value": ${data.followRangeValue}
      }
    },
    "events": {
    }
  }
}
<#-- @formatter:on -->