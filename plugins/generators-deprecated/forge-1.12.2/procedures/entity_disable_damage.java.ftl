if(entity instanceof EntityPlayer) {
    ((EntityPlayer)entity).capabilities.disableDamage = ${input$condition};
    ((EntityPlayer)entity).sendPlayerAbilities();
}