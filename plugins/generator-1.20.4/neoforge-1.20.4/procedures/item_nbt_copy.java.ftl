<#include "mcitems.ftl">
{
    CompoundTag _nbtTag = ${mappedMCItemToItemStackCode(input$a, 1)}.getTag();
    if (_nbtTag != null)
        ${mappedMCItemToItemStackCode(input$b, 1)}.setTag(_nbtTag.copy());
}