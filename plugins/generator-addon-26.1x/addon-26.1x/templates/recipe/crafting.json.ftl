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
                      <#assign ingredients += "{${mappedMCItemToItemObjectJSON(element)}},">
                  </#if>
              </#list>
                ${ingredients[0..(ingredients?last_index_of(',') - 1)]}
            ],
            "result": {
              ${mappedMCItemToItemObjectJSON(data.recipeReturnStack)},
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
            <#assign patternKeys = data.getPatternKeys()>
            "pattern": [
            <#list recipeArray as rl>
                "<#list rl as re><#if !re.isEmpty()>${patternKeys.get(re)}<#else> </#if></#list>"<#sep>,
            </#list>
            ],
            "key": {
            <#list patternKeys.keySet() as item>
                "${patternKeys.get(item)}": {${mappedMCItemToItemObjectJSON(item)}}<#sep>,
            </#list>
            },
            "result": {
              ${mappedMCItemToItemObjectJSON(data.recipeReturnStack)},
              "count": ${data.recipeRetstackSize}
            }
        }
    </#if>

}
<#-- @formatter:on -->