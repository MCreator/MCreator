<#include "mcitems.ftl">
{
	Entity _entity${cbi} = ${input$entity};
	_entity${cbi}.getPersistentData().put(${input$tagName}, ${mappedMCItemToItemStackCode(input$tagValue, 1)}.saveOptional(_entity${cbi}.level().registryAccess()));
}