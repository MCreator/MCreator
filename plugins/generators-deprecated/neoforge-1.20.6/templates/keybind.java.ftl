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
<#include "procedures.java.ftl">

package ${package}.network;

import ${package}.${JavaModName};

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${name}Message(int eventType, int pressedms) implements CustomPacketPayload {

	public static final Type<${name}Message> TYPE = new Type<>(new ResourceLocation(${JavaModName}.MODID, "key_${registryname}"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${name}Message> STREAM_CODEC = StreamCodec.of(
			(RegistryFriendlyByteBuf buffer, ${name}Message message) -> {
				buffer.writeInt(message.eventType);
				buffer.writeInt(message.pressedms);
			},
			(RegistryFriendlyByteBuf buffer) -> new ${name}Message(buffer.readInt(), buffer.readInt())
	);

	@Override public Type<${name}Message> type() {
		return TYPE;
	}

	public static void handleData(final ${name}Message message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> {
				<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
				pressAction(context.player(), message.eventType, message.pressedms);
				</#if>
			}).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();

		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
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

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${name}Message.TYPE, ${name}Message.STREAM_CODEC, ${name}Message::handleData);
	}

}
<#-- @formatter:on -->