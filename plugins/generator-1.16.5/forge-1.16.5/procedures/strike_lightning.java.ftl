if(world instanceof ServerWorld) {
	LightningBoltEntity _ent = EntityType.LIGHTNING_BOLT.create((World) world);
    _ent.moveForced(Vector3d.copyCenteredHorizontally(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})));
    _ent.setEffectOnly(${(field$effectOnly!false)?lower_case});
    ((World) world).addEntity(_ent);
}