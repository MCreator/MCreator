<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
    ItemStack _stktoremove = ${mappedMCItemToItemStackCode(input$item, 1)};
    int _itemsRemoved = (int)${input$amount};
    for (int _slot = 0; _slot < ((PlayerEntity)${input$entity}).inventory.getSizeInventory(); _slot++) {
        if (((PlayerEntity)${input$entity}).inventory.getStackInSlot(_slot).getItem() == _stktoremove.getItem()) {
            _itemsRemoved -= ((PlayerEntity) ${input$entity}).inventory.getStackInSlot(_slot).getCount();
            ((PlayerEntity)${input$entity}).inventory.getStackInSlot(_slot).setCount(-(_itemsRemoved) >= 0 ?  -(_itemsRemoved) : 0);
            if (_itemsRemoved <= 0) {
                break;
            }
        }
    }
}
