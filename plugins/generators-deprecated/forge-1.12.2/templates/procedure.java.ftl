<#-- @formatter:off -->
package ${package}.procedure;

@Elements${JavaModName}.ModElement.Tag public class Procedure${name} extends Elements${JavaModName}.ModElement {

	public Procedure${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	public static void executeProcedure(java.util.HashMap<String, Object> dependencies){
		<#list dependencies as dependency>
		if(dependencies.get("${dependency.getName()}")==null){
			System.err.println("Failed to load dependency ${dependency.getName()} for procedure ${name}!");
			return;
		}
        </#list>

		<#list dependencies as dependency>
            ${dependency.getType(generator.getWorkspace())} ${dependency.getName()} =(${dependency.getType(generator.getWorkspace())})dependencies.get("${dependency.getName()}" );
        </#list>

		${procedurecode}

	}

	${trigger_code}

	<#if has_trigger>
	@Override public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}
	</#if>

}
<#-- @formatter:on -->
