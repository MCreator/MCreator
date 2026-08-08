{
  <#list sounds as sound>
    "${modid}:${sound.getName()}": {
      "category": "${sound.getBECategory()?replace("master", "neutral")?replace("master", "neutral")?replace("voice", "ui")?replace("ambient", "weather")}",
      <#if sound.getBEAttenuationDistance().min != 0>
      "min_distance": ${sound.getBEAttenuationDistance().min},
      </#if>
      <#if sound.getBEAttenuationDistance().max != 0>
      "max_distance": ${sound.getBEAttenuationDistance().max},
      </#if>
      <#if sound.getSubtitle()?has_content>"subtitle": "subtitles.${sound.getName()}",</#if>
        "sounds": [
          <#list sound.getFiles() as file>
          <#if file.isInline()>
          "sounds/${file.getName()}"
          <#else>
            {
              "name": "sounds/${file.getName()}"
              <#if file.getCategory() == "record" || file.getCategory() == "music">,
              "stream": true</#if>
              <#if file.getVolume() != 1>,
              "volume": ${file.getVolume()?string("0.##")}</#if>
              <#if file.getPitch() != 1>,
              "pitch": ${file.getPitch()?string("0.##")}</#if>
              <#if file.getWeight() != 1>,
              "weight": ${file.getWeight()}</#if>
              <#if !file.isBEIs3D()>,
              "is3D": false</#if>
              <#if !file.isBEInterruptible()>,
              "interruptible": false</#if>
            }</#if><#sep>,
          </#list>
        ]
    }<#sep>,
  </#list>
}