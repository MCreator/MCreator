<#include "procedures.java.ftl">
<#assign customVals = {}>
<#if depCount gt 0>
	<#list 0..(depCount - 1) as index>
		<#assign customVals += {names[index]: args[index]}>
	</#list>
</#if>
<@procedureToCode name=procedure dependencies=dependencies customVals=customVals />