<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.keybind;

@Elements${JavaModName}.ModElement.Tag public class KeyBinding${name} extends Elements${JavaModName}.ModElement {

	private KeyBinding keys;

	public KeyBinding${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void preInit(FMLPreInitializationEvent event) {
		elements.addNetworkMessage(KeyBindingPressedMessageHandler.class, KeyBindingPressedMessage.class, Side.SERVER);
	}

	@SideOnly(Side.CLIENT) @Override public void init(FMLInitializationEvent event) {
		keys = new KeyBinding("key.mcreator.${registryname}", Keyboard.KEY_${generator.map(data.triggerKey, "keybuttons")}, "key.categories.misc");
		ClientRegistry.registerKeyBinding(keys);

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent @SideOnly(Side.CLIENT) public void onKeyInput(InputEvent.KeyInputEvent event) {
		<#if hasProcedure(data.onKeyPressed)>
			if (Minecraft.getMinecraft().currentScreen == null) {
				if (org.lwjgl.input.Keyboard.isKeyDown(keys.getKeyCode())) {
					${JavaModName}.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage());
					pressAction(Minecraft.getMinecraft().player);
				}
			}
    	</#if>
	}

	public static class KeyBindingPressedMessageHandler implements IMessageHandler<KeyBindingPressedMessage, IMessage> {

		@Override public IMessage onMessage(KeyBindingPressedMessage message, MessageContext context) {
	    	EntityPlayerMP entity = context.getServerHandler().player;
	    	entity.getServerWorld().addScheduledTask(() -> {
	    		<#if hasProcedure(data.onKeyPressed)>
				pressAction(entity);
				</#if>
	    	});
	    	return null;
		}
	}

	public static class KeyBindingPressedMessage implements IMessage {

		@Override public void toBytes(io.netty.buffer.ByteBuf buf) {
		}

		@Override public void fromBytes(io.netty.buffer.ByteBuf buf) {
		}

	}

	<#if hasProcedure(data.onKeyPressed)>
	private static void pressAction(EntityPlayer entity) {
		World world = entity.world;
		int x = (int) entity.posX;
		int y = (int) entity.posY;
		int z = (int) entity.posZ;

		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;

		<@procedureOBJToCode data.onKeyPressed/>
	}
	</#if>

}
<#-- @formatter:on -->