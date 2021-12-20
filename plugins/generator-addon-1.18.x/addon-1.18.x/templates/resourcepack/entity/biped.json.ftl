<#-- @formatter:off -->
{
	"format_version": "1.8.0",
	"minecraft:client_entity": {
      "description": {
        "identifier": "${modid}:${registryname}",
        "textures": {
          "default": "textures/${data.mobModelTexture}"
        },
        <#if data.hasSpawnEgg>
        "spawn_egg": {
          "base_color": "${thelper.colorToHexString(data.spawnEggBaseColor)}",
          "overlay_color": "${thelper.colorToHexString(data.spawnEggDotColor)}"
        },
        </#if>
        "materials": {
          "default": "entity_alphatest"
        },
        "geometry": {
          "default": "geometry.zombie.v1.8"
        },
        "scripts": {
          "scale": "1",
          "pre_animation": [
            "variable.tcos0 = (Math.cos(query.modified_distance_moved * 38.17) * query.modified_move_speed / variable.gliding_speed_value) * 57.3;",
            "variable.tcos1 = -variable.tcos0;",
            "variable.attack_body_rot_y = Math.sin(Math.sqrt(variable.attack_time) * 360.0) * 11.46;",
            "variable.cos_attack_body_rot_y = Math.cos(variable.attack_body_rot_y);",
            "variable.sin_attack_body_rot_y = Math.sin(variable.attack_body_rot_y);",
            "variable.internal_swim_pos = Math.mod(query.modified_distance_moved, 26.0);",
            "variable.attack = Math.sin((1.0 - (1.0 - variable.attack_time) * (1.0 - variable.attack_time)) * 180.0);",
            "variable.attack2 = Math.sin(variable.attack_time * 180.0);"
          ]
        },
        "animations": {
          "look_at_target_default": "animation.humanoid.look_at_target.default",
          "look_at_target_gliding": "animation.humanoid.look_at_target.gliding",
          "look_at_target_swimming": "animation.humanoid.look_at_target.swimming",
          "move": "animation.humanoid.move",
          "riding.arms": "animation.humanoid.riding.arms",
          "riding.legs": "animation.humanoid.riding.legs",
          "holding": "animation.humanoid.holding",
          "brandish_spear": "animation.humanoid.brandish_spear",
          "charging": "animation.humanoid.charging",
          "attack.rotations": "animation.humanoid.attack.rotations",
          "sneaking": "animation.humanoid.sneaking",
          "bob": "animation.humanoid.bob",
          "damage_nearby_mobs": "animation.humanoid.damage_nearby_mobs",
          "bow_and_arrow": "animation.humanoid.bow_and_arrow",
          "swimming": "animation.humanoid.swimming",
          "use_item_progress": "animation.humanoid.use_item_progress"
        },
        "animation_controllers": [
          {"look_at_target": "controller.animation.humanoid.look_at_target"},
          {"move": "controller.animation.humanoid.move"},
          {"riding": "controller.animation.humanoid.riding"},
          {"holding": "controller.animation.humanoid.holding"},
          {"brandish_spear": "controller.animation.humanoid.brandish_spear"},
          {"charging": "controller.animation.humanoid.charging"},
          {"attack": "controller.animation.humanoid.attack"},
          {"sneaking": "controller.animation.humanoid.sneaking"},
          {"bob": "controller.animation.humanoid.bob"},
          {"damage_nearby_mobs": "controller.animation.humanoid.damage_nearby_mobs"},
          {"bow_and_arrow": "controller.animation.humanoid.bow_and_arrow"},
          {"swimming": "controller.animation.humanoid.swimming"},
          {"use_item_progress": "controller.animation.humanoid.use_item_progress"}
        ],
        "render_controllers": [
          "controller.render.zombie"
        ],
        "enable_attachables": true
      }
    }
}
<#-- @formatter:on -->