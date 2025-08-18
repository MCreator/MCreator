<#include "../mcitems.ftl">
{
  "parent": "minecraft:recipes/root",
  "criteria": {
    "has_the_recipe": {
      "conditions": {
        "recipe": "${data.getNamespace()}:${data.getName()}"
      },
      "trigger": "minecraft:recipe_unlocked"
    },
    <#list data.unlockingItems as item>
    "has_ingredient_${item?index}": {
      "conditions": {
        "items": [
          {
            "items": "${mappedMCItemToRegistryName(item, true)}"
          }
        ]
      },
      "trigger": "minecraft:inventory_changed"
    }<#sep>,
    </#list>
  },
  "requirements": [
    [
      "has_the_recipe",
      <#list data.unlockingItems as item>
      "has_ingredient_${item?index}"<#sep>,
      </#list>
    ]
  ],
  "rewards": {
    "recipes": [
      "${data.getNamespace()}:${data.getName()}"
    ]
  }
}
