if (${input$entity} instanceof LivingEntity _entity)
	_entity.hurt(new DamageSource(_entity.level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)) {
		@Override public Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
			return Component.translatable("death.attack." + ${input$localization_text});
		}
	}, ${opt.toFloat(input$damage_number)});