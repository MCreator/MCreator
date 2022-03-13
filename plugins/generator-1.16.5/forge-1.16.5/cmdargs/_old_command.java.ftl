<#include "procedures.java.ftl">
.then(Commands.argument("arguments", StringArgumentType.greedyString()).executes(cmdargs -> {
	ServerWorld world = cmdargs.getSource().getWorld();

	double x = cmdargs.getSource().getPos().getX();
	double y = cmdargs.getSource().getPos().getY();
	double z = cmdargs.getSource().getPos().getZ();

	Entity entity = cmdargs.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getHorizontalFacing();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);

	HashMap<String, String> cmdparams = new HashMap<>();
	int[] index = { -1 };
	Arrays.stream(cmdargs.getInput().split("\\s+")).forEach(param -> {
		if(index[0] >= 0)
			cmdparams.put(Integer.toString(index[0]), param);
		index[0]++;
	});
    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
}))