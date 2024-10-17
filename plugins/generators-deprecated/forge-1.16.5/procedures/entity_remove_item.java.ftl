<#include "mcitems.ftl">
if(${input$entity} instanceof PlayerEntity) {
    ItemStack _stktoremove = ${mappedMCItemToItemStackCode(input$item, 1)};
    ((PlayerEntity)${input$entity}).inventory
        .func_234564_a_(p -> _stktoremove.getItem() == p.getItem(), (int) ${input$amount}, ((PlayerEntity)${input$entity}).container.func_234641_j_());
}