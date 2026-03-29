{
  "values": [
    <#list data.providedBannerPatterns as pattern>
      "${generator.getIdentifierForModElement(pattern)}"<#sep>,
    </#list>
  ]
}