<#include "mcitems.ftl">
/*@ItemStack*/(ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, ${mappedMCItemToItemStackCode(input$item, 1)}
	.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getCompoundOrEmpty(${input$tagName})).result().orElse(ItemStack.EMPTY))