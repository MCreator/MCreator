if(itemstack.attemptDamageItem((int) ${input$amount},new Random(),null)){
    itemstack.shrink(1);
    itemstack.setItemDamage(0);
}