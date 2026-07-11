if (${input$entity} instanceof LivingEntity _entity) {
	AttributeInstance _attrInst = _entity.getAttribute(Attributes.STEP_HEIGHT);
	if (_attrInst != null)
		_attrInst.setBaseValue(${opt.toFloat(input$stepHeight)});
}