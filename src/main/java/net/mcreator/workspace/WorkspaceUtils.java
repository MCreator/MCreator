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

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.converter.ConverterRegistry;
import net.mcreator.generator.GeneratorConfiguration;
import net.mcreator.generator.GeneratorFile;
import net.mcreator.generator.GeneratorTemplate;
import net.mcreator.generator.usercode.UserCodeProcessor;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.modapis.ModAPIManager;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.elements.VariableElement;
import net.mcreator.workspace.elements.VariableType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WorkspaceUtils {

	private static final Logger LOG = LogManager.getLogger(WorkspaceUtils.class);

	public static File getWorkspaceFileForWorkspaceFolder(File workspaceDir) {
		return Stream.ofNullable(workspaceDir.listFiles()).flatMap(Arrays::stream).filter(File::isFile)
				.filter(wfile -> wfile.getName().endsWith(".mcreator"))
				.max(Comparator.comparingLong(File::lastModified)).orElse(null);
	}

	public static void verifyPluginRequirements(Workspace workspace, GeneratorConfiguration generatorConfiguration)
			throws MissingGeneratorFeaturesException {
		Map<String, Collection<String>> missingDefinitions = new LinkedHashMap<>();

		// Check if all required APIs are present
		Set<String> workspaceRequiredAPIs = workspace.getWorkspaceSettings().getMCreatorDependenciesRaw();
		Set<String> generatorSupportedAPIs = ModAPIManager.getModAPIsForGenerator(
						generatorConfiguration.getGeneratorName()).stream().map(e -> e.parent().id())
				.collect(Collectors.toSet());
		Set<String> missingAPIs = workspaceRequiredAPIs.stream().filter(e -> !generatorSupportedAPIs.contains(e))
				.collect(Collectors.toSet());
		if (!missingAPIs.isEmpty())
			missingDefinitions.put("APIs", missingAPIs);

		// Check if all required METs are present
		Set<String> workspaceRequiredMETs = workspace.getModElements().stream().map(ModElement::getTypeString)
				.collect(Collectors.toSet());
		Set<String> generatorSupportedMETs = getSupportedModElementTypes(generatorConfiguration);
		Set<String> missingMETs = workspaceRequiredMETs.stream().filter(e -> !generatorSupportedMETs.contains(e))
				.collect(Collectors.toSet());
		if (!missingMETs.isEmpty())
			missingDefinitions.put("Mod element types", missingMETs);

		// Check if all required VETs are present
		Set<String> workspaceRequiredVETs = workspace.getVariableElements().stream().map(VariableElement::getTypeString)
				.collect(Collectors.toSet());
		Set<String> generatorSupportedVETs = generatorConfiguration.getVariableTypes().getSupportedVariableTypes()
				.stream().map(VariableType::getName).collect(Collectors.toSet());
		Set<String> missingVETs = workspaceRequiredVETs.stream().filter(e -> !generatorSupportedVETs.contains(e))
				.collect(Collectors.toSet());
		if (!missingVETs.isEmpty())
			missingDefinitions.put("Variable types", missingVETs);

		if (!missingDefinitions.isEmpty())
			throw new MissingGeneratorFeaturesException(missingDefinitions);
	}

	/**
	 * @return Registry names of mod element types supported by the given generator, including legacy types
	 * that can be converted to the supported ones
	 */
	private static Set<String> getSupportedModElementTypes(GeneratorConfiguration generatorConfiguration) {
		Set<String> generatorSupportedMETs = generatorConfiguration.getGeneratorStats().getSupportedModElementTypes()
				.stream().map(ModElementType::getRegistryName).collect(Collectors.toSet());
		generatorSupportedMETs.addAll(
				ConverterRegistry.getConvertibleModElementTypes(generatorConfiguration.getGeneratorFlavor()));
		return generatorSupportedMETs;
	}

	/**
	 * Finds mod element definitions (.mod.json files) in the workspace folder that are not on the mod element list
	 * of the workspace. This can happen if the workspace file was not saved after mod elements were added (e.g. due
	 * to a crash) or was restored from an older copy.
	 *
	 * @return Mod elements for the orphaned definitions found, not yet added to the workspace
	 */
	public static List<ModElement> findOrphanedModElements(Workspace workspace) {
		Set<String> supportedModElementTypes = getSupportedModElementTypes(workspace.getGeneratorConfiguration());

		List<ModElement> retval = new ArrayList<>();
		for (File definitionFile : Stream.ofNullable(workspace.getFolderManager().getModElementsDir().listFiles())
				.flatMap(Arrays::stream).filter(file -> file.getName().endsWith(".mod.json")).toList()) {
			String name = definitionFile.getName().substring(0,
					definitionFile.getName().length() - ".mod.json".length());
			if (workspace.containsModElement(name))
				continue;

			try {
				JsonObject definition = JsonParser.parseString(FileIO.readFileToString(definitionFile))
						.getAsJsonObject();

				JsonObject modElementJSON = new JsonObject();
				modElementJSON.addProperty("name", name);
				modElementJSON.addProperty("type", definition.get("_type").getAsString());
				// deserialize the same way as mod elements from the workspace file so legacy type names are handled
				ModElement modElement = WorkspaceFileManager.gson.fromJson(modElementJSON, ModElement.class);

				if (supportedModElementTypes.contains(modElement.getTypeString())) {
					retval.add(modElement);
				} else {
					LOG.warn("Skipping orphaned mod element {} of type {} not supported by the generator", name,
							modElement.getTypeString());
				}
			} catch (Exception e) {
				LOG.warn("Failed to read orphaned mod element definition {}", definitionFile.getName(), e);
			}
		}
		return retval;
	}

	public static void removeAutoGeneratedCode(Workspace workspace,
			@Nullable ProgressDialog.ProgressUnit progressUnit) {
		// After MCreator 2025.2.20614, we can use a non-invasive method to remove auto-generated code
		// because we keep track of auto-generated code in the workspace metadata and mod element metadata.
		if (workspace.getMCreatorVersion() > 202500220614L) {
			LOG.info("Removing auto-generated code from workspace (non-invasive mode)");
			removeAutoGeneratedCodeNonInvasive(workspace, progressUnit);
		} else {
			LOG.info("Removing auto-generated code from workspace (legacy mode)");
			removeAutoGeneratedCodeLegacy(workspace, progressUnit);
		}
	}

	private static void removeAutoGeneratedCodeNonInvasive(Workspace workspace,
			ProgressDialog.ProgressUnit progressUnit) {
		// remove all sources of mod elements that are not locked
		Collection<ModElement> modElementsOld = workspace.getModElements();
		int modstoload = modElementsOld.size();
		int i = 0;
		for (ModElement mod : modElementsOld) {
			if (progressUnit != null)
				progressUnit.setPercent((int) (i / (float) modstoload * 100));
			i++;
			if (!mod.isCodeLocked()) // but we do in this step, if the code is not locked
				mod.getAssociatedFiles().forEach(File::delete);
		}

		List<File> toBePreserved = new ArrayList<>();
		List<GeneratorTemplate> templates = workspace.getGenerator().getModBaseGeneratorTemplatesList();
		for (GeneratorTemplate template : templates) {
			if (template.getFile().isFile()) {
				GeneratorFile generatorFile = template.toGeneratorFile(FileIO.readFileToString(template.getFile()));
				// preserve base mod element files that have user code blocks with content
				if (!UserCodeProcessor.getUserCodeBlocks(generatorFile.contents(), generatorFile.getUsercodeComment())
						.isEmpty())
					toBePreserved.add(template.getFile());
			}
		}

		// Delete base mod files that are not meant to be preserved (don't contain user code blocks)
		if (workspace.getMetadata("files") instanceof List<?> fileList) {
			fileList.stream()
					.map(e -> new File(workspace.getWorkspaceFolder(), e.toString().replace("/", File.separator)))
					// keep files that are not meant to be preserved
					.filter(file -> !FileIO.isFileOnFileList(toBePreserved, file))
					.filter(workspace.getFolderManager()::isFileInWorkspace).forEach(File::delete);
		}
	}

	private static void removeAutoGeneratedCodeLegacy(Workspace workspace,
			@Nullable ProgressDialog.ProgressUnit progressUnit) {
		List<File> toBePreserved = new ArrayList<>();

		// remove all sources of mod elements that are not locked
		Collection<ModElement> modElementsOld = workspace.getModElements();
		int modstoload = modElementsOld.size();
		int i = 0;
		for (ModElement mod : modElementsOld) {
			if (progressUnit != null)
				progressUnit.setPercent((int) (i / (float) modstoload * 100));
			i++;

			if (mod.getType() == ModElementType.UNKNOWN)
				continue; // skip unknown MEs as we don't know what we can remove from them

			GeneratableElement generatableElement = mod.getGeneratableElement();

			if (generatableElement == null)
				continue; // we can't remove sources for GE that lacks definition

			List<File> modElementFiles;
			if (mod.getMetadata("files") != null) {
				modElementFiles = mod.getAssociatedFiles();
			} else { // Legacy mode for old workspaces without files metadata
				modElementFiles = workspace.getGenerator().getModElementGeneratorTemplatesList(generatableElement)
						.stream().map(GeneratorTemplate::getFile).toList();
			}
			toBePreserved.addAll(modElementFiles); // we don't delete mod element files in next step
			if (!mod.isCodeLocked()) // but we do in this step, if the code is not locked
				modElementFiles.forEach(File::delete);
		}

		List<GeneratorTemplate> templates = workspace.getGenerator().getModBaseGeneratorTemplatesList();
		for (GeneratorTemplate template : templates) {
			if (template.getFile().isFile()) {
				GeneratorFile generatorFile = template.toGeneratorFile(FileIO.readFileToString(template.getFile()));
				// preserve base mod element files that have user code blocks with content
				if (!UserCodeProcessor.getUserCodeBlocks(generatorFile.contents(), generatorFile.getUsercodeComment())
						.isEmpty())
					toBePreserved.add(template.getFile());
			}
		}

		// delete all non-mod element related files from code base package
		File[] files = FileIO.listFilesRecursively(workspace.getGenerator().getGeneratorPackageRoot());
		for (File a : files) {
			// if file is not part of one of the mod elements or base files, it can be removed
			if (!FileIO.isFileOnFileList(toBePreserved, a))
				a.delete();
		}
	}

}
