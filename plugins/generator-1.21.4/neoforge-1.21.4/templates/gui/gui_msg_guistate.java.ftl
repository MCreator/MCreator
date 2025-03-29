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

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${name}GuistateUpdateMessage(int elementType, String name, String content) implements CustomPacketPayload {

	public static final Type<${name}GuistateUpdateMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "${registryname}_guistate"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${name}GuistateUpdateMessage> STREAM_CODEC = StreamCodec.of(
			(RegistryFriendlyByteBuf buffer, ${name}GuistateUpdateMessage message) -> {
			    buffer.writeInt(message.elementType);
				buffer.writeUtf(message.name);
				buffer.writeUtf(message.content);
			},
			(RegistryFriendlyByteBuf buffer) -> new ${name}GuistateUpdateMessage(buffer.readInt(), buffer.readUtf(), buffer.readUtf())
	);

	@Override public Type<${name}GuistateUpdateMessage> type() {
		return TYPE;
	}

	public static void handleData(final ${name}GuistateUpdateMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				if (entity.containerMenu instanceof ${name}Menu menu) {
				    HashMap<String, Object> guistate = menu.guistate;
				    int elementType = message.elementType;
                    if (elementType == 0) {
                    	guistate.put("text:" + message.name, message.content);
                    } else if (elementType == 1) {
                    	guistate.put("checkbox:" + message.name, message.content.equals("true") ? true : false);
                    }
				}
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${name}GuistateUpdateMessage.TYPE, ${name}GuistateUpdateMessage.STREAM_CODEC, ${name}GuistateUpdateMessage::handleData);
	}

}
<#-- @formatter:on -->