(new Object() {
	public float getItemPropertyValue(ItemStack _is, ResourceLocation _rl, World _world, Entity _entity) {
		IItemPropertyGetter _iipg = ItemModelsProperties.func_239417_a_(_is.getItem(), _rl);
		if (_iipg != null && _entity instanceof LivingEntity)
			return _iipg.call(_is, _world, (LivingEntity) _entity);
		return 0F;
	}
}.getItemPropertyValue(${input$itemstack}, new ResourceLocation(${input$property}), world, ${input$entity}))