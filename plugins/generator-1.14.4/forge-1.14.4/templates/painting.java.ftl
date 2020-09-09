<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.painting;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Painting extends ${JavaModName}Elements.ModElement{

	public ${name}Painting (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
	}
	
	@SubscribeEvent
	public void registerTileEntity(RegistryEvent.Register<PaintingType> event) {
		event.getRegistry().register(new PaintingType(${data.width}, ${data.height}).setRegistryName("${registryname}"));
	}

}
<#-- @formatter:on -->