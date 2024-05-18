<#-- @formatter:off -->
package ${package};

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

		<#if w.hasSounds()>${JavaModName}Sounds.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfBaseType("block")>${JavaModName}Blocks.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfBaseType("blockentity")>${JavaModName}BlockEntities.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfBaseType("item")>${JavaModName}Items.register(modEventBus);</#if>
		<#if w.hasElementsOfBaseType("entity")>${JavaModName}Entities.REGISTRY.register(modEventBus);</#if>
		<#if w.hasItemsInTabs()>${JavaModName}Tabs.REGISTRY.register(modEventBus);</#if>
		<#if w.hasVariables()>${JavaModName}Variables.ATTACHMENT_TYPES.register(modEventBus);</#if>
		<#if w.hasElementsOfBaseType("feature")>${JavaModName}Features.REGISTRY.register(modEventBus);</#if>
		<#if w.getElementsOfType("feature")?filter(e -> e.getMetadata("has_nbt_structure")??)?size != 0>StructureFeature.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("painting")>${JavaModName}Paintings.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("potion")>${JavaModName}Potions.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("potioneffect")>${JavaModName}MobEffects.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("enchantment")>${JavaModName}Enchantments.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("gui")>${JavaModName}Menus.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("particle")>${JavaModName}ParticleTypes.REGISTRY.register(modEventBus);</#if>
		<#if w.hasElementsOfType("villagerprofession")>${JavaModName}VillagerProfessions.PROFESSIONS.register(modEventBus);</#if>
		<#if w.hasElementsOfType("fluid")>
			${JavaModName}Fluids.REGISTRY.register(modEventBus);
			${JavaModName}FluidTypes.REGISTRY.register(modEventBus);
		</#if>

		// Start of user code block mod init
		// End of user code block mod init
	}

	// Start of user code block mod methods
	// End of user code block mod methods

	<#-- Networking support below -->
	private static boolean networkingRegistered = false;
	private static final Map<ResourceLocation, NetworkMessage<?>> MESSAGES = new HashMap<>();

	private record NetworkMessage<T extends CustomPacketPayload>(FriendlyByteBuf.Reader<T> reader, IPlayPayloadHandler<T> handler) {}

	public static <T extends CustomPacketPayload> void addNetworkMessage(ResourceLocation id, FriendlyByteBuf.Reader<T> reader, IPlayPayloadHandler<T> handler) {
		if (networkingRegistered)
			throw new IllegalStateException("Cannot register new network messages after networking has been registered");
		MESSAGES.put(id, new NetworkMessage<>(reader, handler));
	}

	@SuppressWarnings({"rawtypes", "unchecked"}) private void registerNetworking(final RegisterPayloadHandlerEvent event) {
		final IPayloadRegistrar registrar = event.registrar(MODID);
		MESSAGES.forEach((id, networkMessage) -> registrar.play(id, ((NetworkMessage) networkMessage).reader(), networkMessage.handler()));
		networkingRegistered = true;
	}

	<#-- Wait procedure block support below -->
	private static final Collection<Tuple<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

	public static void queueServerWork(int tick, Runnable action) {
		if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
			workQueue.add(new Tuple<>(action, tick));
	}

	@SubscribeEvent public void tick(TickEvent.ServerTickEvent event) {
		if (event.phase == TickEvent.Phase.END) {
			List<Tuple<Runnable, Integer>> actions = new ArrayList<>();
			workQueue.forEach(work -> {
				work.setB(work.getB() - 1);
				if (work.getB() == 0)
					actions.add(work);
			});
			actions.forEach(e -> e.getA().run());
			workQueue.removeAll(actions);
		}
	}

}
<#-- @formatter:on -->