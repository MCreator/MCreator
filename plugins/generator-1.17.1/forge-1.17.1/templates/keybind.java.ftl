<#--
 # MCreator (https://mcreator.net/)
 # Copyright (C) 2012-2020, Pylo
 # Copyright (C) 2020-2021, Pylo, opensource contributors
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

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD) public class ${name}Message {

	int type, pressedms;

	public ${name}Message(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public ${name}Message(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(${name}Message message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(${name}Message message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
				<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
				pressAction(context.getSender(), message.type, message.pressedms);
				</#if>
		});
		context.setPacketHandled(true);
	}

	<#if hasProcedure(data.onKeyPressed) || hasProcedure(data.onKeyReleased)>
	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level;
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
		${JavaModName}.addNetworkMessage(${name}Message.class, ${name}Message::buffer, ${name}Message::new, ${name}Message::handler);
	}

}
<#-- @formatter:on -->