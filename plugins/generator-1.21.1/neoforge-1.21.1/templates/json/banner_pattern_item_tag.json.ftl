{
  "values": [
    <#list data.providedBannerPatterns as pattern>
      "${generator.getResourceLocationForModElement(pattern)}"<#sep>,
    </#list>
  ]
}