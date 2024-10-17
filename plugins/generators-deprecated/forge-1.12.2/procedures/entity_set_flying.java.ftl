if(entity instanceof EntityPlayer) {
    ((EntityPlayer)entity).capabilities.isFlying = ${input$condition};
    ((EntityPlayer)entity).sendPlayerAbilities();
}