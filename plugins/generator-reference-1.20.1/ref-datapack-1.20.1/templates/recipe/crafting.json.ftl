<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
    <#if data.recipeShapeless>
        "type": "minecraft:crafting_shapeless",
    <#else>
        "type": "minecraft:crafting_shaped",
    </#if>
    <#if data.group?has_content>"group": "${data.group}",</#if>
    "category": "${data.craftingBookCategory?lower_case}",
    <#if data.recipeShapeless>
        "ingredients": [
          <#assign ingredients = "">
          <#list data.recipeSlots as element>
              <#if !element.isEmpty()>
                  <#assign ingredients += "{${mappedMCItemToItemObjectJSON(element)}},">
              </#if>
          </#list>
          ${ingredients[0..(ingredients?last_index_of(',') - 1)]}
        ],
    <#else>
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
    </#if>
    "result": {
        ${mappedMCItemToItemObjectJSON(data.recipeReturnStack)},
        "count": ${data.recipeRetstackSize}
    }
}
<#-- @formatter:on -->