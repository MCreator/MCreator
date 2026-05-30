{
	Entity _ent = ${input$entity};
	double _tx = ${input$x};
	double _ty = ${input$y};
	double _tz = ${input$z};
	_ent.teleportTo(_tx, _ty, _tz);
	if (_ent instanceof ServerPlayer _serverPlayer)
		_serverPlayer.connection.teleport(_tx, _ty, _tz, _ent.getYRot(), _ent.getXRot());
}