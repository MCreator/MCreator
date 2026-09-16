if (world instanceof ServerLevel _origLevel) {
	LevelAccessor _switchworld${cbi} = _origLevel.getServer().getLevel(${generator.map(field$dimension, "dimensions")});
	if (_switchworld${cbi} != null) {
		worldSwitch${cbi}(_switchworld${cbi} @procedureArgsNoWorld@);
	}
}

<@addAdditionalCode>
private static void worldSwitch${cbi}(LevelAccessor world @procedureSignatureNoWorld@) {
	${statement$worldstatements}
}
</@addAdditionalCode>