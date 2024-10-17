<#-- @formatter:off -->
/**
 * This mod element is always locked. Enter your code in the methods below.
 * If you don't need some of these methods, you can remove them as they
 * are overrides of the base class Elements${JavaModName}.ModElement.
 *
 * You can register new events in this class too.
 *
 * As this class is loaded into mod element list, it NEEDS to extend
 * ModElement class. If you remove this extend statement or remove the
 * constructor, the compilation will fail.
 *
 * If you want to make a plain independent class, create it in
 * "Workspace" -> "Source" menu.
 *
 * If you change workspace package, modid or prefix, you will need
 * to manually adapt this file to these changes or remake it.
*/

package ${package};

@Elements${JavaModName}.ModElement.Tag public class ${name} extends Elements${JavaModName}.ModElement{

	/**
	 * Do not remove this constructor
	 */
	public ${name} (Elements${JavaModName} instance) {
		super(instance, ${data.getModElement().getSortID()});
	}

	@Override public void initElements() {
	}

	@Override public void init(FMLInitializationEvent event) {
	}

	@Override public void preInit(FMLPreInitializationEvent event) {
	}

	@Override public void generateWorld(Random random, int posX, int posZ, World world, int dimID, IChunkGenerator cg, IChunkProvider cp) {
	}

	@Override public void serverLoad(FMLServerStartingEvent event) {
	}

	@Override public void registerModels(ModelRegistryEvent event) {
	}

}
<#-- @formatter:on -->