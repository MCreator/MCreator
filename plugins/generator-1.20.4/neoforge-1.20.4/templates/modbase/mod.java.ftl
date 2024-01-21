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

	public ${JavaModName}(IEventBus modEventBus) {
		NeoForge.EVENT_BUS.register(this);

		modEventBus.addListener(this::init);
		modEventBus.addListener(this::registerNetworking);

		<#if w.hasSounds()>${JavaModName}Sounds.REGISTRY.register(modEventBus);</#if>
	}

	private void init(FMLCommonSetupEvent event) {
		System.err.println("FIRST");
	}

	private void registerNetworking(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(MODID);
		System.err.println("SECOND");
	}

	//public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(
	//		new ResourceLocation(MODID, MODID),
	//		() -> PROTOCOL_VERSION,
	//		PROTOCOL_VERSION::equals,
	//			<#if settings.isServerSideOnly()>clientVersion -> true<#else>PROTOCOL_VERSION::equals</#if>
	//);
	//public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder,
	//									BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
	//TODO: https://neoforged.net/news/20.4networking-rework/
	//TODO: https://docs.neoforged.net/docs/networking/payload
	//}

	private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		workQueue.add(new AbstractMap.SimpleEntry(action, tick));
	}

	@SubscribeEvent public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setValue(work.getValue() - 1);
				if (work.getValue() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getKey().run());
			workQueue.removeAll(actions);
		}
	}

}
<#-- @formatter:on -->