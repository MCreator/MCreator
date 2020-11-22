<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2020 Pylo and contributors
 # 
 # This program is free software: you can redistribute it and/or modify
 # it under the terms of the GNU General Public License as published by
 # the Free Software Foundation, either version 3 of the License, or
 # (at your option) any later version.
 # 
 # This program is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 # GNU General Public License for more details.
 # 
 # You should have received a copy of the GNU General Public License
 # along with this program.  If not, see <https://www.gnu.org/licenses/>.
 # 
 # Additional permission for code generator templates (*.ftl files)
 # 
 # As a special exception, you may create a larger work that contains part or 
 # all of the MCreator code generator templates (*.ftl files) and distribute 
 # that work under terms of your choice, so long as that work isn't itself a 
 # template for code generation. Alternatively, if you modify or redistribute 
 # the template itself, you may (at your option) remove this special exception, 
 # which will cause the template and the resulting code generator output files 
 # to be licensed under the GNU General Public License without this special 
 # exception.
-->

<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.keybind;

import ${package}.${JavaModName};

@${JavaModName}Elements.ModElement.Tag public class ${name}KeyBinding extends ${JavaModName}Elements.ModElement {

	@OnlyIn(Dist.CLIENT)
	private KeyBinding keys;

	<#if hasProcedure(data.onKeyReleased)>
	private long lastpress = 0;
	</#if>

	public ${name}KeyBinding (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		elements.addNetworkMessage(KeyBindingPressedMessage.class, KeyBindingPressedMessage::buffer, KeyBindingPressedMessage::new, KeyBindingPressedMessage::handler);
	}

	@Override @OnlyIn(Dist.CLIENT) public void initElements() {
		keys = new KeyBinding("key.mcreator.${registryname}", GLFW.GLFW_KEY_${generator.map(data.triggerKey, "keybuttons")},
				"key.categories.${data.keyBindingCategoryKey}");
		ClientRegistry.registerKeyBinding(keys);
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent @OnlyIn(Dist.CLIENT) public void onKeyInput(InputEvent.KeyInputEvent event) {
		<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
			if (Minecraft.getInstance().currentScreen == null) {
				if (event.getKey() == keys.getKey().getKeyCode()) {
					if(event.getAction() == GLFW.GLFW_PRESS) {
						<#if hasProcedure(data.onKeyPressed)>
							${JavaModName}.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage(0, 0));
							pressAction(Minecraft.getInstance().player, 0, 0);
						</#if>

						<#if hasProcedure(data.onKeyReleased)>
						lastpress = System.currentTimeMillis();
						</#if>
					}
					<#if hasProcedure(data.onKeyReleased)>
					else if (event.getAction() == GLFW.GLFW_RELEASE) {
						int dt = (int) (System.currentTimeMillis() - lastpress);
						${JavaModName}.PACKET_HANDLER.sendToServer(new KeyBindingPressedMessage(1, dt));
						pressAction(Minecraft.getInstance().player, 1, dt);
					}
					</#if>
				}
			}
    	</#if>
	}

	public static class KeyBindingPressedMessage {

		int type, pressedms;

		public KeyBindingPressedMessage(int type, int pressedms) {
			this.type = type;
			this.pressedms = pressedms;
		}

		public KeyBindingPressedMessage(PacketBuffer buffer) {
			this.type = buffer.readInt();
			this.pressedms = buffer.readInt();
		}

		public static void buffer(KeyBindingPressedMessage message, PacketBuffer buffer) {
			buffer.writeInt(message.type);
			buffer.writeInt(message.pressedms);
		}

		public static void handler(KeyBindingPressedMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
			NetworkEvent.Context context = contextSupplier.get();
			context.enqueueWork(() -> {
				<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
				pressAction(context.getSender(), message.type, message.pressedms);
				</#if>
    		});
    		context.setPacketHandled(true);
		}

	}

	<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
	private static void pressAction(PlayerEntity entity, int type, int pressedms) {
		World world = entity.world;
		double x = entity.getPosX();
		double y = entity.getPosY();
		double z = entity.getPosZ();

		// security measure to prevent arbitrary chunk generation
		if (!world.isBlockLoaded(new BlockPos(x, y, z)))
			return;

		<#if hasProcedure(data.onKeyPressed)>
		if(type == 0) {
			<@procedureOBJToCode data.onKeyPressed/>
		}
		</#if>

		<#if hasProcedure(data.onKeyReleased)>
		if(type == 1) {
			<@procedureOBJToCode data.onKeyReleased/>
		}
		</#if>
	}
	</#if>

}
<#-- @formatter:on -->