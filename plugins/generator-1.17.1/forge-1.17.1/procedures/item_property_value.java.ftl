(new Object() {
	public float getItemPropertyValue(ItemStack _is, ResourceLocation _rl, Level _level, LivingEntity _entity) {
		ItemPropertyFunction _ipf = ItemProperties.getProperty(_is.getItem(), _rl);
		if (_ipf != null)
			return _ipf.call(_is, _level, _entity, 0);
		return 0F;
	}
}.getItemPropertyValue(${input$itemstack}, new ResourceLocation(${input$property}), world, ${input$entity}))