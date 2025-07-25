<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#if field$nbt == "FALSE" && field$state == "FALSE">
world.setBlock(${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)},3);
<#else>
{
	BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
	BlockState _bs = ${mappedBlockToBlockStateCode(input$block)};

	<#if field$state == "TRUE">
	BlockState _bso = world.getBlockState(_bp);
	for(Property<?> _propertyOld : _bso.getProperties()) {
		Property _propertyNew = _bs.getBlock().getStateDefinition().getProperty(_propertyOld.getName());
		if (_propertyNew != null && _bs.getValue(_propertyNew) != null)
			try {
				_bs = _bs.setValue(_propertyNew, _bso.getValue(_propertyOld));
			} catch (Exception e) {}
	}
	</#if>

	<#if field$nbt == "TRUE">
	BlockEntity _be = world.getBlockEntity(_bp);
	CompoundTag _bnbt = null;
	if(_be != null) {
		_bnbt = _be.saveWithFullMetadata(world.registryAccess());
		_be.setRemoved();
	}
	</#if>

	world.setBlock(_bp, _bs, 3);

	<#if field$nbt == "TRUE">
	if(_bnbt != null) {
		_be = world.getBlockEntity(_bp);
		if(_be != null) {
			try {
				_be.loadWithComponents(_bnbt, world.registryAccess());
			} catch(Exception ignored) {}
		}
	}
	</#if>
}
</#if>