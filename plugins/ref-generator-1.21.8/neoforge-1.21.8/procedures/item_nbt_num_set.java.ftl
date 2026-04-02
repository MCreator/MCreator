<#include "mcitems.ftl">
{
	final String _tagName = ${input$tagName};
	final double _tagValue = ${input$tagValue};
	CustomData.update(DataComponents.CUSTOM_DATA, ${mappedMCItemToItemStackCode(input$item, 1)}, tag -> tag.putDouble(_tagName, _tagValue));
}