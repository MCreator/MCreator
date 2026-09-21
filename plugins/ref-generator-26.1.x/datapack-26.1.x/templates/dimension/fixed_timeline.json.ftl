<#function parseColor val>
  <#assign hex = val?replace("#", "")>
  <#if hex?length == 6>
    <#assign a = 255>
    <#assign r = thelper.hexToDec(hex?substring(0,2))>
    <#assign g = thelper.hexToDec(hex?substring(2,4))>
    <#assign b = thelper.hexToDec(hex?substring(4,6))>
    <#return {"r": r, "g": g, "b": b, "a": a}>
  <#elseif hex?length == 8>
    <#assign r = thelper.hexToDec(hex?substring(0,2))>
    <#assign g = thelper.hexToDec(hex?substring(2,4))>
    <#assign b = thelper.hexToDec(hex?substring(4,6))>
    <#assign a = thelper.hexToDec(hex?substring(6,8))>
    <#return {"r": r, "g": g, "b": b, "a": a}>
  <#else>
    <#return {"r": 255, "g": 255, "b": 255, "a": 255}>
  </#if>
</#function>

<#function interpolateColor v1 v2 progress>
  <#assign c1 = parseColor(v1)>
  <#assign c2 = parseColor(v2)>
  <#assign r = c1.r + (c2.r - c1.r) * progress>
  <#assign g = c1.g + (c2.g - c1.g) * progress>
  <#assign b = c1.b + (c2.b - c1.b) * progress>
  <#assign a = c1.a + (c2.a - c1.a) * progress>
  <#return thelper.formatColor(r, g, b, a)>
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

<#assign tracks = fp.file("utils/overworldtimeline.json")?eval_json>

{
  "clock": "minecraft:overworld",
  "period_ticks": 24000,
  "tracks": {
<#-- Custom effects dimensions with sun height effects disabled keep fog and sunrise/sunset colors constant -->
<#if data.useCustomEffects && !data.sunHeightAffectsFog>
<#assign trackNames = tracks?keys?filter(name -> name != "minecraft:visual/fog_color" && name != "minecraft:visual/sunrise_sunset_color")?sequence>
<#else>
<#assign trackNames = tracks?keys>
</#if>
<#list trackNames as trackName>
  <#assign track = tracks[trackName]>
  <#assign val = getTrackValue(trackName, track, data.fixedTimeValue)>
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
