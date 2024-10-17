<#include "mcitems.ftl">
{
	BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
	IBlockState _bs = ${mappedBlockToBlockStateCode(input$block)};

	<#if field$state?lower_case == "true">
	IBlockState _bso = world.getBlockState(_bp);
	for(Map.Entry<IProperty<?>, Comparable<?>> entry : _bso.getProperties().entrySet()) {
		IProperty _property = entry.getKey();
		if (_bs.getPropertyKeys().contains(_property))
			_bs = _bs.withProperty(_property, (Comparable) entry.getValue());
	}
	</#if>

	<#if field$nbt?lower_case == "true">
	TileEntity _te = world.getTileEntity(_bp);
	NBTTagCompound _bnbt = null;
	if(_te != null) {
		_bnbt = _te.writeToNBT(new NBTTagCompound());
		_te.invalidate();
	}
	</#if>

	world.setBlockState(_bp, _bs, 3);

	<#if field$nbt?lower_case == "true">
	if(_bnbt != null) {
		_te = world.getTileEntity(_bp);
		if(_te != null) {
			try {
				_te.readFromNBT(_bnbt);
			} catch(Exception ignored) {}
		}
	}
	</#if>
}