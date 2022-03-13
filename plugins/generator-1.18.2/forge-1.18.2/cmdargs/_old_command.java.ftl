<#include "procedures.java.ftl">
.then(Commands.argument("arguments", StringArgumentType.greedyString()).executes(cmdargs -> {
	ServerLevel world = cmdargs.getSource().getLevel();

	double x = cmdargs.getSource().getPosition().x();
    double y = cmdargs.getSource().getPosition().y();
    double z = cmdargs.getSource().getPosition().z();

	Entity entity = cmdargs.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getDirection();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);

	HashMap<String, String> cmdparams = new HashMap<>();
    int[] index = { -1 };
    Arrays.stream(cmdargs.getInput().split("\\s+")).forEach(param -> {
        if(index[0] >= 0)
    		cmdparams.put(Integer.toString(index[0]), param);
    	index[0]++;
    });

    <@procedureOBJToCode field$procedure/>
    return 0;
}))