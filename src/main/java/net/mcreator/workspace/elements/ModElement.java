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
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.element.types.interfaces.IMCItemProvider;
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

	private final String name;
	private final String type;

	private Integer sortid = null;

	private boolean compiles = true;
	private boolean locked_code = false;

	@Nullable private String registry_name;

	@Nullable private Map<String, Object> metadata = null;

	@Nullable private String path;

	// MCItem representations of this element
	// it is transient, so it does not get serialized
	private transient List<MCItem> mcItems = null;

	// current mod icon if not obtained from mcitem - used for recipes
	// it is transient, so it does not get serialized
	private transient ImageIcon elementIcon;

	// Workspace this ModElement is in
	// it is transient, so it does not get serialized
	private transient Workspace workspace;

	public ModElement(@Nonnull Workspace workspace, @Nonnull String name, ModElementType<?> type) {
		this.name = name;
		this.type = type.getRegistryName();
		this.registry_name = RegistryNameFixer.fromCamelCase(name);

		reinit(workspace);
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

		if (mu.metadata != null) {
			this.metadata = new HashMap<>(mu.metadata);

			// remove files cache from metadata as otherwise on the first re-generation,
			// files from original mod element (mu) will be deleted
			this.metadata.remove("files");
		}

		reinit(workspace);
	}

	/**
	 * Warning: this method uses ModElementManager and is thus not thread safe
	 *
	 * @return GeneratableElement or null if load failed
	 */
	@Nullable public GeneratableElement getGeneratableElement() {
		return this.workspace.getModElementManager().loadGeneratableElement(this);
	}

	/**
	 * Call this method to reinit ME icon, mcItems cache or update associated workspace
	 *
	 * @param workspace Workspace this ME belongs to
	 */
	public void reinit(Workspace workspace) {
		this.workspace = workspace;

		if (type == null || this.getType() == ModElementType.UNKNOWN) {
			return;
		}

		// reload ME icon
		reloadElementIcon();

		// revalidate MCItems cache so it is reloaded on next request
		mcItems = null;
	}

	public void reloadElementIcon() {
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
	}

	public void setSortID(Integer sortid) {
		this.sortid = sortid;
	}

	public ImageIcon getElementIcon() {
		if (elementIcon != null && elementIcon.getImage() != null)
			return elementIcon;
		return null;
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

	public boolean isCodeLocked() {
		return locked_code;
	}

	public void setCodeLock(boolean codeLock) {
		if (this.getType() == ModElementType.CODE && !codeLock)
			return;
		this.locked_code = codeLock;
	}

	@Nonnull public Integer getSortID() {
		if (sortid == null) {
			this.sortid =
					workspace.getModElements().stream().filter(e -> e.sortid != null).mapToInt(e -> e.sortid).max()
							.orElse(0) + 1;
		}

		return sortid;
	}

	/**
	 * Warning: this method relies on getGeneratableElement that is not thread safe, so this method is also not thread safe
	 */
	@Nonnull public synchronized List<MCItem> getMCItems() {
		if (mcItems == null) {
			mcItems = this.getGeneratableElement() instanceof IMCItemProvider provider ?
					provider.providedMCItems() :
					Collections.emptyList();
		}

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
		if (parent == null || parent.isRoot()) {
			this.path = null;
		} else {
			// Make sure that the specified parent folder exists in the workspace
			if (workspace.getFoldersRoot().getRecursiveFolderChildren().stream().anyMatch(e -> e.equals(parent))) {
				this.path = parent.getPath();
			} else {
				this.path = null;
			}
		}
	}

	/**
	 * Warning: this method relies on getGeneratableElement that is not thread safe, so this method is also not thread safe
	 */
	public Collection<BaseType> getBaseTypesProvided() {
		return this.getGeneratableElement() instanceof ICommonType iCommonType ?
				iCommonType.getBaseTypesProvided() :
				Collections.emptyList();
	}

	/**
	 * @param other The mod element to copy settings from.
	 * @apiNote This method performs sensitive operations on this mod element. Avoid using it!
	 */
	@SuppressWarnings("unused") public void loadDataFrom(ModElement other) {
		this.compiles = other.compiles;
		this.locked_code = other.locked_code;
		this.sortid = other.sortid;
		this.registry_name = other.registry_name;
		this.metadata = other.metadata;
		this.mcItems = other.mcItems;
		this.elementIcon = other.elementIcon;
		this.workspace = other.workspace;
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
