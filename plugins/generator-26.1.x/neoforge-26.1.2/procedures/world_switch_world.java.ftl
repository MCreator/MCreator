if (world instanceof ServerLevel _origLevel) {
	LevelAccessor _switchworld${cbi} = _origLevel.getServer().getLevel(${generator.map(field$dimension, "dimensions")});
	if (_switchworld${cbi} != null) {
		worldSwitch${cbi}(@procedureArgs@);
	}
}

<@addAdditionalCode>
private static void worldSwitch${cbi}(@procedureSignature@) {
	${statement$worldstatements}
}
</@addAdditionalCode>