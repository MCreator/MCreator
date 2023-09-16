if (${input$entity} instanceof LivingEntity _entity)
	_entity.hurt(new DamageSource(_entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.GENERIC)) {
		@Override public Component getLocalizedDeathMessage(LivingEntity _msgEntity) {
			String _translatekey = "death.attack." + ${input$localization_text};
			if (this.getEntity() == null && this.getDirectEntity() == null) {
				return _msgEntity.getKillCredit() != null ?
					Component.translatable(_translatekey + ".player", _msgEntity.getDisplayName(), _msgEntity.getKillCredit().getDisplayName()) :
					Component.translatable(_translatekey, _msgEntity.getDisplayName());
			} else {
				Component _component = this.getEntity() == null ? this.getDirectEntity().getDisplayName() : this.getEntity().getDisplayName();
				ItemStack _itemstack = ItemStack.EMPTY;
				if (this.getEntity() instanceof LivingEntity _livingentity)
					_itemstack = _livingentity.getMainHandItem();
				return !_itemstack.isEmpty() && _itemstack.hasCustomHoverName() ? 
					Component.translatable(_translatekey + ".item", _msgEntity.getDisplayName(), _component, _itemstack.getDisplayName()) : 
					Component.translatable(_translatekey, _msgEntity.getDisplayName(), _component);
			}
		}
	}, ${opt.toFloat(input$damage_number)});