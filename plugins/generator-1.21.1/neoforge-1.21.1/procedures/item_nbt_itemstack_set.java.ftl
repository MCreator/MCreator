<#include "mcitems.ftl">
{
	final String _tagName = ${input$tagName};
	final ItemStack _tagValue = ${mappedMCItemToItemStackCode(input$tagValue, 1)};
	CustomData.update(DataComponents.CUSTOM_DATA, ${mappedMCItemToItemStackCode(input$item, 1)}, tag -> tag.put(_tagName, _tagValue.saveOptional(world.registryAccess())));
}