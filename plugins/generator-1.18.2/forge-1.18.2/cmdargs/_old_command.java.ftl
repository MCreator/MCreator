<#include "procedures.java.ftl">
.then(Commands.argument("arguments", StringArgumentType.greedyString()).executes(arguments -> {
	ServerLevel world = arguments.getSource().getLevel();

	double x = arguments.getSource().getPosition().x();
    double y = arguments.getSource().getPosition().y();
    double z = arguments.getSource().getPosition().z();

	Entity entity = arguments.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getDirection();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);

	HashMap<String, String> cmdparams = new HashMap<>();
	int index = -1;
	for (String param : arguments.getInput().split("\\s+")) {
		if (index >= 0)
			cmdparams.put(Integer.toString(index), param);
		index++;
	}

    <@procedureOBJToCode field$procedure/>
    return 0;
}))