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

package net.mcreator.generator.setup;

import freemarker.template.Template;
import net.mcreator.generator.Generator;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorUtils;
import net.mcreator.generator.setup.folders.AbstractFolderStructure;
import net.mcreator.generator.template.InlineTemplatesHandler;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

public class WorkspaceGeneratorSetup {

	private static final Logger LOG = LogManager.getLogger("Workspace Setup");

	public static void cleanupGeneratorForSwitchTo(Workspace workspace, GeneratorConfiguration newGenerator) {
		Generator currentGenerator = workspace.getGenerator();

		if (currentGenerator != null) { // conversion from a generator that is present
			// skip if there is no generator change
			if (workspace.getGeneratorConfiguration().getGeneratorName().equals(newGenerator.getGeneratorName()))
				return;

			LOG.info("Cleaning up generator for switch to " + newGenerator.getGeneratorName() + " from "
					+ workspace.getGeneratorConfiguration().getGeneratorName());

			// close gradle connection so no files are locked
			currentGenerator.close();
		} else if (workspace.getWorkspaceSettings().getCurrentGenerator() != null) {
			LOG.info("Cleaning up generator for switch to " + newGenerator.getGeneratorName()
					+ " from non-existent generator " + workspace.getWorkspaceSettings().getCurrentGenerator());
		}

		// delete gradle dirs if present
		if (new File(workspace.getWorkspaceFolder(), ".gradle/").isDirectory()) {
			FileIO.deleteDir(new File(workspace.getWorkspaceFolder(), ".gradle/"));
			FileIO.deleteDir(new File(workspace.getWorkspaceFolder(), "build/"));
		}

		// delete lib dir if present (we need new up-to-date libs)
		if (new File(workspace.getWorkspaceFolder(), "lib/").isDirectory()) {
			FileIO.deleteDir(new File(workspace.getWorkspaceFolder(), "lib/"));
		}

		// delete generator base files
		if (currentGenerator != null) { // only do it if the current generator is known/present
			Set<String> fileNames = PluginLoader.INSTANCE.getResourcesInPackage(
					currentGenerator.getGeneratorName() + ".workspacebase");
			for (String file : fileNames) {
				File generatorFile = new File(workspace.getWorkspaceFolder(),
						file.replace(currentGenerator.getGeneratorName() + "/workspacebase", ""));
				if (generatorFile.isFile())
					generatorFile.delete();
			}
		}

		AbstractFolderStructure folderStructure = AbstractFolderStructure.getFolderStructure(workspace);

		LOG.info("Moving files to new locations while assuming " + folderStructure.getClass().getSimpleName()
				+ " for the generator converting from");

		// move folders to the new locations, starting from more nested folders down
		moveFilesToAnotherDir(folderStructure.getStructuresDir(),
				GeneratorUtils.getSpecificRoot(workspace, newGenerator, "structures_dir"));

		moveFilesToAnotherDir(folderStructure.getSoundsDir(),
				GeneratorUtils.getSpecificRoot(workspace, newGenerator, "sounds_dir"));

		Arrays.stream(TextureType.values()).forEach(
				category -> moveFilesToAnotherDir(folderStructure.getTexturesFolder(category),
						GeneratorUtils.getSpecificRoot(workspace, newGenerator, category.getID() + "_textures_dir")));

		moveFilesToAnotherDir(folderStructure.getSourceRoot(), GeneratorUtils.getSourceRoot(workspace, newGenerator));

		moveFilesToAnotherDir(folderStructure.getResourceRoot(),
				GeneratorUtils.getResourceRoot(workspace, newGenerator));
	}

	private static void moveFilesToAnotherDir(@Nullable File sourceDir, @Nullable File destinationDir) {
		if (sourceDir != null && sourceDir.isDirectory() && destinationDir != null) {
			try {
				if (!sourceDir.getCanonicalPath().equals(destinationDir.getCanonicalPath())) {
					LOG.info("Moving " + sourceDir.getName() + " to a new directory " + destinationDir.getName());
					FileIO.copyDirectory(sourceDir, destinationDir);
					FileIO.deleteDir(sourceDir);
				}
			} catch (IOException e) {
				LOG.warn("Failed to move " + sourceDir.getName() + " dirs", e);
			}
		}
	}

	public static void setupWorkspaceBase(Workspace workspace) {
		Set<String> fileNames = PluginLoader.INSTANCE.getResourcesInPackage(
				workspace.getGenerator().getGeneratorName() + ".workspacebase");
		for (String file : fileNames) {
			try {
				InputStream stream = PluginLoader.INSTANCE.getResourceAsStream(file);
				if (stream != null) {
					File outFile = new File(workspace.getWorkspaceFolder(),
							file.replace(workspace.getGenerator().getGeneratorName() + "/workspacebase", ""));
					if (file.endsWith(".gradle") || file.endsWith(".properties") || file.endsWith(".txt")) {
						String contents = IOUtils.toString(stream, StandardCharsets.UTF_8);
						Template freemarkerTemplate = InlineTemplatesHandler.getTemplate(contents);
						StringWriter stringWriter = new StringWriter();
						freemarkerTemplate.process(workspace.getGenerator().getBaseDataModelProvider().provide(),
								stringWriter, InlineTemplatesHandler.getConfiguration().getObjectWrapper());
						FileIO.writeStringToFile(stringWriter.getBuffer().toString(), outFile);
					} else {
						FileUtils.copyInputStreamToFile(stream, outFile);
					}
				}
			} catch (Exception e) {
				LOG.error(file, e);
			}
		}
	}

	public static boolean shouldSetupBeRan(Generator generator) {
		File setupFile = new File(generator.getFolderManager().getWorkspaceCacheDir(), "setupInfo");
		if (setupFile.isFile()) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(setupFile));
				if (generator.getFullGeneratorVersion().equals(properties.getProperty("buildFileVersion")))
					return false;
			} catch (IOException e) {
				LOG.error(setupFile, e);
			}
		}
		return true;
	}

	public static void completeSetup(Generator generator) {
		FileIO.writeStringToFile("buildFileVersion=" + generator.getFullGeneratorVersion(),
				new File(generator.getFolderManager().getWorkspaceCacheDir(), "setupInfo"));
	}

	public static void requestSetup(Workspace workspace) {
		new File(workspace.getFolderManager().getWorkspaceCacheDir(), "setupInfo").delete();
	}

}
