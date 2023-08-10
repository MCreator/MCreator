{
	Entity _ent = ${input$entity};
	_ent.setYRot(${opt.toFloat(input$yaw)});
	_ent.setXRot(${opt.toFloat(input$pitch)});
	_ent.setYBodyRot(_ent.getYRot());
	_ent.setYHeadRot(_ent.getYRot());
	_ent.yRotO = _ent.getYRot();
	_ent.xRotO = _ent.getXRot();
	if (_ent instanceof LivingEntity _entity) {
		_entity.yBodyRotO = _entity.getYRot();
		_entity.yHeadRotO = _entity.getYRot();
	}
}