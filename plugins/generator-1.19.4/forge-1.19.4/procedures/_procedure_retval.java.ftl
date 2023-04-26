<#include "procedures.java.ftl">
<#assign customVals = {}>
<#if depCount gt 0>
	<#list 0..(depCount - 1) as index>
		<#assign customVals += {names[index]: args[index]}>
	</#list>
</#if>
<#if type == "itemstack">/*@ItemStack*/<#elseif type == "blockstate">/*@BlockState*/</#if>
<@procedureToRetvalCode name=procedure dependencies=dependencies customVals=customVals />