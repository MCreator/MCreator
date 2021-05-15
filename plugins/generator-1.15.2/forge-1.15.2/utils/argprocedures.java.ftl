<#include "procedures.java.ftl">
<#-- @formatter:off -->
<#macro procedureCode procedurefield="">
    <#if procedurefield?has_content>
        <#assign procedures = generator.procedureNamesToObjects(procedurefield)>
        <#if hasProcedure(procedures[0])>
                <#if hasProcedure(procedures[0])>
                .executes(cmdargs -> {
		        	ServerWorld world = cmdargs.getSource().getWorld();

		        	double x = cmdargs.getSource().getPos().getX();
		        	double y = cmdargs.getSource().getPos().getY();
		        	double z = cmdargs.getSource().getPos().getZ();

		        	Entity entity = cmdargs.getSource().getEntity();
		        	Direction direction = Objects.requireNonNull(entity).getHorizontalFacing();
		        	if (entity == null)
		        		entity = FakePlayerFactory.getMinecraft(world);
                	<@procedureOBJToCode procedures[0]/>
                	return 1;
                })
                </#if>
        </#if>
    </#if>
</#macro>
<#-- @formatter:on -->