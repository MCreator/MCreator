package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;

import java.io.File;
import java.lang.module.ModuleDescriptor;

@SuppressWarnings("unused") public class ArmorTrim extends GeneratableElement {

	public String name;
	public MItemBlock item;
	@TextureReference(value = TextureType.ARMOR, files = { "%s_layer_1", "%s_layer_2" }) public String armorTextureFile;

	private ArmorTrim() {
		this(null);
	}

	public ArmorTrim(ModElement element) {
		super(element);
	}

	@Override public void finalizeModElementGeneration() {
		boolean newPath =  ModuleDescriptor.Version.parse(getModElement().getGeneratorConfiguration().getGeneratorMinecraftVersion())
				.compareTo(ModuleDescriptor.Version.parse("1.21.2")) >= 0;
		Workspace workspace = getModElement().getWorkspace();
		FileIO.copyFile(new File(GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "mod_assets_root"),
						"textures/models/armor/" + armorTextureFile + "_layer_1.png"),
				new File(GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "mod_assets_root"),
						newPath ? "textures/trims/entity/humanoid/" + getModElement().getRegistryName() + ".png"
								: "textures/trims/models/armor/" + getModElement().getRegistryName() + ".png"));
		FileIO.copyFile(new File(GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "mod_assets_root"),
						"textures/models/armor/" + armorTextureFile + "_layer_2.png"),
				new File(GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "mod_assets_root"),
						newPath ? "textures/trims/entity/humanoid_leggings/" + getModElement().getRegistryName() + ".png"
								: "textures/trims/models/armor/" + getModElement().getRegistryName() + "_leggings.png"));
	}

}
