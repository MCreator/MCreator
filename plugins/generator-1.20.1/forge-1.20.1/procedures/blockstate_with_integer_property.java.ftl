<#include "mcitems.ftl">
/*@BlockState*/(new Object() {
	public BlockState with(BlockState _bs, String _property, int _newValue) {
		Property<?> _prop = _bs.getBlock().getStateDefinition().getProperty(_property);
		return _prop instanceof IntegerProperty _ip && _prop.getPossibleValues().contains(_newValue) ? _bs.setValue(_ip, _newValue) : _bs;
}}.with(${mappedBlockToBlockStateCode(input$block)}, ${input$property}, ${opt.toInt(input$value)}))