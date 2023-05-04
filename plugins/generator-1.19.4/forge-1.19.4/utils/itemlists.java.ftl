<#include "mcitems.ftl">

<#macro blockListBasedOnDirectChecks elements blockToCheck>
	<#assign blocks = []>
	<#assign tags = []>
	<#list elements as block>
		<#if block.getUnmappedValue().startsWith("TAG:")>
			<#assign tags += [block.getUnmappedValue().replace("TAG:", "")]>
		<#elseif generator.map(block.getUnmappedValue(), "blocksitems", 1).startsWith("#")>
			<#assign tags += [generator.map(block.getUnmappedValue(), "blocksitems", 1).replace("#", "")]>
		<#else>
			<#assign blocks += [mappedBlockToBlock(block)]>
		</#if>
	</#list>
	<#if !blocks?has_content && !tags?has_content>
	    false
	<#elseif blocks?has_content>
		List.of(
			<#list blocks as block>
				${block}<#sep>,
			</#list>
		).contains(${blockToCheck}.getBlock())
        <#if tags?has_content> || </#if>
	</#if>
	<#if tags?has_content>
		Stream.of(
			<#list tags as tag>
				BlockTags.create(new ResourceLocation("${tag}"))<#sep>,
			</#list>
		).anyMatch(${blockToCheck}::is)
	</#if>
</#macro>