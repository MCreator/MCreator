(new Object() {
	public float getItemPropertyValue(ItemStack _is, ResourceLocation _rl, LevelAccessor _level, Entity _entity) {
		ItemPropertyFunction _ipf = ItemProperties.getProperty(_is.getItem(), _rl);
		if (_ipf != null && _level instanceof ClientLevel _cLevel && _entity instanceof LivingEntity _lEntity)
			return _ipf.call(_is, _cLevel, _lEntity, 0);
		return 0F;
	}
}.getItemPropertyValue(${input$itemstack}, new ResourceLocation(${input$property}), world, ${input$entity}))