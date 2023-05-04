{
	Entity _ent = ${input$entity};
	_ent.teleportTo(${input$x},${input$y},${input$z});
	if (_ent instanceof ServerPlayer _serverPlayer)
		_serverPlayer.connection.teleport(${input$x}, ${input$y}, ${input$z}, _ent.getYRot(), _ent.getXRot());
}