<#-- @formatter:off -->
package ${package};

import java.lang.invoke.MethodHandle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("${modid}") public class ${JavaModName} {

	public static final Logger LOGGER = LogManager.getLogger(${JavaModName}.class);

	public static final String MODID = "${modid}";

	public ${JavaModName}(IEventBus modEventBus) {
		// Start of user code block mod constructor
		// End of user code block mod constructor

		NeoForge.EVENT_BUS.register(this);

		modEventBus.addListener(this::registerNetworking);

		<@javacompress>
		<#if w.hasSounds()>${JavaModName}Sounds.REGISTRY.register(modEventBus);</#if>
		<#if types["base:blocks"]??>${JavaModName}Blocks.REGISTRY.register(modEventBus);</#if>
		<#if types["base:blockentities"]??>${JavaModName}BlockEntities.REGISTRY.register(modEventBus);</#if>
		<#if types["base:items"]??>${JavaModName}Items.REGISTRY.register(modEventBus);</#if>
		<#if types["base:entities"]??>${JavaModName}Entities.REGISTRY.register(modEventBus);</#if>
		<#if w.hasItemsInTabs()>${JavaModName}Tabs.REGISTRY.register(modEventBus);</#if>
		<#if w.hasVariables()>${JavaModName}Variables.ATTACHMENT_TYPES.register(modEventBus);</#if>
		<#if types["base:features"]??>${JavaModName}Features.REGISTRY.register(modEventBus);</#if>
		<#if w.getElementsOfType("feature")?filter(e -> e.getMetadata("has_nbt_structure")??)?size != 0>StructureFeature.REGISTRY.register(modEventBus);</#if>
		<#if types["potions"]??>${JavaModName}Potions.REGISTRY.register(modEventBus);</#if>
		<#if types["potioneffects"]??>${JavaModName}MobEffects.REGISTRY.register(modEventBus);</#if>
		<#if types["guis"]??>${JavaModName}Menus.REGISTRY.register(modEventBus);</#if>
		<#if types["particles"]??>${JavaModName}ParticleTypes.REGISTRY.register(modEventBus);</#if>
		<#if types["villagerprofessions"]??>${JavaModName}VillagerProfessions.PROFESSIONS.register(modEventBus);</#if>
		<#if types["fluids"]??>
			${JavaModName}Fluids.REGISTRY.register(modEventBus);
			${JavaModName}FluidTypes.REGISTRY.register(modEventBus);
		</#if>
		<#if types["attributes"]??>${JavaModName}Attributes.REGISTRY.register(modEventBus);</#if>
		</@javacompress>

		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods

	<#-- Networking support below -->
	private static boolean networkingRegistered = false;
	private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {}

	public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"}) private void registerNetworking(final RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(),
				((NetworkMessage) networkMessage).handler(), ((NetworkMessage) networkMessage).handler()));
		networkingRegistered = true;
	}

	<#-- Wait procedure block support below -->
	private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new Tuple<>(action, tick));
	}

	@SubscribeEvent public void tick(ServerTickEvent.Post event) {
		List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
		workQueue.forEach(work -> {
			work.setB(work.getB() - 1);
			if (work.getB() == 0)
				actions.add(work);
		});
		actions.forEach(e -> e.getA().run());
		workQueue.removeAll(actions);
	}

	<#-- Client side player query support below, we use method handles for this -->
	private static Object minecraft;
	private static MethodHandle playerHandle;
	@Nullable public static Player clientPlayer() {
		if (FMLEnvironment.dist.isClient()) {
			try {
				<#-- Lazy initialize and cache the Minecraft instance and player handle -->
				if (minecraft == null || playerHandle == null) {
					Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
					minecraft = MethodHandles.publicLookup().findStatic(minecraftClass, "getInstance", MethodType.methodType(minecraftClass)).invoke();
					playerHandle = MethodHandles.publicLookup().findGetter(minecraftClass, "player", Class.forName("net.minecraft.client.player.LocalPlayer"));
				}
				return (Player) playerHandle.invoke(minecraft);
			} catch (Throwable e) {
				LOGGER.error("Failed to get client player", e);
				return null;
			}
		} else {
			return null;
		}
	}

}
<#-- @formatter:on -->