<#-- @formatter:off -->
<#include "../mcitems.ftl">
{
  "group": "<#if data.group?has_content>${data.group}<#else>${modid}</#if>",
    <#if data.recipeShapeless>
        "type": "minecraft:crafting_shapeless",
        "ingredients": [
          <#assign ingredients = "">
          <#list data.recipeSlots as element>
              <#if !element.isEmpty()>
                  <#assign ingredients += "{${mappedMCItemToIngameItemName(element)}},">
              </#if>
          </#list>
            ${ingredients[0..(ingredients?last_index_of(',') - 1)]}
        ],
    <#else>
        "type": "minecraft:crafting_shaped",
        <#assign recipeMappings = hashmap>
        <#assign recipeArray = data.getRecipeMatrix(recipeMappings)>
        "pattern": [
        <#list recipeArray as ar>
        		"<#list ar as ac>${ac}</#list>"<#if ar?has_next>,</#if>
        </#list>
        ],
        "key": {
        <#list recipeMappings.entrySet() as recipeMapping>
            "${recipeMapping.getKey()}": {${mappedMCItemToIngameItemName(recipeMapping.getValue())}}<#if recipeMapping?has_next>,</#if>
        </#list>
        },
    </#if>
    "result": {
      ${mappedMCItemToIngameItemName(data.recipeReturnStack)},
      "count": ${data.recipeRetstackSize}
    }
}
<#-- @formatter:on -->