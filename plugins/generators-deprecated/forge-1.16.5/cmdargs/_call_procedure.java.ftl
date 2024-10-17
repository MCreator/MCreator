<#include "procedures.java.ftl">
.executes(arguments -> {
	ServerWorld world = arguments.getSource().getWorld();

	double x = arguments.getSource().getPos().getX();
	double y = arguments.getSource().getPos().getY();
	double z = arguments.getSource().getPos().getZ();

	Entity entity = arguments.getSource().getEntity();
	if (entity == null)
		entity = FakePlayerFactory.getMinecraft(world);

	Direction direction = entity.getHorizontalFacing();

    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
})