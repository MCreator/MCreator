<#-- @formatter:off -->
package ${package};

public interface IProxy${JavaModName} {

	void preInit(FMLPreInitializationEvent event);

	void init(FMLInitializationEvent event);

	void postInit(FMLPostInitializationEvent event);

	void serverLoad(FMLServerStartingEvent event);

}
<#-- @formatter:on -->