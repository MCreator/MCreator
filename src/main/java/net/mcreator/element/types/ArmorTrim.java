package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.io.FileIO;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.TextureReference;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.module.ModuleDescriptor;

@SuppressWarnings("unused") public class ArmorTrim extends GeneratableElement {

	private static final Logger LOG = LogManager.getLogger(ArmorTrim.class);

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
		try {
			boolean newPath = ModuleDescriptor.Version.parse(
							getModElement().getGeneratorConfiguration().getGeneratorMinecraftVersion())
					.compareTo(ModuleDescriptor.Version.parse("1.21.2")) >= 0;
			File armorDirectory = getModElement().getFolderManager().getTexturesFolder(TextureType.ARMOR);
			File texturesDirectory = getModElement().getFolderManager().getTexturesFolder(TextureType.OTHER);
			FileIO.copyFile(new File(armorDirectory, armorTextureFile + "_layer_1.png"), new File(texturesDirectory,
					newPath ?
							"trims/entity/humanoid/" + getModElement().getRegistryName() + ".png" :
							"trims/models/armor/" + getModElement().getRegistryName() + ".png"));
			FileIO.copyFile(new File(armorDirectory, armorTextureFile + "_layer_2.png"), new File(texturesDirectory,
					newPath ?
							"trims/entity/humanoid_leggings/" + getModElement().getRegistryName() + ".png" :
							"trims/models/armor/" + getModElement().getRegistryName() + "_leggings.png"));
		} catch (Exception e) {
			LOG.error("Failed to copy armor trim textures", e);
		}
	}

}
