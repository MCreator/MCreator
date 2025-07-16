<#include "mcitems.ftl">
{
	final String _tagName = ${input$tagName};
	final boolean _tagValue = ${input$tagValue};
	CustomData.update(DataComponents.CUSTOM_DATA, ${mappedMCItemToItemStackCode(input$item, 1)}, tag -> tag.putBoolean(_tagName, _tagValue));
}