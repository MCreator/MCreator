if (world instanceof ServerWorld) {
    IWorld _worldorig = world;

    <#if field$dimension=="Surface">
        RegistryKey<World> world = World.OVERWORLD;
    <#elseif field$dimension=="Nether">
        RegistryKey<World> world = World.THE_NETHER;
    <#elseif field$dimension=="End">
        RegistryKey<World> world = World.THE_END;
    <#else>
        RegistryKey<World> world = RegistryKey.getOrCreateKey(Registry.WORLD_KEY,
        				new ResourceLocation("${generator.getResourceLocationForModElement(field$dimension.replace("CUSTOM:", ""))}"));
    </#if>

    ${statement$worldstatements}

    world = _worldorig;
}