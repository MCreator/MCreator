{
<#list sounds as sound>
"${sound.getName()}": {
  <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
  "sounds": [
    <#list sound.getFiles() as file>
    <#if sound.isInline()>
    "${modid}:${file}"
    <#else>
    {
      "name": "${modid}:${file}",
      "stream": <#if sound.getCategory() == "record" || sound.getCategory() == "music">true<#else>false</#if>
      <#if sound.getVolume() != 1>,
      "volume": ${sound.getVolume()}</#if>
      <#if sound.getPitch() != 1>,
      "pitch": ${sound.getPitch()}</#if>
      <#if sound.getWeight() != 1>,
      "weight": ${sound.getWeight()}</#if>
      <#if sound.getAttenuationDistance() != 16>,
      "attenuation_distance": ${sound.getAttenuationDistance()}</#if>
      <#if sound.isPreload()>,
      "preload": true</#if>
    }</#if><#sep>,
    </#list>
  ]
}<#sep>,
</#list>
}
