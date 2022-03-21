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

import com.google.gson.*;
import net.mcreator.element.*;
import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.minecraft.MCItem;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.workspace.IWorkspaceProvider;
import net.mcreator.workspace.Workspace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.*;

public class ModElement implements Serializable, IWorkspaceProvider, IGeneratorProvider, IElement {

	private String name;
	private String type;

	private Integer sortid = null;

	private boolean compiles = true;
	private boolean locked_code = false;

	@Nullable private String registry_name;

	@Nullable private Map<String, Object> metadata = null;

	@Nullable private String path;

	// MCItem representations of this element
	// it is transient so it does not get serialized
	private transient List<MCItem> mcItems = null;

	// current mod icon if not obtained from mcitem - used for recipes
	// it is transient so it does not get serialized
	private transient ImageIcon elementIcon;

	// Workspace this ModElement is in
	// it is transient so it does not get serialized
	private transient Workspace workspace;

	public ModElement(@Nonnull Workspace workspace, @Nonnull String name, ModElementType<?> type) {
		this.name = name;
		this.type = type.getRegistryName();
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
	public ModElement(@Nonnull Workspace workspace, @Nonnull ModElement mu, String duplicateName) {
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

		if (getType() == ModElementType.DIMENSION) {
			if (getMetadata("ep") != null && (Boolean) getMetadata("ep"))
				mcItems.add(new MCItem.Custom(this, null));
		} else if (getType().getRecipeType() == RecipeType.ITEM || getType().getRecipeType() == RecipeType.BLOCK) {
			mcItems.add(new MCItem.Custom(this, null));
		} else if (getType().getRecipeType() == RecipeType.BUCKET) {
			mcItems.add(new MCItem.Custom(this, null));
			if (getMetadata("gb") != null && (Boolean) getMetadata("gb"))
				mcItems.add(new MCItem.Custom(this, "bucket"));
		} else if (getType().getBaseType() == BaseType.ARMOR) {
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

	@Override public @Nonnull Workspace getWorkspace() {
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

	public boolean doesCompile() {
		return compiles;
	}

	public void setCompiles(boolean compiles) {
		this.compiles = compiles;
	}

	@Override public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ModElementType<?> getType() {
		try {
			return ModElementTypeLoader.getModElementType(type);
		} catch (Exception e) {
			return ModElementType.UNKNOWN;
		}
	}

	public String getTypeString() {
		return type == null ? null : type.toLowerCase(Locale.ENGLISH);
	}

	public void setType(ModElementType<?> type) {
		this.type = type.getRegistryName();
	}

	public boolean isCodeLocked() {
		return locked_code;
	}

	public void setCodeLock(boolean codeLock) {
		if (this.getType() == ModElementType.CODE && !codeLock)
			return;
		this.locked_code = codeLock;
	}

	public List<MCItem> getMCItems() {
		return mcItems;
	}

	public String getRegistryName() {
		if (registry_name == null)
			return RegistryNameFixer.fromCamelCase(this.name);
		else
			return registry_name;
	}

	public String getRegistryNameUpper() {
		if (registry_name == null)
			return RegistryNameFixer.fromCamelCase(this.name).toUpperCase(Locale.ENGLISH);
		else
			return registry_name.toUpperCase(Locale.ENGLISH);
	}

	public void setRegistryName(String registry_name) {
		this.registry_name = registry_name;
	}

	public @Nullable String getFolderPath() {
		return path;
	}

	public void setParentFolder(@Nullable FolderElement parent) {
		if (parent == null || parent.isRoot())
			this.path = null;
		else
			this.path = parent.getPath();
	}

	public static class ModElementDeserializer implements JsonDeserializer<ModElement> {

		private final Gson gson = new Gson();

		@Override
		public ModElement deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			JsonObject json = jsonElement.getAsJsonObject();

			String newType = json.get("type").getAsString();
			if (newType.equals("gun")) {
				newType = "rangeditem";
			} else if (newType.equals("mob")) {
				newType = "livingentity";
			}

			json.addProperty("type", newType);

			return gson.fromJson(json, ModElement.class);
		}
	}

}
