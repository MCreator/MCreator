/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.workspace.elements;

import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.Serializable;
import java.util.*;

public class ModElement implements Serializable, IWorkspaceProvider, IGeneratorProvider {

	private String name;
	private ModElementType type;

	private Integer sortid = null;

	private boolean compiles = true;
	private boolean locked_code = false;

	private Map<Integer, Integer> ids = new HashMap<>();
	private String registry_name = null;

	private Map<String, Object> metadata = null;

	// MCItem representations of this element
	// it is transient so it does not get serialized
	private transient List<MCItem> mcItems = null;

	// current mod icon if not obtained from mcitem - used for recipes
	// it is transient so it does not get serialized
	private transient ImageIcon elementIcon;

	// Workspace this ModElement is in
	// it is transient so it does not get serialized
	private transient Workspace workspace;

	public ModElement(@NotNull Workspace workspace, @NotNull String name, ModElementType type) {
		this.name = name;
		this.type = type;
		this.registry_name = RegistryNameFixer.fromCamelCase(name);

		setWorkspace(workspace);

		reinit();
	}

	/**
	 * Duplicates given mod element, but does not duplicate registry name, lock status, compiles status and ID map
	 *
	 * @param mu            Mod element to be duplicated
	 * @param duplicateName Name of the duplicate
	 */
	public ModElement(@NotNull Workspace workspace, @NotNull ModElement mu, String duplicateName) {
		this.name = duplicateName;
		this.type = mu.type;
		this.registry_name = RegistryNameFixer.fromCamelCase(name);

		if (mu.metadata != null)
			this.metadata = new HashMap<>(mu.metadata);

		setWorkspace(workspace);

		reinit();
	}

	@Nullable public GeneratableElement getGeneratableElement() {
		return this.workspace.getModElementManager().loadGeneratableElement(this);
	}

	public void loadDataFrom(ModElement other) {
		this.type = other.type;
		this.compiles = other.compiles;
		this.locked_code = other.locked_code;
		this.sortid = other.sortid;
		this.ids = other.ids;
		this.registry_name = other.registry_name;
		this.metadata = other.metadata;
		this.mcItems = other.mcItems;
		this.elementIcon = other.elementIcon;
		this.workspace = other.workspace;
	}

	public void reinit() {
		if (type == null)
			return;

		reloadElementIcon();

		mcItems = new ArrayList<>();

		if (type == ModElementType.DIMENSION) {
			if (getMetadata("ep") != null && (Boolean) getMetadata("ep"))
				mcItems.add(new MCItem.Custom(this, null));
		} else if (type.getRecipeElementType() == ModElementType.RecipeElementType.ITEM
				|| type.getRecipeElementType() == ModElementType.RecipeElementType.BLOCK) {
			mcItems.add(new MCItem.Custom(this, null));
		} else if (type.getBaseType() == ModElementType.BaseType.ARMOR) {
			if (getMetadata("eh") != null && (Boolean) getMetadata("eh"))
				mcItems.add(new MCItem.Custom(this, "helmet"));
			if (getMetadata("ec") != null && (Boolean) getMetadata("ec"))
				mcItems.add(new MCItem.Custom(this, "body"));
			if (getMetadata("el") != null && (Boolean) getMetadata("el"))
				mcItems.add(new MCItem.Custom(this, "legs"));
			if (getMetadata("eb") != null && (Boolean) getMetadata("eb"))
				mcItems.add(new MCItem.Custom(this, "boots"));
		}
	}

	private void reloadElementIcon() {
		if (elementIcon != null && elementIcon.getImage() != null)
			elementIcon.getImage().flush();

		elementIcon = new ImageIcon(
				workspace.getFolderManager().getModElementPicturesCacheDir().getAbsolutePath() + "/" + name + ".png");
	}

	@Override public @NotNull Workspace getWorkspace() {
		return workspace;
	}

	public void setWorkspace(Workspace workspace) {
		this.workspace = workspace;

		// if this mod element does not have ID inside the workspace yet, define it now
		if (sortid == null)
			this.sortid =
					workspace.getModElements().stream().filter(e -> e.sortid != null).mapToInt(e -> e.sortid).max()
							.orElse(0) + 1;
	}

	public Integer getSortID() {
		return sortid;
	}

	public void setSortID(Integer sortid) {
		this.sortid = sortid;
	}

	public ImageIcon getElementIcon() {
		if (elementIcon != null && elementIcon.getImage() != null)
			return elementIcon;
		return null;
	}

	public void updateIcons() {
		// flush all the buffers of the contained icons in case if the icons changed
		mcItems.forEach(mcItem -> mcItem.icon.getImage().flush());

		reloadElementIcon();
	}

	public ModElement putMetadata(String key, Object data) {
		if (metadata == null)
			metadata = new HashMap<>();
		metadata.put(key, data);

		return this;
	}

	public ModElement clearMetadata() {
		if (metadata != null)
			metadata = new HashMap<>();

		return this;
	}

	public Object getMetadata(String key) {
		if (metadata == null)
			return null;
		return metadata.get(key);
	}

	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object element) {
		return element instanceof ModElement && name.equals(((ModElement) element).getName());
	}

	@Override public int hashCode() {
		return name.hashCode();
	}

	public void setIDAt(int idindex, int id) {
		this.ids.put(idindex, id);
	}

	public Map<Integer, Integer> getIDMap() {
		return ids;
	}

	/**
	 * Get id for given index or get one for base type of this mod element
	 *
	 * @param index The ID index
	 * @return The ID of the element for the given index, could be newly created
	 */
	public int getID(int index) {
		return getID(index, type.getBaseType());
	}

	/**
	 * Get id for given index or get one for base type provided
	 *
	 * @param index    The ID index
	 * @param baseType The base type under which to look for the free IDs
	 * @return The ID of the element for the given index, could be newly created
	 */
	public int getID(int index, ModElementType.BaseType baseType) {
		if (ids.get(index) == null) { // id at this index is not set yet, create id
			int free_id = workspace.getNextFreeIDAndIncrease(baseType);
			ids.put(index, free_id);
			return free_id;
		}
		return ids.get(index);
	}

	/**
	 * Get id for given index or get one for base type string provided
	 *
	 * @param index    The ID index
	 * @param baseType The base type under which to look for the free IDs
	 * @return The ID of the element for the given index, could be newly created
	 */
	@SuppressWarnings("unused") public int getID(int index, String baseType) {
		if (ids.get(index) == null) { // id at this index is not set yet, create id
			int free_id = workspace
					.getNextFreeIDAndIncrease(ModElementType.BaseType.valueOf(baseType.toUpperCase(Locale.ENGLISH)));
			ids.put(index, free_id);
			return free_id;
		}
		return ids.get(index);
	}

	public boolean doesCompile() {
		return compiles;
	}

	public void setCompiles(boolean compiles) {
		this.compiles = compiles;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ModElementType getType() {
		return type;
	}

	public void setType(ModElementType type) {
		this.type = type;
	}

	public boolean isCodeLocked() {
		return locked_code;
	}

	public void setCodeLock(boolean codeLock) {
		if (this.type == ModElementType.CODE && !codeLock)
			return;
		this.locked_code = codeLock;
	}

	public List<MCItem> getMCItems() {
		return mcItems;
	}

	public String getRegistryName() {
		if (registry_name == null)
			return getName().toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9/._-]+", "");
		else
			return registry_name;
	}

	public void setRegistryName(String registry_name) {
		this.registry_name = registry_name;
	}

}
