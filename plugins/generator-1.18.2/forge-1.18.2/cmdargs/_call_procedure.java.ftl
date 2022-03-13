<#include "procedures.java.ftl">
.executes(cmdargs -> {
	ServerLevel world = cmdargs.getSource().getLevel();

	double x = cmdargs.getSource().getPosition().x();
    double y = cmdargs.getSource().getPosition().y();
    double z = cmdargs.getSource().getPosition().z();

	Entity entity = cmdargs.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getDirection();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);
    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
})