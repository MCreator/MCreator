(new Object() {
	public float getItemPropertyValue(ItemStack _is, ResourceLocation _rl, World _world, LivingEntity _entity) {
		IItemPropertyGetter _iipg = ItemModelsProperties.func_239417_a_(_is.getItem(), _rl);
		if (_iipg != null)
			return _iipg.call(_is, _world, _entity);
		return 0F;
	}
}.getItemPropertyValue(${input$itemstack}, new ResourceLocation(${input$property}), world, ${input$entity}))