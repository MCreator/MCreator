<#include "procedures.java.ftl">
@Mod.EventBusSubscriber(value = {Dist.CLIENT}) public class ${name}Procedure {
	@SubscribeEvent public static void onLeftClick(PlayerInteractEvent.RightClickEmpty event) {
		<#assign dependenciesCode><#compress>
			<@procedureDependenciesCode dependencies, {
			"x": "event.getPos().getX()",
			"y": "event.getPos().getY()",
			"z": "event.getPos().getZ()",
			"world": "event.getLevel()",
			"entity": "event.getEntity()"
			}/>
		</#compress></#assign>
		${JavaModName}.PACKET_HANDLER.sendToServer(new ${name}Message());
		execute(${dependenciesCode});
	}

	@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
	public static class ${name}Message {
		public ${name}Message() {}

		public ${name}Message(FriendlyByteBuf buffer) {}

		public static void buffer(${name}Message message, FriendlyByteBuf buffer) {}

		public static void handler(${name}Message message, Supplier<NetworkEvent.Context> contextSupplier) {
    		NetworkEvent.Context context = contextSupplier.get();
    		context.enqueueWork(() -> {
    			if (!context.getSender().level().hasChunkAt(context.getSender().blockPosition()))
        			return;
        		<#assign dependenciesCode><#compress>
        			<@procedureDependenciesCode dependencies, {
        			"x": "context.getSender().getX()",
        			"y": "context.getSender().getY()",
        			"z": "context.getSender().getZ()",
        			"world": "context.getSender().level()",
        			"entity": "context.getSender()"
        			}/>
        		</#compress></#assign>
    			execute(${dependenciesCode});
    		});
    		context.setPacketHandled(true);
    	}

    	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
    		${JavaModName}.addNetworkMessage(${name}Message.class, ${name}Message::buffer, ${name}Message::new, ${name}Message::handler);
    	}
	}