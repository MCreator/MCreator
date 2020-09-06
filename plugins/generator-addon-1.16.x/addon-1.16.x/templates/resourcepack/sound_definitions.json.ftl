{
<#list sounds as sound>
"${sound.getName()}": {
  "category": "${sound.getCategory()}",
  <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
  "sounds": [
    <#list sound.getFiles() as file>
    {
      "name": "sounds/${file}",
      "volume": 1
    }<#if file?has_next>,</#if>
    </#list>
  ]
}<#if sound?has_next>,</#if>
</#list>
}
