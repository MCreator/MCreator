<@head>if (${input$entity} instanceof LivingEntity _entity) {</@head>
	AttributeInstance _attrInst = _entity.getAttribute(Attributes.STEP_HEIGHT);
	if (_attrInst != null)
		_attrInst.setBaseValue(${opt.toFloat(input$stepHeight)});
<@tail>}</@tail>