<#include "mcitems.ftl">
{
    BlockPos _bp = new BlockPos((int)${input$x},(int)${input$y},(int)${input$z});
    BlockState _bs = ${mappedBlockToBlockStateCode(input$block)};

    <#if field$state?lower_case == "true" || field$nbt?lower_case == "true">
    BlockState _bso = world.getBlockState(_bp);
    </#if>

    <#if field$state?lower_case == "true">
    for(Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
        Property _property = _bs.getBlock().getStateContainer().getProperty(entry.getKey().getName());
        if (_property != null && _bs.get(_property) != null)
            try {
            	_bs = _bs.with(_property, (Comparable) entry.getValue());
            } catch (Exception e) {}
    }
    </#if>

    <#if field$nbt?lower_case == "true">
    TileEntity _te = world.getTileEntity(_bp);
    CompoundNBT _bnbt = null;
    if(_te != null) {
        _bnbt = _te.write(new CompoundNBT());
        _te.remove();
    }
    </#if>

    world.setBlockState(_bp, _bs, 3);

    <#if field$nbt?lower_case == "true">
    if(_bnbt != null) {
        _te = world.getTileEntity(_bp);
        if(_te != null) {
            try {
                _te.read(_bso, _bnbt);
            } catch(Exception ignored) {}
        }
    }
    </#if>
}
