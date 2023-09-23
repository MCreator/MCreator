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
        <#assign rm = [], i = 0>
        "pattern": [
        <#list recipeArray as rl>
        		"<#list rl as re><#if !re.isEmpty()><#assign rm+=["\"${i}\": {${mappedMCItemToItemObjectJSON(re)}}"]/>${i}<#else> </#if><#assign i+=1></#list>"<#sep>,
        </#list>
        ],
        "key": {
        <#list rm as recipeMapping>
            ${recipeMapping}<#sep>,
        </#list>
        },
    </#if>
    "result": {
        ${mappedMCItemToItemObjectJSON(data.recipeReturnStack)},
        "count": ${data.recipeRetstackSize}
    }
}
<#-- @formatter:on -->