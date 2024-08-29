{
  "replace": false,
  "entries": [
    <#list loottables?filter(table -> table.isLootModifier) as modifier>
      "${modid}:${modifier.getModElement().getRegistryName()}"<#sep>,
    </#list>
  ]
}