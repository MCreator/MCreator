if(entity instanceof EntityPlayer) {
    ((EntityPlayer)entity).capabilities.allowFlying = ${input$condition};
    ((EntityPlayer)entity).sendPlayerAbilities();
}