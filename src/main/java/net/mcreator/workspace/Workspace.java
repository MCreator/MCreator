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

package net.mcreator.workspace;

import net.mcreator.Launcher;
import net.mcreator.element.ModElementType;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFlavor;
import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.io.FileIO;
import net.mcreator.ui.dialogs.workspace.GeneratorSelector;
import net.mcreator.vcs.WorkspaceVCS;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.ModElementManager;
import net.mcreator.workspace.elements.SoundElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.misc.WorkspaceInfo;
import net.mcreator.workspace.settings.WorkspaceSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Workspace implements Closeable, IGeneratorProvider {

	private static final Logger LOG = LogManager.getLogger("Workspace");

	private ConcurrentHashMap<ModElementType.BaseType, Integer> id_map = new ConcurrentHashMap<>();
	private Set<ModElement> mod_elements = Collections.synchronizedSet(new LinkedHashSet<>(0));
	private Set<VariableElement> variable_elements = Collections.synchronizedSet(new LinkedHashSet<>(0));
	private Set<SoundElement> sound_elements = Collections.synchronizedSet(new LinkedHashSet<>(0));
	private ConcurrentHashMap<String, ConcurrentHashMap<String, String>> language_map = new ConcurrentHashMap<String, ConcurrentHashMap<String, String>>() {{
		put("en_us", new ConcurrentHashMap<>());
	}};

	private WorkspaceSettings workspaceSettings;
	private long mcreatorVersion;

	// transient fields
	private transient boolean changed = false;
	transient WorkspaceFileManager fileManager;
	protected transient Generator generator;
	@Nullable private transient WorkspaceVCS vcs;
	private transient boolean regenerateRequired = false;
	private transient boolean failingGradleDependencies = false;

	@NotNull private final transient WorkspaceInfo workspaceInfo;

	private Workspace(WorkspaceSettings workspaceSettings) {
		this();
		this.workspaceSettings = workspaceSettings;
	}

	private Workspace() {
		this.workspaceInfo = new WorkspaceInfo(this);
	}

	public WorkspaceSettings getWorkspaceSettings() {
		return workspaceSettings;
	}

	public void setWorkspaceSettings(WorkspaceSettings workspaceSettings) {
		this.workspaceSettings = workspaceSettings;
		markDirty();
	}

	public Collection<ModElement> getModElements() {
		return mod_elements;
	}

	public Collection<VariableElement> getVariableElements() {
		return variable_elements;
	}

	public Collection<SoundElement> getSoundElements() {
		return sound_elements;
	}

	public Map<String, ConcurrentHashMap<String, String>> getLanguageMap() {
		return language_map;
	}

	public ConcurrentHashMap<ModElementType.BaseType, Integer> getIDMap() {
		return id_map;
	}

	@NotNull public WorkspaceInfo getWorkspaceInfo() {
		return workspaceInfo;
	}

	public ModElement getModElementByName(String elementName) {
		for (ModElement element : mod_elements)
			if (element.getName().equals(elementName))
				return element;
		return null;
	}

	public VariableElement getVariableElementByName(String elementName) {
		for (VariableElement element : variable_elements)
			if (element.getName().equals(elementName))
				return element;
		return null;
	}

	public void resetModElementCompilesStatus() {
		for (ModElement el : mod_elements)
			el.setCompiles(true);
		markDirty();
	}

	public void addLanguage(String language, ConcurrentHashMap<String, String> data) {
		language_map.putIfAbsent(language, data);
		markDirty();
	}

	public void updateLanguage(String language, ConcurrentHashMap<String, String> data) {
		language_map.put(language, data);
		markDirty();
	}

	public void setLocalization(String key, String value) {
		// we always update default localization
		language_map.get("en_us").put(key, value);

		// add localization to others if existing if there is not existing definition present
		for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : language_map.entrySet())
			entry.getValue().putIfAbsent(key, value);
		markDirty();
	}

	public void removeLocalizationEntryByKey(String key) {
		for (Map.Entry<String, ConcurrentHashMap<String, String>> entry : language_map.entrySet())
			entry.getValue().remove(key);

		markDirty();
	}

	public void removeLocalizationLanguage(String language) {
		if (language.equals("en_us"))
			return;
		language_map.remove(language);
		markDirty();
	}

	public void addModElement(ModElement element) {
		if (!mod_elements.contains(element)) { // only add this mod element if it is not already added
			element.reinit(); // if it is new element, it now probably has icons so we reinit modicons
			mod_elements.add(element);
			markDirty();
		} else
			updateModElement(element); // if exists, we store new version
	}

	public void addVariableElement(VariableElement element) {
		if (!variable_elements.contains(element)) {
			variable_elements.add(element);
			markDirty();
		}
	}

	public void addSoundElement(SoundElement element) {
		if (!sound_elements.contains(element)) {
			sound_elements.add(element);
			markDirty();
		}
	}

	public void updateModElement(ModElement element) {
		for (ModElement el : mod_elements) {
			if (el.getName().equals(element.getName())) {
				el.loadDataFrom(element);
				el.updateIcons();
			}
		}
		markDirty();
	}

	public void updateSoundElement(SoundElement originalElement, SoundElement updatedElement) {
		Set<SoundElement> tmp = new HashSet<>(sound_elements);
		for (SoundElement el : tmp) {
			if (el.getName().equals(originalElement.getName())) {
				sound_elements.remove(el);
				sound_elements.add(updatedElement);
			}
		}
		markDirty();
	}

	public void updateVariableElement(VariableElement originalElement, VariableElement updatedElement) {
		Set<VariableElement> tmp = new HashSet<>(variable_elements);
		for (VariableElement el : tmp) {
			if (el.getName().equals(originalElement.getName())) {
				variable_elements.remove(el);
				variable_elements.add(updatedElement);
			}
		}
		markDirty();
	}

	public void removeModElement(ModElement element) {
		if (mod_elements.contains(element)) {
			// first we ask generator to remove all related files
			try {
				if (generator != null)
					generator.removeElementFilesAndLangKeys(element);
			} catch (Exception e) {
				LOG.warn("Failed to remove element files for element " + element, e);
			}

			// after we don't need the definition anymore, remove actual files
			new File(fileManager.getFolderManager().getModElementsDir(), element.getName() + ".mod.json").delete();
			new File(fileManager.getFolderManager().getModElementPicturesCacheDir(), element.getName() + ".png")
					.delete();

			// finally remove element form the list
			mod_elements.remove(element);

			markDirty();
		}
	}

	public void removeVariableElement(VariableElement element) {
		variable_elements.remove(element);
		markDirty();
	}

	public void removeSoundElement(SoundElement element) {
		element.getFiles()
				.forEach(file -> new File(fileManager.getFolderManager().getSoundsDir(), file + ".ogg").delete());
		sound_elements.remove(element);
		markDirty();
	}

	public int getNextFreeIDAndIncrease(ModElementType.BaseType baseType) {
		if (id_map.get(baseType) == null) {
			id_map.put(baseType, 1);
			markDirty();
			return 1;
		} else {
			int free = id_map.get(baseType) + 1;
			id_map.put(baseType, free);
			markDirty();
			return free;
		}
	}

	public void setMCreatorVersion(long mcreatorVersion) {
		this.mcreatorVersion = mcreatorVersion;
		markDirty();
	}

	public long getMCreatorVersion() {
		return mcreatorVersion;
	}

	public WorkspaceFileManager getFileManager() {
		return fileManager;
	}

	public WorkspaceFolderManager getFolderManager() {
		return fileManager.getFolderManager();
	}

	public Generator getGenerator() {
		return generator;
	}

	public ModElementManager getModElementManager() {
		return fileManager.getModElementManager();
	}

	@Override public void close() {
		LOG.info("Closing workspace");

		generator.close();
		fileManager.close();
	}

	@Override public String toString() {
		return workspaceSettings.getModID();
	}

	void markDirty() {
		changed = true;
	}

	void markClean() {
		changed = false;
	}

	boolean isDirty() {
		return changed;
	}

	void reloadModElements() {
		for (ModElement modElement : mod_elements) {
			modElement.setWorkspace(this);
			modElement.reinit();
		}
	}

	public WorkspaceVCS getVCS() {
		return vcs;
	}

	public void setVCS(WorkspaceVCS vcs) {
		this.vcs = vcs;
	}

	public void switchGenerator(String generatorName) {
		this.getWorkspaceSettings().setCurrentGenerator(generatorName);

		this.generator.close();
		this.generator = new Generator(this); // reload generator
	}

	public void bindToNewWorkspaceFile(File workspaceFile) {
		this.fileManager.close(); // first close current workspace file
		this.fileManager = null; // reset reference
		this.fileManager = new WorkspaceFileManager(workspaceFile, this); // new file manager instance for the new file
	}

	public void markFailingGradleDependencies() {
		this.failingGradleDependencies = true;
		LOG.error("Detected failing Gradle dependencies. Will try to recover on next build.");
	}

	public boolean checkFailingGradleDependenciesAndClear() {
		boolean retval = failingGradleDependencies;
		if (retval)
			LOG.warn("Reported failing Gradle dependencies in the workspace");

		this.failingGradleDependencies = false;
		return retval;
	}

	@Override public @NotNull Workspace getWorkspace() {
		return this;
	}

	public static Workspace readFromFS(File workspaceFile, @Nullable Window ui)
			throws UnsupportedGeneratorException, CorruptedWorkspaceFileException, FileNotFoundException {
		if (workspaceFile.isFile()) {
			String workspace_string = FileIO.readFileToString(workspaceFile);
			Workspace retval;
			try {
				retval = WorkspaceFileManager.gson.fromJson(workspace_string, Workspace.class);
			} catch (Exception jse) {
				throw new CorruptedWorkspaceFileException(jse);
			}
			if (retval == null)
				throw new CorruptedWorkspaceFileException(new NullPointerException());
			retval.fileManager = new WorkspaceFileManager(workspaceFile, retval);

			if (Generator.GENERATOR_CACHE.get(retval.getWorkspaceSettings().getCurrentGenerator()) == null) {
				if (ui == null) {
					throw new UnsupportedGeneratorException(retval.getWorkspaceSettings().getCurrentGenerator());
				} else {
					String currentGenerator = retval.getWorkspaceSettings().getCurrentGenerator();
					GeneratorFlavor currentFlavor = GeneratorFlavor
							.valueOf(currentGenerator.split("-")[0].toUpperCase(Locale.ENGLISH));

					JOptionPane.showMessageDialog(ui,
							"<html><b>This workspace uses unsupported generator type: " + currentGenerator
									+ "</b><br>Select one of the supported generators for the workspace.",
							"Unsupported generator", JOptionPane.WARNING_MESSAGE);
					GeneratorConfiguration generatorConfiguration = GeneratorSelector.getGeneratorSelector(ui,
							GeneratorConfiguration.getRecommendedGeneratorForFlavor(Generator.GENERATOR_CACHE.values(),
									currentFlavor), currentFlavor);
					if (generatorConfiguration != null) {
						retval.getWorkspaceSettings().setCurrentGenerator(generatorConfiguration.getGeneratorName());

						retval.generator = new Generator(retval);
						retval.regenerateRequired = true;

						WorkspaceGeneratorSetup.cleanupGeneratorForSwitchTo(retval,
								Generator.GENERATOR_CACHE.get(retval.workspaceSettings.getCurrentGenerator()));

						WorkspaceGeneratorSetup.requestSetup(retval);
					} else {
						throw new UnsupportedGeneratorException(currentGenerator);
					}
				}
			} else {
				retval.generator = new Generator(retval);
				try {
					retval.generator.loadOrCreateGradleCaches();
				} catch (GradleCacheImportFailedException e) {
					LOG.warn("Failed to import caches when opening a workspace", e);
					// gradle is missing libs, rerun the setup to fix this
					WorkspaceGeneratorSetup.requestSetup(retval);
				}
			}

			retval.getWorkspaceSettings().setWorkspace(retval);

			List<ModElement> corruptedElements = new ArrayList<>();

			for (ModElement element : retval.getModElements()) {
				if (element.getName() == null || element.getType() == null) {
					corruptedElements.add(element);
				}
			}

			for (ModElement corrupted : corruptedElements) {
				retval.removeModElement(corrupted);
				LOG.warn("Detected corrupted mod element while deserializing. Element: " + corrupted);
			}

			retval.reloadModElements(); // reload mod element icons and register reference to this workspace for all of them
			LOG.info("Loaded workspace file " + workspaceFile);
			return retval;
		} else {
			throw new FileNotFoundException();
		}
	}

	public static Workspace createWorkspace(File workspaceFile, WorkspaceSettings workspaceSettings) {
		Workspace retval = new Workspace(workspaceSettings);
		workspaceFile.getParentFile().mkdirs();
		retval.setMCreatorVersion(Launcher.version.versionlong);
		retval.fileManager = new WorkspaceFileManager(workspaceFile, retval);
		retval.generator = new Generator(retval);
		retval.fileManager.saveWorkspaceDirectlyAndWait();
		retval.getWorkspaceSettings().setWorkspace(retval);
		LOG.info("Created new workspace with workspace file " + workspaceFile + ", modid: " + workspaceSettings
				.getModID());
		return retval;
	}

	public void reloadFromFS() {
		String workspace_string = FileIO.readFileToString(fileManager.getWorkspaceFile());
		Workspace workspace_on_fs = WorkspaceFileManager.gson.fromJson(workspace_string, Workspace.class);
		loadStoredDataFrom(workspace_on_fs);
		reloadModElements();
		LOG.info("Reloaded current workspace from the workspace file");
	}

	public void loadStoredDataFrom(Workspace other) {
		this.id_map = other.id_map;
		this.mod_elements = other.mod_elements;
		this.variable_elements = other.variable_elements;
		this.sound_elements = other.sound_elements;
		this.language_map = other.language_map;
		this.mcreatorVersion = other.mcreatorVersion;
		this.workspaceSettings = other.workspaceSettings;
		this.workspaceSettings.setWorkspace(this);
	}

	public boolean isRegenerateRequired() {
		return regenerateRequired;
	}

	public final static class VirtualWorkspace extends Workspace {

		public VirtualWorkspace(Workspace original, String workspace_string) throws IOException {
			super(null);
			Workspace retval = WorkspaceFileManager.gson.fromJson(workspace_string, Workspace.class);
			if (retval == null)
				throw new IOException("Failed to parse workspace string");
			this.loadStoredDataFrom(retval);
			this.generator = new Generator(this);
			this.generator.setGradleCache(this.generator.getGradleCache());
			this.fileManager = original.getFileManager();
			this.reloadModElements();
		}

	}

}
