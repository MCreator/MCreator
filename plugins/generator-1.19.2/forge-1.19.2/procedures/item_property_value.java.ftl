<#include "mcitems.ftl">
(new Object() {
	public float getItemPropertyValue(ItemStack _is, ResourceLocation _rl, LevelAccessor _level, Entity _entity) {
		if (_level.isClientSide()) {
			ItemPropertyFunction _ipf = ItemProperties.getProperty(_is.getItem(), _rl);
			if (_ipf != null && _entity instanceof LivingEntity _lEntity)
				return _ipf.call(_is, Minecraft.getInstance().level, _lEntity, _lEntity.getId());
		}
		return 0F;
	}
}.getItemPropertyValue(${mappedMCItemToItemStackCode(input$itemstack, 1)}, new ResourceLocation(${input$property}), world, ${input$entity}))