{
  "structure": "${modid}:${field$structure}",
  <#if field$random_rotation == "TRUE">"random_rotation": true,</#if>
  <#if field$random_mirror == "TRUE">"random_mirror": true,</#if>
  "ignored_blocks": ${input$ignored_blocks}
  <#if (field$x != "0")||(field$y != "0")||(field$z != "0")>,
  "offset": [
    <#-- #4958 - clamping needed because old converters did not clamp this correctly --->
    <#if (field$x?number < -47)>-47<#elseif (field$x?number > 47)>47<#else>${field$x}</#if>,
    <#if (field$y?number < -47)>-47<#elseif (field$y?number > 47)>47<#else>${field$y}</#if>,
    <#if (field$z?number < -47)>-47<#elseif (field$z?number > 47)>47<#else>${field$z}</#if>
  ]</#if>
}