{
  <#list sounds as sound>
    "${modid}:${sound.getName()}": {
      "category": "${sound.getCategory()?replace("master", "neutral")?replace("master", "neutral")?replace("voice", "ui")?replace("ambient", "weather")}",
      <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
        "sounds": [
          <#list sound.getFiles() as file>
          <#if sound.isInline()>
          "sounds/${file}"
          <#else>
            {
              "name": "sounds/${file}"
              <#if sound.getCategory() == "record" || sound.getCategory() == "music">,
              "stream": true</#if>
              <#if sound.getVolume() != 1>,
              "volume": ${sound.getVolume()}</#if>
              <#if sound.getPitch() != 1>,
              "pitch": ${sound.getPitch()}</#if>
              <#if sound.getWeight() != 1>,
              "weight": ${sound.getWeight()}</#if>
            }</#if><#sep>,
          </#list>
        ]
    }<#sep>,
  </#list>
}