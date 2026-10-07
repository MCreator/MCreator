<#include "mcitems.ftl">
{
	final String _tagName = ${input$tagName};
	final String _tagValue = ${input$tagValue};
	CustomData.update(DataComponents.CUSTOM_DATA, ${mappedMCItemToItemStackCode(input$item, 1)}, tag -> tag.putString(_tagName, _tagValue));
}