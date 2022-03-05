if (world instanceof ServerLevel _origLevel) {
	LevelAccessor _worldorig = world;

	<#if field$dimension=="Surface">
		world = _origLevel.getServer().getLevel(Level.OVERWORLD);
	<#elseif field$dimension=="Nether">
		world = _origLevel.getServer().getLevel(Level.NETHER);
	<#elseif field$dimension=="End">
		world = _origLevel.getServer().getLevel(Level.END);
	<#else>
		world = _origLevel.getServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY,
			new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}")));
	</#if>

	if (world != null) {
		${statement$worldstatements}
	}

	world = _worldorig;
}