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

import net.mcreator.generator.GeneratorUtils;
import net.mcreator.io.FileIO;
import net.mcreator.io.OS;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WorkspaceFolderManager {

	private static final Logger LOG = LogManager.getLogger("Workspace Folder Manager");

	private final File workspaceFolder;
	private final Workspace workspace;

	public WorkspaceFolderManager(@Nonnull File workspaceFile, @Nonnull Workspace workspace) {
		this.workspaceFolder = workspaceFile.getParentFile();
		this.workspace = workspace;
	}

	private static List<File> listPNGsInDir(@Nullable File dir) {
		if (dir == null)
			return Collections.emptyList();

		List<File> retval = new ArrayList<>();
		File[] block = dir.listFiles();
		for (File element : block != null ? block : new File[0])
			if (element.getName().endsWith(".png"))
				retval.add(element);
		return retval;
	}

	public static File getSuggestedWorkspaceFoldersRoot() {
		File workspacesFolder = new File(System.getProperty("user.home"), "MCreatorWorkspaces");
		if (!workspacesFolder.getAbsolutePath().matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
			if (OS.getOS() == OS.WINDOWS)
				workspacesFolder = new File("C:/", "MCreatorWorkspaces");
		}
		return workspacesFolder;
	}

	public File getWorkspaceFolder() {
		return workspaceFolder;
	}

	/**
	 * Checks if the provided file is contained in the workspace folder.
	 *
	 * @param file The file to check.
	 * @return True if the provided file is inside the workspace folder.
	 */
	public boolean isFileInWorkspace(File file) {
		return isFileInWorkspace(file, false);
	}

	/**
	 * Checks if the provided file is contained in the workspace folder.
	 *
	 * @param file   The file to check.
	 * @param silent Determines whether non-presence of input file in the workspace should be logged or not.
	 * @return True if the provided file is inside the workspace folder.
	 */
	public boolean isFileInWorkspace(File file, boolean silent) {
		if (FileIO.isFileSomewhereInDirectory(file, workspaceFolder)) {
			return true;
		} else {
			if (!silent)
				LOG.warn(file.getAbsolutePath() + " is not in workspace path!");
			return false;
		}
	}

	/**
	 * Attempts to locate the provided file inside workspace folder and return its path relative to this folder.
	 *
	 * @param file The input file.
	 * @return File path relative to workspace folder or its absolute path if not found in the workspace.
	 */
	public String getPathInWorkspace(File file) {
		if (!isFileInWorkspace(file, true))
			throw new RuntimeException(file.getAbsolutePath() + " is not in workspace path!");

		return workspaceFolder.toPath().relativize(file.toPath()).toString();
	}

	/**
	 * <p>This method gets an image depending on the desired type.</p>
	 *
	 * @param textureIdentifier <p>This is the name without the file extension of the texture file.</p>
	 * @param section           <p>This {@link TextureType} defines which path, defined by each generator, MCreator will search the texture file.</p>
	 * @return <p>The texture file as an {@link ImageIcon}.</p>
	 */
	public ImageIcon getTextureImageIcon(String textureIdentifier, TextureType section) {
		return new ImageIcon(getTextureFile(textureIdentifier, section).getAbsolutePath());
	}

	/**
	 * <p>This method gets a PNG texture file depending on the desired type.</p>
	 *
	 * @param textureIdentifier <p>This is the name without the file extension of the texture file.</p>
	 * @param section           <p>This {@link TextureType} defines which path, defined by each generator, MCreator will search the texture file.</p>
	 * @return <p>A PNG {@link File}</p>
	 */
	public File getTextureFile(String textureIdentifier, TextureType section) {
		return new File(getTexturesFolder(section), textureIdentifier + ".png");
	}

	public List<String> getStructureList() {
		List<String> structures = new ArrayList<>();
		File structuresDir = getStructuresDir();
		if (structuresDir != null) {
			File[] files = structuresDir.listFiles();
			for (File file : files != null ? files : new File[0])
				if (file.getName().endsWith(".nbt"))
					structures.add(FilenameUtilsPatched.removeExtension(file.getName()));
		}
		return structures;
	}

	public File[] getArmorTextureFilesForName(String armorTextureName) {
		return new File[] { new File(getTexturesFolder(TextureType.ARMOR), armorTextureName + "_layer_1.png"),
				new File(getTexturesFolder(TextureType.ARMOR), armorTextureName + "_layer_2.png") };
	}

	/**
	 * @param section <p>The {@link TextureType} we want to get the folder, defined by each generator.</p>
	 * @return <p> A list containing all texture files found in the {@link TextureType} provided.</p>
	 */
	public List<File> getTexturesList(TextureType section) {
		return listPNGsInDir(getTexturesFolder(section));
	}

	public void removeStructure(String name) {
		new File(getStructuresDir(), name + ".nbt").delete();
	}

	/**
	 * @param section <p>The {@link TextureType} we want to get the folder, defined by each generator.</p>
	 * @return <p> The folder storing texture files of the given {@link TextureType}.</p>
	 */
	@Nullable public File getTexturesFolder(TextureType section) {
		return GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(),
				section.getID() + "_textures_dir");
	}

	@Nullable public File getStructuresDir() {
		return GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "structures_dir");
	}

	@Nullable public File getSoundsDir() {
		return GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(), "sounds_dir");
	}

	@Nonnull public File getClientRunDir() {
		File retval = GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(),
				"client_run_dir");
		if (retval == null) // Support for old generator types that may not specify client_run_dir
			retval = new File(workspaceFolder, "run/");
		return retval;
	}

	@Nonnull public File getServerRunDir() {
		File retval = GeneratorUtils.getSpecificRoot(workspace, workspace.getGeneratorConfiguration(),
				"server_run_dir");
		if (retval == null) // Support for old generator types that may not specify server_run_dir
			retval = new File(workspaceFolder, "run/");
		return retval;
	}

	public File getModElementsDir() {
		return new File(workspaceFolder, "elements/");
	}

	public File getModelsDir() {
		return new File(workspaceFolder, "models/");
	}

	public File getWorkspaceCacheDir() {
		return new File(workspaceFolder, ".mcreator/");
	}

	public File getModElementPicturesCacheDir() {
		return new File(getWorkspaceCacheDir(), "modElementThumbnails/");
	}

	public File getWorkspaceBackupsCacheDir() {
		return new File(getWorkspaceCacheDir(), "workspaceBackups/");
	}
}
