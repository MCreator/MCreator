<#include "procedures.java.ftl">
.then(Commands.argument("arguments", StringArgumentType.greedyString()).executes(arguments -> {
	ServerWorld world = arguments.getSource().getWorld();

	double x = arguments.getSource().getPos().getX();
	double y = arguments.getSource().getPos().getY();
	double z = arguments.getSource().getPos().getZ();

	Entity entity = arguments.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getHorizontalFacing();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);

	HashMap<String, String> cmdparams = new HashMap<>();
	int index = -1;
	for (String param : arguments.getInput().split("\\s+")) {
		if (index >= 0)
			cmdparams.put(Integer.toString(index), param);
		index++;
	}
    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
}))