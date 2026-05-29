{
  "replace": false,
  "entries": [
    <#list loottables?filter(table -> table.hasLootModifier()) as modifier>
      "${modid}:${modifier.getModElement().getRegistryName()}"<#sep>,
    </#list>
  ]
}