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

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${name}ButtonMessage(int buttonID, int x, int y, int z) implements CustomPacketPayload {

	public static final Type<${name}ButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "${registryname}_buttons"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${name}ButtonMessage> STREAM_CODEC = StreamCodec.of(
			(RegistryFriendlyByteBuf buffer, ${name}ButtonMessage message) -> {
				buffer.writeInt(message.buttonID);
				buffer.writeInt(message.x);
				buffer.writeInt(message.y);
				buffer.writeInt(message.z);
			},
			(RegistryFriendlyByteBuf buffer) -> new ${name}ButtonMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt())
	);

	@Override public Type<${name}ButtonMessage> type() {
		return TYPE;
	}

	public static void handleData(final ${name}ButtonMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				Player entity = context.player();
				int buttonID = message.buttonID;
				int x = message.x;
				int y = message.y;
				int z = message.z;

				handleButtonAction(entity, buttonID, x, y, z);
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleButtonAction(Player entity, int buttonID, int x, int y, int z) {
		Level world = entity.level();
		HashMap<String, Object> guistate = new HashMap<>();
        if (entity.containerMenu instanceof ${name}Menu menu) {
            guistate = menu.guistate;
        }

		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;

		<#assign btid = 0>
		<#list data.getComponentsOfType("Button") as component>
				<#if hasProcedure(component.onClick)>
					if (buttonID == ${btid}) {
						<@procedureOBJToCode component.onClick/>
					}
				</#if>
				<#assign btid +=1>
		</#list>
		<#list data.getComponentsOfType("ImageButton") as component>
				<#if hasProcedure(component.onClick)>
					if (buttonID == ${btid}) {
						<@procedureOBJToCode component.onClick/>
					}
				</#if>
				<#assign btid +=1>
		</#list>
	}

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${name}ButtonMessage.TYPE, ${name}ButtonMessage.STREAM_CODEC, ${name}ButtonMessage::handleData);
	}

}
<#-- @formatter:on -->