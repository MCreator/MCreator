<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "format_version": "1.12",
    <#if data.recipeShapeless>
        "minecraft:recipe_shapeless": {
            "description": {
              "identifier": "${data.getNamespace()}:${data.getName()}"
            },
            "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
            "tags": [ "crafting_table" ],
            "ingredients": [
              <#assign ingredients = "">
              <#list data.recipeSlots as element>
                  <#if !element.isEmpty()>
                      <#assign ingredients += "{${mappedMCItemToIngameItemName(element)}},">
                  </#if>
              </#list>
                ${ingredients[0..(ingredients?last_index_of(',') - 1)]}
            ],
            "result": {
              ${mappedMCItemToIngameItemName(data.recipeReturnStack)},
              "count": ${data.recipeRetstackSize}
            }
        }
    <#else>
        "minecraft:recipe_shaped": {
            "description": {
                "identifier": "${data.getNamespace()}:${data.getName()}"
            },
            "groups": [ "<#if data.group?has_content>${data.group}<#else>${modid}</#if>" ],
            "tags": [ "crafting_table" ],
            <#assign recipeArray = data.getOptimisedRecipe()>
            <#assign rm = [], i = 0>
            "pattern": [
            <#list recipeArray as rl>
            		"<#list rl as re><#if !re.isEmpty()><#assign rm+=["\"${i}\": {${mappedMCItemToIngameItemName(re)}}"]/>${i}<#else> </#if><#assign i+=1></#list>"<#if rl?has_next>,</#if>
            </#list>
            ],
            "key": {
            <#list rm as recipeMapping>
                ${recipeMapping}<#if recipeMapping?has_next>,</#if>
            </#list>
            },
            "result": {
              ${mappedMCItemToIngameItemName(data.recipeReturnStack)},
              "count": ${data.recipeRetstackSize}
            }
        }
    </#if>

}
<#-- @formatter:on -->