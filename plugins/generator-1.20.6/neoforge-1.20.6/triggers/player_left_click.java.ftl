<#include "procedures.java.ftl">
@EventBusSubscriber(value = {Dist.CLIENT}) public class ${name}Procedure {
	@SubscribeEvent public static void onLeftClick(PlayerInteractEvent.LeftClickEmpty event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"world": "event.getLevel()",
			"entity": "event.getEntity()"
			}/>
		</#compress></#assign>
		PacketDistributor.sendToServer(new ${name}Message());
		execute(${dependenciesCode});
	}

	@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
	public static record ${name}Message() implements CustomPacketPayload {
		public static final Type<${name}Message> TYPE = new Type<>(new ResourceLocation(${JavaModName}.MODID, "procedure_${registryname}"));

		public static final StreamCodec<RegistryFriendlyByteBuf, ${name}Message> STREAM_CODEC = StreamCodec.of(
			(RegistryFriendlyByteBuf buffer, ${name}Message message) -> {},
			(RegistryFriendlyByteBuf buffer) -> new ${name}Message()
		);

		@Override public Type<${name}Message> type() {
    		return TYPE;
    	}

    	public static void handleData(final ${name}Message message, final IPayloadContext context) {
    		if (context.flow() == PacketFlow.SERVERBOUND) {
    			context.enqueueWork(() -> {
    				if (!context.player().level().hasChunkAt(context.player().blockPosition()))
        				return;
        			<#assign dependenciesCode><#compress>
        				<@procedureDependenciesCode dependencies, {
        				"x": "context.player().getPos().getX()",
        				"y": "context.player().getPos().getY()",
        				"z": "context.player().getPos().getZ()",
        				"world": "context.player().level()",
        				"entity": "context.player()"
        				}/>
        			</#compress></#assign>
    				execute(${dependenciesCode});
    			}).exceptionally(e -> {
    				context.connection().disconnect(Component.literal(e.getMessage()));
    				return null;
    			});
    		}
    	}

		@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
			${JavaModName}.addNetworkMessage(${name}Message.TYPE, ${name}Message.STREAM_CODEC, ${name}Message::handleData);
		}
	}