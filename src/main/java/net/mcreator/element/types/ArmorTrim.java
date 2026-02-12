package net.mcreator.element.types;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.MItemBlock;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.references.ModElementReference;
import net.mcreator.workspace.references.TextureReference;

public class ArmorTrim extends GeneratableElement {

	public String name;
	@ModElementReference
	public MItemBlock item;
	@TextureReference(
			value = TextureType.ARMOR,
			files = {"%s_layer_1", "%s_layer_2"}
	)
	public String armorTextureFile;

	public ArmorTrim(ModElement element) {
		super(element);
	}

}
