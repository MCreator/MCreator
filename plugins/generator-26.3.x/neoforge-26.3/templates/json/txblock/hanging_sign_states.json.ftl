<#-- @formatter:off -->
{
  "variants": {
    <#list ["", "attached_"] as attached>
    <#list 0..15 as rotation>
    "attached=${(attached == "attached_")?c},rotation=${rotation}": { "model": "${modid}:block/${registryname}_${attached}rot_${rotation % 4}"<#if rotation gte 4>, "y": ${(rotation / 4)?int * 90}</#if> }<#sep>,
    </#list><#sep>,
    </#list>
  }
}
<#-- @formatter:on -->
