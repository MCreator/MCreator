<#-- @formatter:off -->
<#include "procedures.java.ftl">

package ${package}.command;

@Elements${JavaModName}.ModElement.Tag public class Command${name} extends Elements${JavaModName}.ModElement{

	public Command${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void serverLoad(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandHandler());
	}

	public static class CommandHandler implements ICommand {

		@Override public int compareTo(ICommand c) {
			return getName().compareTo(c.getName());
		}

		@Override public boolean checkPermission(MinecraftServer server, ICommandSender var1) {
			return true;
		}

		@Override public List getAliases() {
			return new ArrayList();
		}

		@Override
		public List getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
			return new ArrayList();
		}

		@Override public boolean isUsernameIndex(String[] string, int index) {
			return true;
		}

		@Override public String getName() {
			return "${data.commandName}";
		}

		@Override public String getUsage(ICommandSender var1) {
			return "/${data.commandName} [<arguments>]";
		}

		@Override public void execute(MinecraftServer server, ICommandSender sender, String[] cmd) {
			<#if hasProcedure(data.onCommandExecuted)>
				int x = sender.getPosition().getX();
				int y = sender.getPosition().getY();
				int z = sender.getPosition().getZ();
				Entity entity = sender.getCommandSenderEntity();
				if (entity != null) {
					World world = entity.world;

					HashMap<String, String> cmdparams = new HashMap<>();
					int[] index = { 0 };
					Arrays.stream(cmd).forEach(param -> {
						cmdparams.put(Integer.toString(index[0]), param);
						index[0]++;
					});

                    <@procedureOBJToCode data.onCommandExecuted/>
				}
            </#if>
		}

	}

}
<#-- @formatter:on -->