<#include "mcitems.ftl">
(new Object() {
	public boolean get(BlockState _bs, String _property) {
		Property<?> _prop = _bs.getBlock().getStateContainer().getProperty(_property);
		return _prop instanceof BooleanProperty ? _bs.get((BooleanProperty)_prop) : false;
}}.get(${mappedBlockToBlockStateCode(input$block)}, ${input$property}))