if(entity instanceof EntityPlayer) {
    ((EntityPlayer)entity).capabilities.allowEdit = ${input$condition};
    ((EntityPlayer)entity).sendPlayerAbilities();
}