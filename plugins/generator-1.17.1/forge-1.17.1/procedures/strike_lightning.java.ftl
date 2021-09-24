if(world instanceof ServerLevel _level) {
	LightningBolt _ent = EntityType.LIGHTNING_BOLT.create(_level);
    _ent.moveTo(Vec3.atBottomCenterOf(new BlockPos((int)${input$x},(int)${input$y},(int)${input$z})));
    _ent.setVisualOnly(${(field$effectOnly!false)?lower_case});
    _level.addFreshEntity(_ent);
}