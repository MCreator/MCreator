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

package ${package}.command;

@Mod.EventBusSubscriber public class ${name}Command {

	@SubscribeEvent public static void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(Commands.literal("${data.commandName}")
			<#if data.permissionLevel != "No requirement">.requires(s -> s.hasPermission(${data.permissionLevel}))</#if>
			.then(Commands.argument("arguments", StringArgumentType.greedyString())
				<#if hasProcedure(data.onCommandExecuted)>
            	.executes(${name}Command::execute)
				</#if>
			)
			<#if hasProcedure(data.onCommandExecuted)>
            .executes(${name}Command::execute)
			</#if>
		);
	}

    private static int execute(CommandContext<CommandSourceStack> ctx) {
		ServerLevel world = ctx.getSource().getLevel();

		double x = ctx.getSource().getPosition().x();
		double y = ctx.getSource().getPosition().y();
		double z = ctx.getSource().getPosition().z();

		Entity entity = ctx.getSource().getEntity();
		if (entity == null)
			entity = FakePlayerFactory.getMinecraft(world);

		HashMap<String, String> cmdparams = new HashMap<>();
		int[] index = { -1 };
		Arrays.stream(ctx.getInput().split("\\s+")).forEach(param -> {
			if(index[0] >= 0)
				cmdparams.put(Integer.toString(index[0]), param);
			index[0]++;
		});

		<@procedureOBJToCode data.onCommandExecuted/>

		return 0;
	}

}
<#-- @formatter:on -->