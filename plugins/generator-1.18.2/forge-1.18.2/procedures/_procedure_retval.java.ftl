<#include "procedures.java.ftl">
<#assign customVals = {}>
<#if paramsCount gt 0>
	<#list 0..(paramsCount - 1) as index>
		<#assign customVals += {names[index]: args[index]}>
	</#list>
</#if>
<#if type == "itemstack">/*@ItemStack*/<#elseif type == "blockstate">/*@BlockState*/</#if>
<@procedureToRetvalCode name=procedure dependencies=dependencies customVals=customVals />