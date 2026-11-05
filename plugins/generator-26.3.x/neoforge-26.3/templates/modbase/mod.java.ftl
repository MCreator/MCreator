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
		<#if types.base_blocks?has_content>${JavaModName}Blocks.REGISTRY.register(modEventBus);</#if>
		<#if types.base_blockentities?has_content>${JavaModName}BlockEntities.REGISTRY.register(modEventBus);</#if>
		<#if types.base_items?has_content>${JavaModName}Items.REGISTRY.register(modEventBus);</#if>
		<#if types.base_entities?has_content>${JavaModName}Entities.REGISTRY.register(modEventBus);</#if>
		<#if w.hasItemsInTabs()>${JavaModName}Tabs.REGISTRY.register(modEventBus);</#if>
		<#if w.hasVariables()>${JavaModName}Variables.ATTACHMENT_TYPES.register(modEventBus);</#if>
		<#if types.base_features?has_content>${JavaModName}FeatureTypes.REGISTRY.register(modEventBus);</#if>
		<#if types.features?filter(e -> e.getModElement().getMetadata("has_nbt_structure")??)?size != 0>StructureFeature.REGISTRY.register(modEventBus);</#if>
		<#if types.potions?has_content>${JavaModName}Potions.REGISTRY.register(modEventBus);</#if>
		<#if types.potioneffects?has_content>${JavaModName}MobEffects.REGISTRY.register(modEventBus);</#if>
		<#if types.guis?has_content>${JavaModName}Menus.REGISTRY.register(modEventBus);</#if>
		<#if types.particles?has_content>${JavaModName}ParticleTypes.REGISTRY.register(modEventBus);</#if>
		<#if types.villagerprofessions?has_content>${JavaModName}VillagerProfessions.PROFESSIONS.register(modEventBus);</#if>
		<#if types.gamerules?has_content>${JavaModName}GameRules.REGISTRY.register(modEventBus);</#if>
		<#if types.fluids?has_content>
			${JavaModName}Fluids.REGISTRY.register(modEventBus);
			${JavaModName}FluidTypes.REGISTRY.register(modEventBus);
		</#if>
		<#if types.attributes?has_content>${JavaModName}Attributes.REGISTRY.register(modEventBus);</#if>
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
	private static final Queue<IntObjectPair<Runnable>> workToBeScheduled = new ConcurrentLinkedQueue<>();
	private static final PriorityQueue<TickTask> workQueue = new PriorityQueue<>(Comparator.comparingInt(TickTask::getTick));

	public static void queueServerWork(int delay, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workToBeScheduled.add(new IntObjectImmutablePair<>(delay, action));
	}

	@SubscribeEvent public void tick(ServerTickEvent.Post event) {
		int currentTick = event.getServer().getTickCount();

		IntObjectPair<Runnable> work;
		while ((work = workToBeScheduled.poll()) != null) {
			workQueue.add(new TickTask(currentTick + work.leftInt(), work.right()));
		}

		while (!workQueue.isEmpty() && currentTick >= workQueue.peek().getTick()) {
			workQueue.poll().run();
		}
	}

	<#-- Client side player query support below, we use method handles for this -->
	private static Object minecraft;
	private static MethodHandle playerHandle;
	@Nullable public static Player clientPlayer() {
		if (FMLEnvironment.getDist().isClient()) {
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