<#function temperature2temperature temperature biomeWeight, float_sfx="">
    <#assign base = ((temperature + 1) / 3) * 2 - 1>
    <#return [(base - biomeWeight), -2]?max?string + float_sfx + ", " + [(base + biomeWeight), 2]?min?string + float_sfx>
</#function>

<#function rainingPossibility2humidity rainingPossibility biomeWeight, float_sfx="">
    <#assign base = (rainingPossibility * 2) - 1>
    <#return [(base - biomeWeight), -2]?max?string + float_sfx + ", " + [(base + biomeWeight), 2]?min?string + float_sfx>
</#function>

<#function baseHeight2continentalness baseHeight biomeWeight, float_sfx="">
<#-- continentalness (low: oceans, high: inlands) -->
    <#assign base = (baseHeight + 5) / 10.0>
    <#return [(base - biomeWeight), -2]?max?string + float_sfx + ", " + [(base + biomeWeight), 2]?min?string + float_sfx>
</#function>

<#function heightVariation2erosion heightVariation biomeWeight, float_sfx="">
<#-- erosion (high: flat terrain) -->
    <#assign base = 2 - heightVariation - 1>
    <#return [(base - biomeWeight), -2]?max?string + float_sfx + ", " + [(base + biomeWeight), 2]?min?string + float_sfx>
</#function>

<#function registryname2weirdness registryname biomeWeight, float_sfx="">
    <#assign base = (thelper.random(registryname) * 2) - 1>
    <#return [(base - biomeWeight), -2]?max?string + float_sfx + ", " + [(base + biomeWeight), 2]?min?string + float_sfx>
</#function>

<#function normalizeWeight biomeWeight, float_sfx="">
    <#return (biomeWeight / 50.0)>
</#function>

<#function normalizeWeightUnderground biomeWeight, float_sfx="">
    <#return (biomeWeight / 10.0)>
</#function>