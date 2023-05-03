<#include "mcelements.ftl">
<#include "mcitems.ftl">
<#if field$nbt?lower_case == "false" && field$state?lower_case == "false">
world.setBlock(${toBlockPos(input$x,input$y,input$z)}, ${mappedBlockToBlockStateCode(input$block)},3);
<#else>
{
    BlockPos _bp = ${toBlockPos(input$x,input$y,input$z)};
    BlockState _bs = ${mappedBlockToBlockStateCode(input$block)};

    <#if field$state?lower_case == "true">
    BlockState _bso = world.getBlockState(_bp);
    for(Map.Entry<Property<?>, Comparable<?>> entry : _bso.getValues().entrySet()) {
        Property _property = _bs.getBlock().getStateDefinition().getProperty(entry.getKey().getName());
        if (_property != null && _bs.getValue(_property) != null)
            try {
            	_bs = _bs.setValue(_property, (Comparable) entry.getValue());
            } catch (Exception e) {}
    }
    </#if>

    <#if field$nbt?lower_case == "true">
    BlockEntity _be = world.getBlockEntity(_bp);
    CompoundTag _bnbt = null;
    if(_be != null) {
        _bnbt = _be.saveWithFullMetadata();
        _be.setRemoved();
    }
    </#if>

    world.setBlock(_bp, _bs, 3);

    <#if field$nbt?lower_case == "true">
    if(_bnbt != null) {
        _be = world.getBlockEntity(_bp);
        if(_be != null) {
            try {
                _be.load(_bnbt);
            } catch(Exception ignored) {}
        }
    }
    </#if>
}
</#if>