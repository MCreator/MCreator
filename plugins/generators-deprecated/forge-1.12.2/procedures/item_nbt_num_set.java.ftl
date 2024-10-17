<#include "mcitems.ftl">
{
	ItemStack _stack= ${mappedMCItemToItemStackCode(input$item, 1)};
	if(!_stack.hasTagCompound())
		_stack.setTagCompound(new NBTTagCompound());
	_stack.getTagCompound().setDouble(${input$tagName}, ${input$tagValue});
}