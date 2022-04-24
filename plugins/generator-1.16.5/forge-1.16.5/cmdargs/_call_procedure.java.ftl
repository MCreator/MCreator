<#include "procedures.java.ftl">
.executes(cmdargs -> {
	ServerWorld world = cmdargs.getSource().getWorld();

	double x = cmdargs.getSource().getPos().getX();
	double y = cmdargs.getSource().getPos().getY();
	double z = cmdargs.getSource().getPos().getZ();

	Entity entity = cmdargs.getSource().getEntity();
	Direction direction = Objects.requireNonNull(entity).getHorizontalFacing();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);
    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
})