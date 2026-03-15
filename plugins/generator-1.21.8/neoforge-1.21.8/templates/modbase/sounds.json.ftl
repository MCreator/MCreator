{
<#list sounds as sound>
"${sound.getName()}": {
  <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
  "sounds": [
    <#list sound.getFiles() as file>
    <#if file.isInline()>
    "${modid}:${file.getName()}"
    <#else>
    {
      "name": "${modid}:${file.getName()}"
      <#if file.getCategory() == "record" || file.getCategory() == "music">,
      "stream": true</#if>
      <#if file.getVolume() != 1>,
      "volume": ${file.getVolume()}</#if>
      <#if file.getPitch() != 1>,
      "pitch": ${file.getPitch()}</#if>
      <#if file.getWeight() != 1>,
      "weight": ${file.getWeight()}</#if>
      <#if file.getAttenuationDistance() != 16>,
      "attenuation_distance": ${file.getAttenuationDistance()}</#if>
      <#if file.isPreload()>,
      "preload": true</#if>
    }</#if><#sep>,
    </#list>
  ]
}<#sep>,
</#list>
}
