<#-- Timeline for custom effects dimensions with NORMAL sky type. It replicates the vanilla
     minecraft:day timeline (utils/overworldtimeline.json) to provide the overworld day/night
     cycle. Color tracks use the multiply modifier, so custom base colors defined in dimension
     type attributes are preserved. Fog and sunrise/sunset color tracks are only added if sun
     height is allowed to affect the fog color, otherwise the fog keeps the custom color at
     all times. -->
<#assign tracks = fp.file("utils/overworldtimeline.json")?eval_json>
<#if data.sunHeightAffectsFog>
<#assign trackNames = tracks?keys>
<#else>
<#assign trackNames = tracks?keys?filter(name -> name != "minecraft:visual/fog_color" && name != "minecraft:visual/sunrise_sunset_color")?sequence>
</#if>
{
  "clock": "minecraft:overworld",
  "period_ticks": 24000,
  "tracks": {
    <#list trackNames as trackName>
    "${trackName}": ${thelper.obj2str(tracks[trackName])}<#sep>,</#sep>
    </#list>
  }
}
