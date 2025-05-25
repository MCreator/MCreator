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

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD) public record ${name}SliderMessage(int buttonID, int x, int y, int z, double value) implements CustomPacketPayload {

	public static final Type<${name}SliderMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(${JavaModName}.MODID, "${registryname}_sliders"));

	public static final StreamCodec<RegistryFriendlyByteBuf, ${name}SliderMessage> STREAM_CODEC = StreamCodec.of(
			(RegistryFriendlyByteBuf buffer, ${name}SliderMessage message) -> {
				buffer.writeInt(message.buttonID);
				buffer.writeInt(message.x);
				buffer.writeInt(message.y);
				buffer.writeInt(message.z);
				buffer.writeDouble(message.value);
			},
			(RegistryFriendlyByteBuf buffer) -> new ${name}SliderMessage(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readDouble())
	);

	@Override public Type<${name}SliderMessage> type() {
		return TYPE;
	}

	public static void handleData(final ${name}SliderMessage message, final IPayloadContext context) {
		if (context.flow() == PacketFlow.SERVERBOUND) {
			context.enqueueWork(() -> handleSliderAction(context.player(), message.buttonID, message.x, message.y, message.z, message.value)).exceptionally(e -> {
				context.connection().disconnect(Component.literal(e.getMessage()));
				return null;
			});
		}
	}

	public static void handleSliderAction(Player entity, int buttonID, int x, int y, int z, double value) {
		Level world = entity.level();

		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(new BlockPos(x, y, z)))
			return;

		<#assign btid = 0>
		<#list data.getComponentsOfType("Slider") as component>
				<#if hasProcedure(component.whenSliderMoves)>
					if (buttonID == ${btid}) {
						<@procedureOBJToCode component.whenSliderMoves/>
					}
				</#if>
				<#assign btid +=1>
		</#list>
	}

	@SubscribeEvent public static void registerMessage(FMLCommonSetupEvent event) {
		${JavaModName}.addNetworkMessage(${name}SliderMessage.TYPE, ${name}SliderMessage.STREAM_CODEC, ${name}SliderMessage::handleData);
	}

}
<#-- @formatter:on -->