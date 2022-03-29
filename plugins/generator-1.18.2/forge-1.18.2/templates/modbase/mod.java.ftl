<#-- @formatter:off -->
/*
 *    MCreator note:
 *
 *    If you lock base mod element files, you can edit this file and it won't get overwritten.
 *    If you change your modid or package, you need to apply these changes to this file MANUALLY.
 *
 *    Settings in @Mod annotation WON'T be changed in case of the base mod element
 *    files lock too, so you need to set them manually here in such case.
 *
 *    If you do not lock base mod element files in Workspace settings, this file
 *    will be REGENERATED on each build.
 *
 */

package ${package};

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("${modid}") public class ${JavaModName} {

	public static final Logger LOGGER = LogManager.getLogger(${JavaModName}.class);

	public static final String MODID = "${modid}";

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID),
		() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

	private static int messageID = 0;

	public ${JavaModName}() {
		<#if w.hasElementsOfType("tab")>${JavaModName}Tabs.load();</#if>

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		<#if w.hasElementsOfBaseType("block")>${JavaModName}Blocks.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfBaseType("item")>${JavaModName}Items.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfBaseType("entity")>${JavaModName}Entities.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfBaseType("blockentity")>${JavaModName}BlockEntities.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfBaseType("feature")>${JavaModName}Features.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("fluid")>${JavaModName}Fluids.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("enchantment")>${JavaModName}Enchantments.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("potioneffect")>${JavaModName}MobEffects.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("potion")>${JavaModName}Potions.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("biome")>${JavaModName}Biomes.REGISTRY.register(bus);</#if>
		<#if w.hasElementsOfType("particle")>${JavaModName}ParticleTypes.REGISTRY.register(bus);</#if>
	}

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
										BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

}
<#-- @formatter:on -->