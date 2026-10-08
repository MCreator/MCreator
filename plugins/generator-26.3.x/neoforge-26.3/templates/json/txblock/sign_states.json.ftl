<#-- @formatter:off -->
{
  "variants": {
    <#list 0..15 as rotation>
    "rotation=${rotation}": { "model": "${modid}:block/${registryname}_rot_${rotation % 4}"<#if rotation gte 4>, "y": ${(rotation / 4)?int * 90}</#if> }<#sep>,
    </#list>
  }
}
<#-- @formatter:on -->
