<#-- @formatter:off -->
/*
 *    MCreator note: This file will be REGENERATED on each build.
 *
 */

package ${package};

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("${modid}")
public class ${JavaModName} {

	public static final Logger LOGGER = LogManager.getLogger(${JavaModName}.class);

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation("${modid}", "${modid}"),
		() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	public ${JavaModName}() {
		FMLJavaModLoadingContext.get().getModEventBus().register(this);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::init);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientLoad);

		<#if w.hasCreativeTabs()>
		CreativeModeTabs.load();
		</#if>

		<#if w.hasBrewingRecipes()>
		BrewingRecipes.load();
		</#if>
	}

	private void init(FMLCommonSetupEvent event) {
	}

	public void clientLoad(FMLClientSetupEvent event) {
	}

	@SubscribeEvent public void registerBlocks(RegistryEvent.Register<Block> event) {
	}

	@SubscribeEvent public void registerItems(RegistryEvent.Register<Item> event) {
	}

	@SubscribeEvent public void registerEntities(RegistryEvent.Register<EntityType<?>> event) {
	}

	@SubscribeEvent public void registerEnchantments(RegistryEvent.Register<Enchantment> event) {
	}

	@SubscribeEvent public void registerSounds(RegistryEvent.Register<net.minecraft.sounds.SoundEvent> event) {
	}

}
<#-- @formatter:on -->