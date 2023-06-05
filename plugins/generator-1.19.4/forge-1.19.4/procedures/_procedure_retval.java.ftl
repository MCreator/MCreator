<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#assign customVals = {}>
<#if depCount gt 0>
	<#list 0..(depCount - 1) as index>
		<#if types[index] == "blockstate">
			<#assign customVals += {names[index]: mappedBlockToBlockStateCode(args[index])}>
		<#elseif types[index] == "itemstack">
			<#assign customVals += {names[index]: mappedMCItemToItemStackCode(args[index], 1)}>
		<#else>
			<#assign customVals += {names[index]: args[index]}>
		</#if>
	</#list>
</#if>
<#if type == "itemstack">/*@ItemStack*/<#elseif type == "blockstate">/*@BlockState*/</#if>
<@procedureToRetvalCode name=procedure dependencies=dependencies customVals=customVals />