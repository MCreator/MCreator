<#include "procedures.java.ftl">
.then(Commands.argument("arguments", StringArgumentType.greedyString()).executes(arguments -> {
    Level world = arguments.getSource().getUnsidedLevel();

    double x = arguments.getSource().getPosition().x();
    double y = arguments.getSource().getPosition().y();
    double z = arguments.getSource().getPosition().z();

    Entity entity = arguments.getSource().getEntity();
    if (entity == null && world instanceof ServerLevel _servLevel)
        entity = FakePlayerFactory.getMinecraft(_servLevel);

    Direction direction = Direction.DOWN;
    if (entity != null)
    	direction = entity.getDirection();

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
.executes(arguments -> {
    Level world = arguments.getSource().getUnsidedLevel();

    double x = arguments.getSource().getPosition().x();
    double y = arguments.getSource().getPosition().y();
    double z = arguments.getSource().getPosition().z();

    Entity entity = arguments.getSource().getEntity();
    if (entity == null && world instanceof ServerLevel _servLevel)
        entity = FakePlayerFactory.getMinecraft(_servLevel);

    Direction direction = Direction.DOWN;
    if (entity != null)
    	direction = entity.getDirection();

    HashMap<String, String> cmdparams = new HashMap<>();
    int index = -1;
    for (String param : arguments.getInput().split("\\s+")) {
        if (index >= 0)
            cmdparams.put(Integer.toString(index), param);
        index++;
    }

    <@procedureToCode name=procedure dependencies=dependencies/>
    return 0;
})