<#-- @formatter:off -->
<#include "mcitems.ftl">
{
    <#if !data.disableDisplay>
        "display": {
          <#if data.parent == "none" && !data.parent.toString().contains("@")>
              <#if !data.background?has_content || data.background == "Default">
                  "background": "minecraft:textures/block/stone.png",
              <#else>
                  "background": "${modid}:textures/${data.background}",
              </#if>
          </#if>
          "icon": {
            ${mappedMCItemToIngameItemName(data.achievementIcon)}
          },
          "title": "${data.achievementName}",
          "description": "${data.achievementDescription}",
          "frame": "${data.achievementType}",
          "show_toast": ${data.showPopup},
          "announce_to_chat": ${data.announceToChat},
          "hidden": ${data.hideIfNotCompleted}
        },
    </#if>
    "criteria": {
      "${registryname}": ${triggercode}
    },
    "rewards": {
        "experience": ${data.rewardXP}

        <#if data.rewardFunction?has_content && data.rewardFunction != "No function">
        ,"function": "${generator.getResourceLocationForModElement(data.rewardFunction)}"
        </#if>

        <#if data.rewardLoot?has_content>
        ,"loot": [
            <#list data.rewardLoot as value>
                "${generator.getResourceLocationForModElement(value)}"
                <#if value?has_next>,</#if>
            </#list>
        ]
        </#if>

        <#if data.rewardRecipes?has_content>
        ,"recipes": [
            <#list data.rewardRecipes as value>
                "${generator.getResourceLocationForModElement(value)}"
                <#if value?has_next>,</#if>
            </#list>
        ]
        </#if>
    }
<#if data.parent != "none" && !data.parent.toString().contains("@")>
    ,"parent": "${data.parent}"
</#if>
}
<#-- @formatter:on -->