${input$entity}.rotationYaw = (float) (${input$yaw});
entity.setRenderYawOffset(entity.rotationYaw);
entity.prevRotationYaw = entity.rotationYaw;

if(entity instanceof LivingEntity) {
    ((LivingEntity) entity).prevRenderYawOffset = entity.rotationYaw;
    ((LivingEntity) entity).rotationYawHead = entity.rotationYaw;
    ((LivingEntity) entity).prevRotationYawHead = entity.rotationYaw;
}

${input$entity}.rotationPitch = (float) (${input$pitch});