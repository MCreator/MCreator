if (world instanceof ServerWorld) {
    IWorld _worldorig = world;

    <#if field$dimension=="Surface">
        world = ((ServerWorld) world).getServer().getWorld(World.OVERWORLD);
    <#elseif field$dimension=="Nether">
        world = ((ServerWorld) world).getServer().getWorld(World.THE_NETHER);
    <#elseif field$dimension=="End">
        world = ((ServerWorld) world).getServer().getWorld(World.THE_END);
    <#else>
        world = ((ServerWorld) world).getServer().getWorld(RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
				new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}")));
    </#if>

    ${statement$worldstatements}

    world = _worldorig;
}