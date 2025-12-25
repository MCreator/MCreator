<#include "mcelements.ftl">
<#include "mcitems.ftl">
<@head>
if (!world.isClientSide()) {
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	BlockEntity _blockEntity = world.getBlockEntity(_bp);
	BlockState _bs = world.getBlockState(_bp);
	if(_blockEntity != null) {
</@head>
		_blockEntity.getPersistentData().put(${input$tagName}, (CompoundTag) ItemStack.OPTIONAL_CODEC.encode(${mappedMCItemToItemStackCode(input$tagValue, 1)}, NbtOps.INSTANCE, new CompoundTag()).result().orElse(new CompoundTag()));
<@tail>
	}
	if(world instanceof Level _level)
		_level.sendBlockUpdated(_bp, _bs, _bs, 3);
}</@tail>