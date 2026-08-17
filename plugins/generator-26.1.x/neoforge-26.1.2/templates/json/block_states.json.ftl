<#assign definedStates = (data.getDefinedStates?? && data.getDefinedStates()?has_content)?then(data.getDefinedStates(), [])>
<#assign multipart = data.multipartModel && definedStates?has_content>
<#assign stateCombinations = (!multipart && definedStates?has_content)?then(data.getStateCombinations(), [])>

<#if data.rotationMode?? && (data.rotationMode == 1 || data.rotationMode == 3)>
<#if data.enablePitch>
{
  <@statesOpen/>
    <@variant 'face=floor,facing=north'/>,
    <@variant 'face=floor,facing=east' '"y": 90'/>,
    <@variant 'face=floor,facing=south' '"y": 180'/>,
    <@variant 'face=floor,facing=west' '"y": 270'/>,
    <@variant 'face=wall,facing=north' '"x": 90'/>,
    <@variant 'face=wall,facing=east' '"x": 90, "y": 90'/>,
    <@variant 'face=wall,facing=south' '"x": 90, "y": 180'/>,
    <@variant 'face=wall,facing=west' '"x": 90, "y": 270'/>,
    <@variant 'face=ceiling,facing=north' '"x": 180, "y": 180'/>,
    <@variant 'face=ceiling,facing=east' '"x": 180, "y": 270'/>,
    <@variant 'face=ceiling,facing=south' '"x": 180'/>,
    <@variant 'face=ceiling,facing=west' '"x": 180, "y": 90'/>
  <@statesClose/>
}
<#else>
{
  <@statesOpen/>
    <@variant 'facing=north'/>,
    <@variant 'facing=east' '"y": 90'/>,
    <@variant 'facing=south' '"y": 180'/>,
    <@variant 'facing=west' '"y": 270'/>
  <@statesClose/>
}
</#if>
<#elseif data.rotationMode?? && (data.rotationMode == 2 || data.rotationMode == 4)>
{
  <@statesOpen/>
    <@variant 'facing=north'/>,
    <@variant 'facing=east' '"y": 90'/>,
    <@variant 'facing=south' '"y": 180'/>,
    <@variant 'facing=west' '"y": 270'/>,
    <@variant 'facing=up' '"x": 270'/>,
    <@variant 'facing=down' '"x": 90'/>
  <@statesClose/>
}
<#elseif data.rotationMode?? && data.rotationMode == 5>
{
  <@statesOpen/>
    <@variant 'axis=x' '"x": 90, "y": 90'/>,
    <@variant 'axis=y'/>,
    <@variant 'axis=z' '"x": 90'/>
  <@statesClose/>
}
<#else>
{
  <@statesOpen/>
    <@variant/>
  <@statesClose/>
}
</#if>

<#macro statesOpen>${multipart?then("'multipart': [", "'variants': {")}</#macro>
<#macro statesClose>${multipart?then("]", "}")}</#macro>

<#macro variant conditionExtra="" additionalData="">
  <#if multipart>
    <#list definedStates as model>
     <#assign conditions = []>
     <#list conditionExtra?split(",") as pair>
      <#if pair?has_content>
       <#assign conditions += ['"' + pair?keep_before("=") + '": "' + pair?keep_after("=") + '"']>
      </#if>
     </#list>
     <#list model.stateMap.keySet() as property>
      <#assign conditions += ['"' + generator.map(property.getName(), "blockstateproperties", 1) + '": "' + model.stateMap.get(property) + '"']>
     </#list>
     {
      <#if conditions?has_content>"when": { ${conditions?join(", ")} },</#if>
      "apply": {
       "model": "${modid}:block/${registryname}_${model?index}"
       <#if additionalData?has_content>,${additionalData}</#if>
      }
     }<#sep>,
    </#list>
  <#elseif stateCombinations?has_content>
    <#list stateCombinations as model>
     <#assign variantPredicate = "">
     <#list model.stateMap.keySet() as property>
      <#assign variantPredicate += (generator.map(property.getName(), "blockstateproperties", 1) + "=" + model.stateMap.get(property) + property?has_next?then(",", ""))>
     </#list>
     "<#if conditionExtra?has_content>${conditionExtra},</#if>${variantPredicate}": {
      "model": "${modid}:block/${registryname}<#if model.renderType != -1>_${model?index}</#if>"
      <#if additionalData?has_content>,${additionalData}</#if>
     }<#sep>,
    </#list>
  <#else>
   "${conditionExtra}": {
    "model": "${modid}:block/${registryname}"
    <#if additionalData?has_content>,${additionalData}</#if>
   }
  </#if>
</#macro>