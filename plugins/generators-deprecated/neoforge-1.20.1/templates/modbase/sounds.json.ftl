{
<#list sounds as sound>
"${sound.getName()}": {
  <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
  "sounds": [
    <#list sound.getFiles() as file>
    {
      "name": "${modid}:${file}",
      "stream": <#if sound.getCategory() == "record" || sound.getCategory() == "music">true<#else>false</#if>
    }<#sep>,
    </#list>
  ]
}<#sep>,
</#list>
}
