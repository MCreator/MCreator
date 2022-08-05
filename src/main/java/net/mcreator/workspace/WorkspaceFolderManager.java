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
import net.mcreator.io.OS;
import net.mcreator.io.UserFolderManager;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
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
		return UserFolderManager.getFileFromUserFolder("MCreatorWorkspaces");
	}

	public File getWorkspaceFolder() {
		return workspaceFolder;
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

	public boolean isFileInWorkspace(File file) {
		try {
			boolean inworkspace = file.getCanonicalPath().startsWith(workspaceFolder.getCanonicalPath());
			if (!inworkspace)
				LOG.warn(file.getAbsolutePath() + " is not in workspace path!");
			return inworkspace;
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
			return false;
		}
	}

}
