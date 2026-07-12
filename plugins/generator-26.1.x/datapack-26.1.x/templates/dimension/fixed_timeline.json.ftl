<#-- Color helpers for hex string tracks -->
<#function hexToDec hex>
  <#assign hexChars = "0123456789abcdef">
  <#assign val = 0>
  <#list 0..(hex?length-1) as i>
    <#assign char = hex?substring(i, i+1)?lower_case>
    <#assign val = val * 16 + hexChars?index_of(char)>
  </#list>
  <#return val>
</#function>

<#function decToHex dec>
  <#assign hexChars = "0123456789abcdef">
  <#assign d = dec?floor>
  <#assign h1 = (d / 16)?floor % 16>
  <#assign h2 = d % 16>
  <#return hexChars?substring(h1, h1+1) + hexChars?substring(h2, h2+1)>
</#function>

<#function parseColor val>
  <#assign hex = val?replace("#", "")>
  <#if hex?length == 6>
    <#assign a = 255>
    <#assign r = hexToDec(hex?substring(0,2))>
    <#assign g = hexToDec(hex?substring(2,4))>
    <#assign b = hexToDec(hex?substring(4,6))>
  <#elseif hex?length == 8>
    <#assign r = hexToDec(hex?substring(0,2))>
    <#assign g = hexToDec(hex?substring(2,4))>
    <#assign b = hexToDec(hex?substring(4,6))>
    <#assign a = hexToDec(hex?substring(6,8))>
  <#else>
    <#return {"r": 255, "g": 255, "b": 255, "a": 255}>
  </#if>
  <#return {"r": r, "g": g, "b": b, "a": a}>
</#function>

<#function formatColor c>
  <#assign baseHex = "#" + decToHex(c.r) + decToHex(c.g) + decToHex(c.b)>
  <#if c.a == 255>
    <#return baseHex>
  <#else>
    <#return baseHex + decToHex(c.a)>
  </#if>
</#function>

<#function interpolateColor v1 v2 progress>
  <#assign c1 = parseColor(v1)>
  <#assign c2 = parseColor(v2)>
  <#assign r = c1.r + (c2.r - c1.r) * progress>
  <#assign g = c1.g + (c2.g - c1.g) * progress>
  <#assign b = c1.b + (c2.b - c1.b) * progress>
  <#assign a = c1.a + (c2.a - c1.a) * progress>
  <#return formatColor({"r": r, "g": g, "b": b, "a": a})>
</#function>

<#function isHexColorValue val>
  <#return val?is_string && val?starts_with("#")>
</#function>

<#function isConstantEase ease>
  <#if !ease??><#return false></#if>
  <#return ease?is_string && ease == "constant">
</#function>

<#function cubicBezierComponent t p1 p2>
  <#assign mt = 1 - t>
  <#return 3 * mt * mt * t * p1 + 3 * mt * t * t * p2 + t * t * t>
</#function>

<#function cubicBezierEase x x1 y1 x2 y2>
  <#assign lo = 0>
  <#assign hi = 1>
  <#list 1..20 as _>
    <#assign mid = (lo + hi) / 2>
    <#assign bx = cubicBezierComponent(mid, x1, x2)>
    <#if bx < x>
      <#assign lo = mid>
    <#else>
      <#assign hi = mid>
    </#if>
  </#list>
  <#assign t = (lo + hi) / 2>
  <#return cubicBezierComponent(t, y1, y2)>
</#function>

<#function findSegmentIndices keyframes time>
  <#assign n = keyframes?size>
  <#if n == 0>
    <#return {"idx1": -1, "idx2": -1}>
  <#elseif n == 1>
    <#return {"idx1": 0, "idx2": 0}>
  </#if>
  <#assign idx1 = -1>
  <#list 0..<n as i>
    <#if keyframes[i].ticks <= time>
      <#assign idx1 = i>
    </#if>
  </#list>
  <#if idx1 == -1>
    <#assign idx1 = n - 1>
  </#if>
  <#assign idx2 = (idx1 + 1) % n>
  <#return {"idx1": idx1, "idx2": idx2}>
</#function>

<#function segmentProgress keyframes idx1 idx2 time>
  <#assign t1 = keyframes[idx1].ticks>
  <#assign t2 = keyframes[idx2].ticks>
  <#if t2 <= t1>
    <#assign t2 = t2 + 24000>
  </#if>
  <#assign adjustedTime = time>
  <#if time < t1>
    <#assign adjustedTime = time + 24000>
  </#if>
  <#if t2 == t1>
    <#return 0>
  </#if>
  <#return (adjustedTime - t1) / (t2 - t1)>
</#function>

<#function getAngleTrackValue track time>
  <#assign keyframes = track.keyframes>
  <#assign anchor = keyframes[0].ticks>
  <#assign elapsed = (time - anchor + 24000) % 24000>
  <#assign rawProgress = elapsed / 24000>
  <#assign ease = track.ease!"">
  <#assign progress = rawProgress>
  <#if ease?is_hash && ease.cubic_bezier??>
    <#assign cb = ease.cubic_bezier>
    <#assign progress = cubicBezierEase(rawProgress, cb[0], cb[1], cb[2], cb[3])>
  </#if>
  <#assign v1 = keyframes[0].value>
  <#assign v2 = keyframes[1].value>
  <#return v1 + (v2 - v1) * progress>
</#function>

<#function getTrackValue trackName track time>
  <#assign keyframes = track.keyframes>
  <#if keyframes?size == 0>
    <#return "">
  </#if>

  <#-- Sun/moon/star angles use same-tick keyframes interpolated across the full day period -->
  <#if trackName?contains("angle") && keyframes?size == 2 && keyframes[0].ticks == keyframes[1].ticks>
    <#return getAngleTrackValue(track, time)>
  </#if>

  <#assign segment = findSegmentIndices(keyframes, time)>
  <#assign idx1 = segment.idx1>
  <#assign idx2 = segment.idx2>
  <#assign kf1 = keyframes[idx1]>
  <#assign kf2 = keyframes[idx2]>
  <#assign v1 = kf1.value>
  <#assign v2 = kf2.value>

  <#-- Booleans and constant-ease tracks hold the previous keyframe value -->
  <#if v1?is_boolean || isConstantEase(track.ease!"")>
    <#return v1>
  </#if>

  <#assign progress = segmentProgress(keyframes, idx1, idx2, time)>
  <#if isHexColorValue(v1) && isHexColorValue(v2)>
    <#return interpolateColor(v1, v2, progress)>
  <#elseif v1?is_number>
    <#return v1 + (v2 - v1) * progress>
  <#else>
    <#return v1>
  </#if>
</#function>

<#assign fixedTime = (data.fixedTimeValue)!0>
<#assign time = fixedTime % 24000>
<#if time < 0><#assign time = time + 24000></#if>

<#assign timelineData = '{
  "clock": "minecraft:overworld",
  "period_ticks": 24000,
  "tracks": {
    "minecraft:audio/firefly_bush_sounds": { "keyframes": [ {"ticks": 12600, "value": true}, {"ticks": 23401, "value": false} ], "modifier": "or" },
    "minecraft:gameplay/bees_stay_in_hive": { "keyframes": [ {"ticks": 12542, "value": true}, {"ticks": 23460, "value": false} ], "modifier": "or" },
    "minecraft:gameplay/cat_waking_up_gift_chance": { "ease": "constant", "keyframes": [ {"ticks": 362, "value": 0.0}, {"ticks": 23667, "value": 0.7} ], "modifier": "maximum" },
    "minecraft:gameplay/creaking_active": { "keyframes": [ {"ticks": 12600, "value": true}, {"ticks": 23401, "value": false} ], "modifier": "or" },
    "minecraft:gameplay/eyeblossom_open": { "keyframes": [ {"ticks": 12600, "value": true}, {"ticks": 23401, "value": false} ] },
    "minecraft:gameplay/monsters_burn": { "keyframes": [ {"ticks": 12542, "value": false}, {"ticks": 23460, "value": true} ], "modifier": "or" },
    "minecraft:gameplay/sky_light_level": { "keyframes": [ {"ticks": 133, "value": 1.0}, {"ticks": 11867, "value": 1.0}, {"ticks": 13670, "value": 0.26666668}, {"ticks": 22330, "value": 0.26666668} ], "modifier": "multiply" },
    "minecraft:gameplay/turtle_egg_hatch_chance": { "ease": "constant", "keyframes": [ {"ticks": 21062, "value": 1.0}, {"ticks": 21905, "value": 0.002} ], "modifier": "maximum" },
    "minecraft:visual/cloud_color": { "keyframes": [ {"ticks": 133, "value": -1}, {"ticks": 11867, "value": -1}, {"ticks": 13670, "value": -15132378}, {"ticks": 22330, "value": -15132378} ], "modifier": "multiply" },
    "minecraft:visual/fog_color": { "keyframes": [ {"ticks": 133, "value": "#ffffff"}, {"ticks": 11867, "value": "#ffffff"}, {"ticks": 13670, "value": "#0f0f16"}, {"ticks": 22330, "value": "#0f0f16"} ], "modifier": "multiply" },
    "minecraft:visual/moon_angle": { "ease": { "cubic_bezier": [0.362, 0.241, 0.638, 0.759] }, "keyframes": [ {"ticks": 6000, "value": 540.0}, {"ticks": 6000, "value": 180.0} ] },
    "minecraft:visual/sky_color": { "keyframes": [ {"ticks": 133, "value": "#ffffff"}, {"ticks": 11867, "value": "#ffffff"}, {"ticks": 13670, "value": "#000000"}, {"ticks": 22330, "value": "#000000"} ], "modifier": "multiply" },
    "minecraft:visual/sky_light_color": { "keyframes": [ {"ticks": 730, "value": "#ffffff"}, {"ticks": 11270, "value": "#ffffff"}, {"ticks": 13140, "value": "#7a7aff"}, {"ticks": 22860, "value": "#7a7aff"} ], "modifier": "multiply" },
    "minecraft:visual/sky_light_factor": { "keyframes": [ {"ticks": 730, "value": 1.0}, {"ticks": 11270, "value": 1.0}, {"ticks": 13140, "value": 0.24}, {"ticks": 22860, "value": 0.24} ], "modifier": "multiply" },
    "minecraft:visual/star_angle": { "ease": { "cubic_bezier": [0.362, 0.241, 0.638, 0.759] }, "keyframes": [ {"ticks": 6000, "value": 360.0}, {"ticks": 6000, "value": 0.0} ] },
    "minecraft:visual/star_brightness": { "keyframes": [ {"ticks": 92, "value": 0.037}, {"ticks": 627, "value": 0.0}, {"ticks": 11373, "value": 0.0}, {"ticks": 11732, "value": 0.016}, {"ticks": 11959, "value": 0.044}, {"ticks": 12399, "value": 0.143}, {"ticks": 12729, "value": 0.258}, {"ticks": 13228, "value": 0.5}, {"ticks": 22772, "value": 0.5}, {"ticks": 23032, "value": 0.364}, {"ticks": 23356, "value": 0.225}, {"ticks": 23758, "value": 0.101} ], "modifier": "maximum" },
    "minecraft:visual/sun_angle": { "ease": { "cubic_bezier": [0.362, 0.241, 0.638, 0.759] }, "keyframes": [ {"ticks": 6000, "value": 360.0}, {"ticks": 6000, "value": 0.0} ] },
    "minecraft:visual/sunrise_sunset_color": { "keyframes": [ {"ticks": 71, "value": "#5fefa333"}, {"ticks": 310, "value": "#29f5ba33"}, {"ticks": 565, "value": "#06fbd433"}, {"ticks": 730, "value": "#00ffe533"}, {"ticks": 11270, "value": "#00ffe533"}, {"ticks": 11397, "value": "#04fcd833"}, {"ticks": 11522, "value": "#0ff9cb33"}, {"ticks": 11690, "value": "#29f5ba33"}, {"ticks": 11929, "value": "#5fefa333"}, {"ticks": 12243, "value": "#b1e78733"}, {"ticks": 12358, "value": "#cce47e33"}, {"ticks": 12512, "value": "#e9e07233"}, {"ticks": 12613, "value": "#f6dd6b33"}, {"ticks": 12732, "value": "#feda6333"}, {"ticks": 12841, "value": "#fed75c33"}, {"ticks": 13035, "value": "#ecd25133"}, {"ticks": 13252, "value": "#c1cc4733"}, {"ticks": 13775, "value": "#36be3733"}, {"ticks": 13888, "value": "#1fbb3533"}, {"ticks": 14039, "value": "#09b73333"}, {"ticks": 14192, "value": "#00b33333"}, {"ticks": 21807, "value": "#00b23333"}, {"ticks": 21961, "value": "#09b73333"}, {"ticks": 22112, "value": "#1fbb3533"}, {"ticks": 22225, "value": "#36be3733"}, {"ticks": 22748, "value": "#c1cc4733"}, {"ticks": 22965, "value": "#ecd25133"}, {"ticks": 23159, "value": "#fed75c33"}, {"ticks": 23272, "value": "#feda6333"}, {"ticks": 23488, "value": "#e9e07233"}, {"ticks": 23642, "value": "#cce47e33"}, {"ticks": 23757, "value": "#b1e78733"} ] }
  }
}'?eval_json>

{
  "clock": "minecraft:overworld",
  "period_ticks": 24000,
  "tracks": {
<#assign tracks = timelineData.tracks>
<#assign trackNames = tracks?keys>
<#list trackNames as trackName>
  <#assign track = tracks[trackName]>
  <#assign val = getTrackValue(trackName, track, time)>
  <#assign isLast = !trackName_has_next>
  <#assign formattedVal = val>
  <#if val?is_boolean>
    <#assign formattedVal = val?c>
  <#elseif val?is_number>
    <#assign formattedVal = val?c>
  <#else>
    <#assign formattedVal = '"' + val + '"'>
  </#if>
    "${trackName}": {
      "keyframes": [
        {
          "ticks": 0,
          "value": ${formattedVal}
        },
        {
          "ticks": 24000,
          "value": ${formattedVal}
        }
      ]<#if track.modifier??>,
      "modifier": "${track.modifier}"</#if>
    }<#if !isLast>,</#if>
</#list>
  }
}
