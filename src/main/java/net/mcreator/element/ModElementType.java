/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.mcreator.element;

import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ModElementGUI;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ModElementType<GE extends GeneratableElement> {

	private final String registryName;

	private final ModElementGUIProvider<GE> modElementGUIProvider;
	private final Class<? extends GE> modElementStorageClass;

	private final String readableName;
	private final String description;
	@Nullable private final Character shortcut;

	@Nullable private List<GeneratorFlavor> coveredFlavors;

	public ModElementType(String registryName, @Nullable Character shortcut,
			ModElementGUIProvider<GE> modElementGUIProvider, Class<? extends GE> modElementStorageClass) {
		this.registryName = registryName;
		this.shortcut = shortcut;

		this.modElementGUIProvider = modElementGUIProvider;
		this.modElementStorageClass = modElementStorageClass;

		this.readableName = L10N.t("modelement." + registryName.toLowerCase(Locale.ENGLISH));
		this.description = L10N.t("modelement." + registryName.toLowerCase(Locale.ENGLISH) + ".description");
	}

	public String getRegistryName() {
		return registryName;
	}

	public String getPluralName() {
		if (this == LIVINGENTITY)
			return "livingentities";

		return registryName.toLowerCase(Locale.ENGLISH) + "s";
	}

	@Nullable public Character getShortcut() {
		return shortcut;
	}

	public String getReadableName() {
		return readableName;
	}

	public String getDescription() {
		return description;
	}

	public ImageIcon getIcon() {
		return UIRES.get("mod_types." + registryName);
	}

	public ModElementGUI<GE> getModElementGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		return modElementGUIProvider.get(mcreator, modElement, editingMode);
	}

	public Class<? extends GE> getModElementStorageClass() {
		return modElementStorageClass;
	}

	public ModElementType<GE> coveredOn(GeneratorFlavor... supportedFlavors) {
		this.coveredFlavors = List.of(supportedFlavors);
		return this;
	}

	/**
	 * This method returns the list of flavors that this element type is covered on.
	 * Covered means that this MET is expected to be supported by the returned flavors.
	 * <p>
	 * Generator definitions of flavors not returned here can still support this MET
	 * by defining the MET definition.
	 * <p>
	 * The use of this return value is for generator selector to show red cross on
	 * METs that certain flavor should support, but doesn't.
	 *
	 * @return List of covered flavors, if not set, all flavors are covered
	 */
	public List<GeneratorFlavor> getCoveredFlavors() {
		return coveredFlavors != null ?
				coveredFlavors :
				Arrays.stream(GeneratorFlavor.values()).filter(e -> !GeneratorFlavor.SPECIAL_FLAVORS.contains(e))
						.toList();
	}

	@Override public String toString() {
		return this.registryName;
	}

	@Override public boolean equals(Object element) {
		return element instanceof ModElementType && registryName.equals(
				((ModElementType<?>) element).getRegistryName());
	}

	@Override public int hashCode() {
		return registryName.hashCode();
	}

	public interface ModElementGUIProvider<GE extends GeneratableElement> {
		ModElementGUI<GE> get(MCreator mcreator, ModElement modElement, boolean editingMode);
	}

	public static ModElementType<?> ADVANCEMENT;
	public static ModElementType<?> ARMOR;
	public static ModElementType<?> ATTRIBUTE;
	public static ModElementType<?> BIOME;
	public static ModElementType<?> BLOCK;
	public static ModElementType<?> COMMAND;
	public static ModElementType<?> DAMAGETYPE;
	public static ModElementType<?> DIMENSION;
	public static ModElementType<?> CODE;
	public static ModElementType<?> ENCHANTMENT;
	public static ModElementType<?> FEATURE;
	public static ModElementType<?> FLUID;
	public static ModElementType<?> FUNCTION;
	public static ModElementType<?> GAMERULE;
	public static ModElementType<?> GUI;
	public static ModElementType<?> ITEM;
	public static ModElementType<?> ITEMEXTENSION;
	public static ModElementType<?> KEYBIND;
	public static ModElementType<?> LIVINGENTITY;
	public static ModElementType<?> LOOTTABLE;
	public static ModElementType<?> OVERLAY;
	public static ModElementType<?> PAINTING;
	public static ModElementType<?> PARTICLE;
	public static ModElementType<?> PLANT;
	public static ModElementType<?> POTION;
	public static ModElementType<?> POTIONEFFECT;
	public static ModElementType<?> PROCEDURE;
	public static ModElementType<?> PROJECTILE;
	public static ModElementType<?> RECIPE;
	public static ModElementType<?> STRUCTURE;
	public static ModElementType<?> TAB;
	public static ModElementType<?> TOOL;
	public static ModElementType<?> VILLAGERPROFESSION;
	public static ModElementType<?> VILLAGERTRADE;

	public static ModElementType<?> UNKNOWN;

}
