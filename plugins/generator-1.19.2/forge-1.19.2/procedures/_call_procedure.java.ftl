<#include "procedures.java.ftl">
<#assign customVals = {}>
<#if paramsCount gt 0>
	<#list 0..(paramsCount - 1) as index>
		<#assign customVals += {names[index]: args[index]}>
	</#list>
</#if>
<@procedureToCode name=procedure dependencies=dependencies customVals=customVals />