<#include "mcitems.ftl">
<#include "procedures.java.ftl">
<#assign customVals = {}>
<#list depInputs as depInput>
	<#if depInput.type() == "blockstate">
		<#assign customVals += {depInput.name(): mappedBlockToBlockStateCode(depInput.value())}>
	<#elseif depInput.type() == "itemstack">
		<#assign customVals += {depInput.name(): mappedMCItemToItemStackCode(depInput.value())}>
	<#else>
		<#assign customVals += {depInput.name(): depInput.value()}>
	</#if>
</#list>
<#if type == "itemstack">/*@ItemStack*/<#elseif type == "blockstate">/*@BlockState*/</#if>
<@procedureToRetvalCode name=procedure dependencies=dependencies customVals=customVals />