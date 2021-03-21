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

package ${package}.command;

@${JavaModName}Elements.ModElement.Tag
public class ${name}Command extends ${JavaModName}Elements.ModElement{

	public ${name}Command (${JavaModName}Elements instance) {
		super(instance, ${data.getModElement().getSortID()});

		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent public void registerCommands(RegisterCommandsEvent event) {
		event.getDispatcher().register(customCommand());
	}

	private LiteralArgumentBuilder<CommandSource> customCommand() {
        return LiteralArgumentBuilder.<CommandSource>literal("${data.commandName}")
			<#if data.permissionLevel != "No requirement">.requires(s -> s.hasPermissionLevel(${data.permissionLevel}))</#if>
			<#if !argscode??>
			.then(Commands.argument("arguments", StringArgumentType.greedyString())
            .executes(this::execute)
        	)
            .executes(this::execute)
            <#else>
            ${argscode}
            </#if>
			;
    }

    private int execute(CommandContext<CommandSource> cmdargs) {
		<#if hasProcedure(data.onCommandExecuted)>
		ServerWorld world = cmdargs.getSource().getWorld();

		double x = cmdargs.getSource().getPos().getX();
		double y = cmdargs.getSource().getPos().getY();
		double z = cmdargs.getSource().getPos().getZ();

		Entity entity = cmdargs.getSource().getEntity();
		if (entity == null)
			entity = FakePlayerFactory.getMinecraft(world);

		<#if !argscode??>
		HashMap<String, String> cmdparams = new HashMap<>();
		int[] index = { -1 };
		Arrays.stream(cmdargs.getInput().split("\\s+")).forEach(param -> {
			if(index[0] >= 0)
				cmdparams.put(Integer.toString(index[0]), param);
			index[0]++;
		});
		</#if>

		<@procedureOBJToCode data.onCommandExecuted/>
		</#if>

		return 0;
	}

}
<#-- @formatter:on -->