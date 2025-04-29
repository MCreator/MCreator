<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2023, Pylo, opensource contributors
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

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${JavaModName}MenustateUpdateMessage(int elementType, String name, Object elementState) implements CustomPacketPayload {

	public static final Type<${JavaModName}MenustateUpdateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "guistate_update"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${JavaModName}MenustateUpdateMessage> STREAM_CODEC = StreamCodec.of(${JavaModName}MenustateUpdateMessage::write, ${JavaModName}MenustateUpdateMessage::read);

    public static void write(FriendlyByteBuf buffer, ${JavaModName}MenustateUpdateMessage message) {
        int elementType = message.elementType;
        Object data = message.elementState;
        buffer.writeInt(elementType);
        buffer.writeUtf(message.name);
        if (elementType == 0) {
            buffer.writeUtf((String)data);
        }
        if (elementType == 1) {
            buffer.writeBoolean((boolean)data);
        }
    }

    public static ${JavaModName}MenustateUpdateMessage read(FriendlyByteBuf buffer) {
        int elementType = buffer.readInt();
        String name = buffer.readUtf();
        Object data = null;
        if (elementType == 0) {
            data = buffer.readUtf();
        }
        if (elementType == 1) {
            data = buffer.readBoolean();
        }
        return new ${JavaModName}MenustateUpdateMessage(elementType, name, data);
    }

	@Override public Type<${JavaModName}MenustateUpdateMessage> type() {
		return TYPE;
	}

	public static void handleData(final ${JavaModName}MenustateUpdateMessage message, final IPayloadContext context) {
	    context.enqueueWork(() -> {
        	Player entity = context.player();
        	//updateGuistate(entity, message.elementType, message.name, message.elementState);
        	//if (context.flow() == PacketFlow.CLIENTBOUND) {
        	//    ${JavaModName}Screens.onGuistateUpdate(message.elementType, message.name, message.elementState);
        	//}
        }).exceptionally(e -> {
        	context.connection().disconnect(Component.literal(e.getMessage()));
        	return null;
        });
	}

	/*public static void updateGuistate(Player entity, int elementType, String name, Object elementState) {
	    if (entity.containerMenu instanceof ${JavaModName}Menus.MenuAccessor menu) {
        	HashMap<String, Object> guistate = menu.getGuistate();
            if (elementType == 0) {
                guistate.put("textfield:" + name, elementState);
            } else if (elementType == 1) {
                guistate.put("checkbox:" + name, elementState);
            }
        }
	}*/

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${JavaModName}MenustateUpdateMessage.TYPE, ${JavaModName}MenustateUpdateMessage.STREAM_CODEC, ${JavaModName}MenustateUpdateMessage::handleData);
	}

}
<#-- @formatter:on -->