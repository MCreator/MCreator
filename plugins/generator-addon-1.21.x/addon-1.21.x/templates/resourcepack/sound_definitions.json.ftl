{
<#list sounds as sound>
"${modid}:${sound.getName()}": {
  "category": "${sound.getCategory()?replace("master", "neutral")?replace("master", "neutral")?replace("voice", "ui")?replace("ambient", "weather")}",
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
