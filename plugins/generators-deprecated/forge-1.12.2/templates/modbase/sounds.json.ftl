{
<#list sounds as sound>
"${sound.getName()}": {
  "category": "${sound.getCategory()}",
  "sounds": [
    {
      "name": "${modid}:${sound.getFile()}",
    "stream": <#if sound.getCategory() == "record" || sound.getCategory() == "music">true<#else>false</#if>
    }
  ]
}<#if sound?has_next>,</#if>
</#list>
}