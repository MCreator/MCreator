{
  "condition": "minecraft:weather_check",
  <#if field$weather == "clear">"raining": false
  <#elseif field$weather == "raining">"raining": true
  <#else>"thundering": true</#if>
}