<#include "mcitems.ftl">
(BlockTags.getCollection().getTagByID(new ResourceLocation((${input$tag}).toLowerCase(java.util.Locale.ENGLISH))).contains(${mappedBlockToBlockStateCode(input$item)}.getBlock()))