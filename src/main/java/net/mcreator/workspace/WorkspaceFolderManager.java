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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static net.mcreator.ui.dialogs.GeneralTextureSelector.*;

public class WorkspaceFolderManager {

	private static final Logger LOG = LogManager.getLogger("Workspace Folder Manager");

	private final File workspaceFolder;
	private final Workspace workspace;

	public WorkspaceFolderManager(@NotNull File workspaceFile, @NotNull Workspace workspace) {
		this.workspaceFolder = workspaceFile.getParentFile();
		this.workspace = workspace;
	}

	public File getWorkspaceFolder() {
		return workspaceFolder;
	}

	public ImageIcon getBlockImageIcon(String textureIdentifier) {
		return new ImageIcon(getBlockTextureFile(textureIdentifier).getAbsolutePath());
	}

	public ImageIcon getItemImageIcon(String textureIdentifier) {
		return new ImageIcon(getItemTextureFile(textureIdentifier).getAbsolutePath());
	}

	public File getBlockTextureFile(String textureIdentifier) {
		return new File(getBlocksTexturesDir(), textureIdentifier + ".png");
	}

	public File getItemTextureFile(String textureIdentifier) {
		return new File(getItemsTexturesDir(), textureIdentifier + ".png");
	}

	public File getEntityTextureFile(String textureIdentifier) {
		return new File(getEntitiesTexturesDir(), textureIdentifier + ".png");
	}

	public File getPaintingTextureFile(String textureIdentifier) {
		return new File(getPaintingsTexturesDir(), textureIdentifier + ".png");
	}

	public File getOtherTextureFile(String textureIdentifier) {
		return new File(getOtherTexturesDir(), textureIdentifier + ".png");
	}

	public List<String> getStructureList() {
		List<String> structures = new ArrayList<>();
		File structuresDir = getStructuresDir();
		if (structuresDir != null) {
			File[] files = structuresDir.listFiles();
			for (File file : files != null ? files : new File[0])
				if (file.getName().endsWith(".nbt"))
					structures.add(FilenameUtils.removeExtension(file.getName()));
		}
		return structures;
	}

	public File[] getArmorTextureFilesForName(String armorTextureName) {
		return new File[] { new File(getArmorTexturesDir(), armorTextureName + "_layer_1.png"),
				new File(getArmorTexturesDir(), armorTextureName + "_layer_2.png") };
	}

	public List<File> getBlockTexturesList() {
		return listPNGsInDir(getBlocksTexturesDir(), null);
	}

	public List<File> getItemTexturesList() {
		return listPNGsInDir(getItemsTexturesDir(), null);
	}

	public List<File> getEntityTexturesList() {
		return listPNGsInDir(getEntitiesTexturesDir(), null);
	}

	public List<File> getPaintingTexturesList() {
		return listPNGsInDir(getPaintingsTexturesDir(), null);
	}

	public List<File> getArmorTexturesList() {
		return listPNGsInDir(getArmorTexturesDir(), null);
	}

	public List<File> getOtherTexturesList() {
		return listPNGsInDir(getOtherTexturesDir(), null);
	}

	public void removeStructure(String name) {
		new File(getStructuresDir(), name + ".nbt").delete();
	}

	private static List<File> listPNGsInDir(@Nullable File dir, List<File> list) {
		if (dir == null)
			return Collections.emptyList();

		List<File> retval;
		if (list == null) {
			retval = new LinkedList<>();
		} else {
			retval = list;
		}

		File[] block = dir.listFiles();
		for (File f : block) {
			if (f.isFile()) {
				if (f.getName().endsWith(".png")) {
					retval.add(f);
				}
			} else if (f.isDirectory()) {
				listPNGsInDir(f, retval);
			}
		} for (File file : retval) {
			System.out.println(file.getName());
		}
		return retval;
	}

	@Nullable public File getBlocksTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "block_textures_dir");
	}

	@Nullable public File getItemsTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "item_textures_dir");
	}

	@Nullable public File getEntitiesTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "entity_textures_dir");
	}

	@Nullable public File getPaintingsTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "painting_textures_dir");
	}

	@Nullable public File getArmorTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "armor_textures_dir");
	}

	@Nullable public File getOtherTexturesDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "other_textures_dir");
	}

	@Nullable public File getStructuresDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "structures_dir");
	}

	@Nullable public File getSoundsDir() {
		return GeneratorUtils
				.getSpecificRoot(workspace, workspace.getGenerator().getGeneratorConfiguration(), "sounds_dir");
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

	public static File getSuggestedWorkspaceFoldersRoot() {
		File workspacesFolder = new File(System.getProperty("user.home"), "MCreatorWorkspaces");
		if (!workspacesFolder.getAbsolutePath().matches("[a-zA-Z0-9_/+\\-\\\\:()\\[\\].,@$=`' ]+")) {
			if (OS.getOS() == OS.WINDOWS)
				workspacesFolder = new File("C:/", "MCreatorWorkspaces");
		}
		return workspacesFolder;
	}

}
