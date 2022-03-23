{
<#list sounds as sound>
"${sound.getName()}": {
  "category": "${generator.map(sound.getCategory(), "soundcategories", 1)}",
  <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
  "sounds": [
    <#list sound.getFiles() as file>
    {
      "name": "${modid}:${file}",
      "stream": <#if sound.getCategory() == "record" || sound.getCategory() == "music">true<#else>false</#if>
    }<#if file?has_next>,</#if>
    </#list>
  ]
}<#if sound?has_next>,</#if>
</#list>
}
