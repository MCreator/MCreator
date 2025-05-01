<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2025, Pylo, opensource contributors
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
<#include "../procedures.java.ftl">

package ${package}.network;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${JavaModName}MenuStateUpdateMessage(int elementType, String name, Object elementState) implements CustomPacketPayload {

	public static final Type<${JavaModName}MenuStateUpdateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "menustate_update"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${JavaModName}MenuStateUpdateMessage> STREAM_CODEC = StreamCodec.of(${JavaModName}MenuStateUpdateMessage::write, ${JavaModName}MenuStateUpdateMessage::read);

	public static void write(FriendlyByteBuf buffer, ${JavaModName}MenuStateUpdateMessage message) {
		int elementType = message.elementType;
		Object elementState = message.elementState;
		buffer.writeInt(elementType);
		buffer.writeUtf(message.name);
		if (elementType == 0) {
			buffer.writeUtf((String)elementState);
		} else if (elementType == 1) {
			buffer.writeBoolean((boolean)elementState);
		}
	}

	public static ${JavaModName}MenuStateUpdateMessage read(FriendlyByteBuf buffer) {
		int elementType = buffer.readInt();
		String name = buffer.readUtf();
		Object elementState = null;
		if (elementType == 0) {
			elementState = buffer.readUtf();
		} else if (elementType == 1) {
			elementState = buffer.readBoolean();
		}
		return new ${JavaModName}MenuStateUpdateMessage(elementType, name, elementState);
	}

	@Override public Type<${JavaModName}MenuStateUpdateMessage> type() {
		return TYPE;
	}

	public static void handleMenuState(final ${JavaModName}MenuStateUpdateMessage message, final IPayloadContext context) {
		context.enqueueWork(() -> {
			int elementType = message.elementType;
			String name = message.name;
			Object state = message.elementState;

			if (context.player().containerMenu instanceof ${JavaModName}Menus.MenuAccessor menu) {
				menu.getMenuState().put(elementType + ":" + name, state);
				if (context.flow() == PacketFlow.CLIENTBOUND && Minecraft.getInstance().screen instanceof ${JavaModName}Screens.ScreenAccessor accessor) {
					accessor.updateMenuState(elementType, name, state);
				}
			}
		}).exceptionally(e -> {
			context.connection().disconnect(Component.literal(e.getMessage()));
			return null;
		});
	}

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${JavaModName}MenuStateUpdateMessage.TYPE, ${JavaModName}MenuStateUpdateMessage.STREAM_CODEC, ${JavaModName}MenuStateUpdateMessage::handleMenuState);
	}

}
<#-- @formatter:on -->