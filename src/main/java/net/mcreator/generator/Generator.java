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

package net.mcreator.generator;

import com.google.gson.GsonBuilder;
import net.mcreator.element.BaseType;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.ModElementType;
import net.mcreator.element.ModElementTypeLoader;
import net.mcreator.element.types.interfaces.ICommonType;
import net.mcreator.generator.setup.WorkspaceGeneratorSetup;
import net.mcreator.generator.template.MinecraftCodeProvider;
import net.mcreator.generator.template.TemplateExpressionParser;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.generator.template.base.BaseDataModelProvider;
import net.mcreator.generator.usercode.UserCodeProcessor;
import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Closeable;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Generator implements IGenerator, Closeable {

	public static final Map<String, GeneratorConfiguration> GENERATOR_CACHE = Collections.synchronizedMap(
			new LinkedHashMap<>());

	private final Logger LOG;
	private final String generatorName;
	private final GeneratorConfiguration generatorConfiguration;

	private final Map<String, TemplateGenerator> templateGeneratorMap = new HashMap<>();

	private final MinecraftCodeProvider minecraftCodeProvider;

	private final Workspace workspace;

	@Nullable private GradleConnector gradleConnector;
	@Nullable private ProjectConnection gradleProjectConnection;
	@Nullable private GeneratorGradleCache generatorGradleCache;

	private final BaseDataModelProvider baseDataModelProvider;

	public Generator(@Nonnull Workspace workspace) {
		this.workspace = workspace;
		this.generatorName = workspace.getWorkspaceSettings().getCurrentGenerator();

		this.LOG = LogManager.getLogger("Generator " + generatorName);

		this.generatorConfiguration = GENERATOR_CACHE.get(generatorName);

		this.minecraftCodeProvider = new MinecraftCodeProvider(workspace);

		this.baseDataModelProvider = new BaseDataModelProvider(this);
	}

	@Override public @Nonnull Workspace getWorkspace() {
		return workspace;
	}

	@Override public GeneratorConfiguration getGeneratorConfiguration() {
		return generatorConfiguration;
	}

	public TemplateGenerator getTemplateGeneratorFromName(String name) {
		return templateGeneratorMap.computeIfAbsent(name,
				key -> new TemplateGenerator(generatorConfiguration.getTemplateGenConfigFromName(key), this));
	}

	public String getGeneratorName() {
		return generatorName;
	}

	public MinecraftCodeProvider getMinecraftCodeProvider() {
		return minecraftCodeProvider;
	}

	public BaseDataModelProvider getBaseDataModelProvider() {
		return baseDataModelProvider;
	}

	public File getGeneratorPackageRoot() {
		return new File(GeneratorTokens.replaceTokens(workspace, generatorConfiguration.getSourceRoot()),
				workspace.getWorkspaceSettings().getModElementsPackage().replace(".", "/"));
	}

	public File getLangFilesRoot() {
		return new File(GeneratorTokens.replaceTokens(workspace,
				(String) generatorConfiguration.getLanguageFileSpecification().get("root_folder")));
	}

	/**
	 * Generates the generator mod base files. Formats the imports in the generated Java code.
	 *
	 * @return true if generator generated all files without any errors
	 */
	public boolean generateBase() {
		return this.generateBase(true);
	}

	/**
	 * Generates the generator mod base files and writes them to disk.
	 *
	 * @param formatAndOrganiseImports true if imports should be formatted
	 * @return true if generator generated all files without any errors
	 */
	public boolean generateBase(boolean formatAndOrganiseImports) {
		AtomicBoolean success = new AtomicBoolean(true);

		TemplateGenerator templateGenerator = getTemplateGeneratorFromName("templates");

		List<GeneratorFile> generatorFiles = getModBaseGeneratorTemplatesList(true).stream().map(generatorTemplate -> {
			try {
				String code = templateGenerator.generateBaseFromTemplate(
						(String) generatorTemplate.getTemplateDefinition().get("template"),
						generatorTemplate.getDataModel(),
						(String) generatorTemplate.getTemplateDefinition().get("variables"));
				return generatorTemplate.toGeneratorFile(code);
			} catch (TemplateGeneratorException e) {
				success.set(false);
				return null;
			}
		}).filter(Objects::nonNull).collect(Collectors.toList());

		generateFiles(generatorFiles, formatAndOrganiseImports);

		// run other source tasks
		GeneratorFileTasks.runFileTasks(this, generatorConfiguration.getSourceSetupTasks());

		// generate lang files
		LocalizationUtils.generateLanguageFiles(this, workspace, generatorConfiguration.getLanguageFileSpecification());

		// generate tags files
		TagsUtils.generateTagsFiles(this, workspace, generatorConfiguration.getTagsSpecification());

		return success.get();
	}

	/**
	 * GeneratableElement should be saved AFTER this method is called, not before
	 * as it can be altered in this process
	 *
	 * @param element GeneratableElement to generate code and resources for
	 * @return true if generation succeeds
	 */
	public boolean generateElement(GeneratableElement element) {
		try {
			this.generateElement(element, true);
			return true;
		} catch (TemplateGeneratorException e) {
			LOG.error("Failed to generate mod element: " + element.getModElement().getName(), e);
			return false;
		}
	}

	@Nonnull public List<GeneratorFile> generateElement(GeneratableElement element, boolean formatAndOrganiseImports)
			throws TemplateGeneratorException {
		return this.generateElement(element, formatAndOrganiseImports, true);
	}

	@Nonnull
	public List<GeneratorFile> generateElement(GeneratableElement element, boolean formatAndOrganiseImports,
			boolean performFSTasks) throws TemplateGeneratorException {
		if (element.getModElement().isCodeLocked()) {
			LOG.debug("Skipping code generation for mod element: " + element.getModElement().getName()
					+ " - the code of this element is locked");
			return new ArrayList<>();
		}

		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(element.getModElement().getType()); // config map
		if (map == null) {
			if (element.getModElement().getType() != ModElementType.UNKNOWN) // silently skip unknown elements
				LOG.warn("Failed to load element definition for mod element type " + element.getModElement().getType()
						.getRegistryName());
			return new ArrayList<>();
		}

		Set<GeneratorFile> generatorFiles = new HashSet<>();

		TemplateGenerator templateGenerator = getTemplateGeneratorFromName("templates");

		// generate all source files
		List<GeneratorTemplate> generatorTemplateList = getModElementGeneratorTemplatesList(element);
		for (GeneratorTemplate generatorTemplate : generatorTemplateList) {
			String templateFileName = (String) generatorTemplate.getTemplateDefinition().get("template");

			Map<String, Object> dataModel = generatorTemplate.getDataModel();

			String variables = (String) generatorTemplate.getTemplateDefinition().get("variables");

			String code;
			if (generatorTemplate instanceof ListTemplate listTemplate) { // list template - generate it for list data item pointed at
				code = templateGenerator.generateListItemFromTemplate(
						listTemplate.getTemplatesList().listData().get(listTemplate.getListItemIndex()),
						listTemplate.getListItemIndex(), element, templateFileName, dataModel, variables,
						element.getAdditionalTemplateData());
			} else { // regular template
				code = templateGenerator.generateElementFromTemplate(element, templateFileName, dataModel, variables,
						element.getAdditionalTemplateData());
			}

			GeneratorFile generatorFile = generatorTemplate.toGeneratorFile(code);

			// only preserve the last template for given file (only the last template matching given file will be generated)
			generatorFiles.remove(generatorFile);
			generatorFiles.add(generatorFile);
		}

		if (performFSTasks) {
			// remove outdated files for mod element files list (used to know what files belong to the ME for removal on regeneration)
			Object oldFiles = element.getModElement().getMetadata("files");
			if (oldFiles instanceof List<?> fileList)
				// filter by files in workspace so one can not create .mcreator file that would delete files on computer when opened
				fileList.stream().map(e -> new File(getWorkspaceFolder(), e.toString().replace("/", File.separator)))
						.filter(workspace.getFolderManager()::isFileInWorkspace).forEach(File::delete);

			// generate files as old files were deleted
			generateFiles(generatorFiles, formatAndOrganiseImports);

			// store paths of generated files
			element.getModElement().putMetadata("files", generatorFiles.stream().map(GeneratorFile::getFile)
					.map(e -> getFolderManager().getPathInWorkspace(e).replace(File.separator, "/")).toList());

			// add lang keys to the workspace
			LocalizationUtils.generateLocalizationKeys(this, element, (List<?>) map.get("localizationkeys"));

			// add tag elements to the workspace
			TagsUtils.processDefinitionToTags(this, element, (List<?>) map.get("tags"), false);

			// do additional tasks if mod element has them
			element.finalizeModElementGeneration();
		}

		return new ArrayList<>(generatorFiles);
	}

	public List<String> getElementLocalizationKeys(GeneratableElement element) {
		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(element.getModElement().getType()); // config map
		if (map == null) {
			if (element.getModElement().getType() != ModElementType.UNKNOWN) // silently skip unknown elements
				LOG.warn("Failed to load element definition for mod element type " + element.getModElement().getType()
						.getRegistryName());
			return new ArrayList<>();
		}

		return new ArrayList<>(LocalizationUtils.processDefinitionToLocalizationKeys(this, element,
				(List<?>) map.get("localizationkeys")).keySet());
	}

	public void removeElementFilesAndLangKeys(GeneratableElement generatableElement) {
		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(generatableElement.getModElement().getType());

		if (map == null) {
			if (generatableElement.getModElement().getType()
					!= ModElementType.UNKNOWN) // silently skip unknown elements
				LOG.warn("Failed to load element definition for mod element type " + generatableElement.getModElement()
						.getType().getRegistryName());
			return;
		}

		for (GeneratorTemplate template : getModElementGeneratorTemplatesList(generatableElement)) {
			if (workspace.getFolderManager().isFileInWorkspace(template.getFile()))
				template.getFile().delete();
		}

		// delete localization keys associated with the mod element from the workspace
		LocalizationUtils.deleteLocalizationKeys(this, generatableElement, (List<?>) map.get("localizationkeys"));

		// delete tag elements associated with the mod element from the workspace
		TagsUtils.processDefinitionToTags(this, generatableElement, (List<?>) map.get("tags"), true);
	}

	@Nonnull public List<GeneratorTemplate> getModBaseGeneratorTemplatesList(boolean performFSTasks) {
		AtomicInteger templateID = new AtomicInteger();

		List<GeneratorTemplate> files = new ArrayList<>(
				processTemplateDefinitionsToGeneratorTemplates(generatorConfiguration.getBaseTemplates(),
						performFSTasks, templateID));

		// Pre-precess GEs and sort them by sortID
		List<GeneratableElement> generatableElements = workspace.getModElements().stream()
				.sorted(Comparator.comparing(ModElement::getSortID)).map(ModElement::getGeneratableElement)
				.filter(Objects::nonNull).toList();

		// Add mod element type specific global files (eg. registries for mod elements)
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			List<GeneratorTemplate> globalTemplatesList = getGlobalTemplatesListForModElementType(type, performFSTasks,
					templateID);

			List<GeneratableElement> filteredGeneratableElements = generatableElements.parallelStream()
					.filter(e -> e.getModElement().getType() == type).toList();

			if (!filteredGeneratableElements.isEmpty()) {
				globalTemplatesList.forEach(
						e -> e.addDataModelEntry(type.getRegistryName() + "s", filteredGeneratableElements));

				files.addAll(globalTemplatesList);
			} else if (performFSTasks) { // if no elements of this type are present, delete the global template for that type
				for (GeneratorTemplate template : globalTemplatesList) {
					template.getFile().delete();
				}
			}
		}

		Map<BaseType, List<GeneratableElement>> baseTypeListMap = new HashMap<>();
		for (GeneratableElement generatableElement : generatableElements) {
			if (generatableElement instanceof ICommonType commonType) {
				for (BaseType baseType : commonType.getBaseTypesProvided()) {
					if (!baseTypeListMap.containsKey(baseType))
						baseTypeListMap.put(baseType, new ArrayList<>());

					baseTypeListMap.get(baseType).add(generatableElement);
				}
			}
		}

		for (BaseType baseType : BaseType.values()) {
			List<GeneratorTemplate> globalTemplatesList = getGlobalTemplatesListForDefinition(
					generatorConfiguration.getDefinitionsProvider().getBaseTypeDefinition(baseType), performFSTasks,
					templateID);

			if (!baseTypeListMap.containsKey(baseType) || baseTypeListMap.get(baseType).isEmpty()) {
				if (performFSTasks) { // if no elements of this type are present, delete the base type template for that type
					for (GeneratorTemplate template : globalTemplatesList) {
						template.getFile().delete();
					}
				}
			} else {
				globalTemplatesList.forEach(
						e -> e.addDataModelEntry(baseType.getPluralName(), baseTypeListMap.get(baseType)));

				files.addAll(globalTemplatesList);
			}
		}

		return files;
	}

	public List<GeneratorTemplate> getGlobalTemplatesListForModElementType(ModElementType<?> type,
			boolean performFSTasks, AtomicInteger templateID) {
		return getGlobalTemplatesListForDefinition(
				generatorConfiguration.getDefinitionsProvider().getModElementDefinition(type), performFSTasks,
				templateID);
	}

	public List<GeneratorTemplate> getGlobalTemplatesListForDefinition(@Nullable Map<?, ?> map, boolean performFSTasks,
			AtomicInteger templateID) {
		if (map == null)
			return new ArrayList<>();

		List<?> templates = (List<?>) map.get("global_templates");
		if (templates != null)
			return processTemplateDefinitionsToGeneratorTemplates(templates, performFSTasks, templateID);

		return new ArrayList<>();
	}

	private List<GeneratorTemplate> processTemplateDefinitionsToGeneratorTemplates(@Nonnull List<?> templates,
			boolean performFSTasks, AtomicInteger templateID) {
		Set<GeneratorTemplate> files = new HashSet<>();
		for (Object template : templates) {
			File file = new File(GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) template).get("name")));

			if (!workspace.getFolderManager().isFileInWorkspace(file))
				continue; // if file is not in workspace, we skip it

			GeneratorTemplate generatorTemplate = new GeneratorTemplate(file,
					Integer.toString(templateID.getAndIncrement()) + ((Map<?, ?>) template).get("template"),
					(Map<?, ?>) template);

			if (generatorTemplate.shouldBeSkippedBasedOnCondition(this, workspace.getWorkspaceInfo())) {
				// if template is skipped, we delete its potential file if performFSTasks and file was not previously generated
				// this prevents deletion of files that were previously generated by another passing condition for the same file
				if (!files.contains(generatorTemplate) && performFSTasks) {
					generatorTemplate.getFile().delete(); // if template is skipped, we delete its potential file
				}
				continue;
			}

			// only keep the last template for given file
			files.remove(generatorTemplate);
			files.add(generatorTemplate);
		}

		return new ArrayList<>(files);
	}

	@Nonnull public List<GeneratorTemplate> getModElementGeneratorTemplatesList(GeneratableElement generatableElement) {
		if (generatableElement == null)
			throw new RuntimeException("GeneratableElement is null");

		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(generatableElement.getModElement().getType());

		if (map == null) {
			if (generatableElement.getModElement().getType()
					!= ModElementType.UNKNOWN) // silently skip unknown elements
				LOG.info("Failed to load element definition for mod element type " + generatableElement.getModElement()
						.getType().getRegistryName());
			return new ArrayList<>();
		}

		Set<GeneratorTemplate> files = new HashSet<>();
		List<?> templates = (List<?>) map.get("templates");
		if (templates != null) {
			int templateID = 0;

			for (Object template : templates) {
				File file = new File(GeneratorTokens.replaceVariableTokens(generatableElement,
						GeneratorTokens.replaceTokens(workspace,
								((String) ((Map<?, ?>) template).get("name")).replace("@NAME",
										generatableElement.getModElement().getName()).replace("@registryname",
										generatableElement.getModElement().getRegistryName()))));

				if (!workspace.getFolderManager().isFileInWorkspace(file))
					continue; // if file is not in workspace, we skip it

				GeneratorTemplate generatorTemplate = new GeneratorTemplate(file,
						Integer.toString(templateID) + ((Map<?, ?>) template).get("template"), (Map<?, ?>) template);

				if (generatorTemplate.shouldBeSkippedBasedOnCondition(this, generatableElement)) {
					continue;
				}

				// only preserve the last template for given file (only the last template matching given file will be generated)
				files.remove(generatorTemplate);
				files.add(generatorTemplate);

				templateID++;
			}
		}

		// we add all list templates (if any) for given element to the list
		getModElementListTemplates(generatableElement).forEach(list -> list.templates().forEach(files::addAll));

		return new ArrayList<>(files);
	}

	@Nonnull public List<GeneratorTemplatesList> getModElementListTemplates(GeneratableElement generatableElement) {
		if (generatableElement == null)
			throw new RuntimeException("GeneratableElement is null");

		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(generatableElement.getModElement().getType());
		if (map == null) {
			LOG.info("Failed to load element list templates definition for mod element type "
					+ generatableElement.getModElement().getType().getRegistryName());
			return new ArrayList<>();
		}

		Set<GeneratorTemplatesList> fileLists = new HashSet<>();

		List<?> templateLists = (List<?>) map.get("list_templates");
		if (templateLists != null) {
			int templateID = 0;
			for (Object list : templateLists) {
				List<?> templates = (List<?>) ((Map<?, ?>) list).get("forEach");
				if (templates != null) {
					String groupName = (String) ((Map<?, ?>) list).get("name");
					Object listData = TemplateExpressionParser.processFTLExpression(this,
							(String) ((Map<?, ?>) list).get("listData"), generatableElement);

					// we check type of listData collection and convert it to a list if needed
					List<?> items;
					if (listData instanceof Map<?, ?> listMap)
						items = List.copyOf(listMap.entrySet());
					else if (listData instanceof Collection<?> collection)
						items = List.copyOf(collection);
					else if (listData instanceof Iterable<?> iterable) // fallback for the worst case
						items = List.copyOf(StreamSupport.stream(iterable.spliterator(), false).toList());
					else
						items = List.of();

					GeneratorTemplatesList templatesList = new GeneratorTemplatesList(groupName, items,
							new ArrayList<>());

					for (int index = 0; index < items.size(); index++) {
						Set<ListTemplate> filesForCurrentItem = new HashSet<>();
						for (Object template : templates) {
							File file = new File(
									GeneratorTokens.replaceVariableTokens(generatableElement, items.get(index),
											GeneratorTokens.replaceTokens(workspace,
													((String) ((Map<?, ?>) template).get("name")).replace("@NAME",
																	generatableElement.getModElement().getName())
															.replace("@registryname", generatableElement.getModElement()
																	.getRegistryName())
															.replace("@itemindex", Integer.toString(index)))));

							if (!workspace.getFolderManager().isFileInWorkspace(file))
								continue; // if file is not in workspace, we skip it

							ListTemplate listTemplate = new ListTemplate(file,
									Integer.toString(templateID) + ((Map<?, ?>) template).get("template"),
									templatesList, index, (Map<?, ?>) template);

							if (listTemplate.shouldBeSkippedBasedOnCondition(this, items.get(index)))
								continue;

							// only preserve the last template for given file (only the last template matching given file will be generated)
							filesForCurrentItem.remove(listTemplate);
							filesForCurrentItem.add(listTemplate);

							templateID++;
						}

						templatesList.templates().add(List.copyOf(filesForCurrentItem));
					}

					fileLists.add(templatesList);
				}
			}
		}

		return new ArrayList<>(fileLists);
	}

	public ModElement getModElementThisFileBelongsTo(File file) {
		if (!file.isFile() || !workspace.getFolderManager().isFileInWorkspace(file))
			return null;

		return workspace.getModElements().parallelStream().filter(element -> {
			if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo().get(element.getType())
					== GeneratorStats.CoverageStatus.NONE)
				return false;

			Object oldFiles = element.getMetadata("files");
			if (oldFiles instanceof List<?> fileList) {
				return FileIO.isFileOnFileList(fileList.stream()
								.map(e -> new File(getWorkspaceFolder(), e.toString().replace("/", File.separator))).toList(),
						file);
			} else {
				return false;
			}
		}).findAny().orElse(null);
	}

	private void generateFiles(Collection<GeneratorFile> generatorFiles, boolean formatAndOrganiseImports) {
		Map<File, String> javaFiles = new HashMap<>();

		for (GeneratorFile generatorFile : generatorFiles) {
			if (generatorFile.writer() == GeneratorFile.Writer.JAVA) {
				// first create Java files if they do not exist already for the import formatter to load them
				// so the imports get properly organised in the next step
				if (formatAndOrganiseImports && !generatorFile.getFile().isFile())
					FileIO.touchFile(generatorFile.getFile());

				javaFiles.put(generatorFile.getFile(),
						UserCodeProcessor.processUserCode(generatorFile.getFile(), generatorFile.contents(), "//"));
			} else if (generatorFile.writer() == GeneratorFile.Writer.JSON) {
				JSONWriter.writeJSONToFile(generatorFile.contents(), generatorFile.getFile());
			} else if (generatorFile.writer() == GeneratorFile.Writer.FILE) {
				String usercodeComment = generatorFile.source().getUsercodeComment();
				if (usercodeComment != null)
					FileIO.writeStringToFile(
							UserCodeProcessor.processUserCode(generatorFile.getFile(), generatorFile.contents(),
									usercodeComment), generatorFile.getFile());
				else
					FileIO.writeStringToFile(generatorFile.contents(), generatorFile.getFile());
			}
		}

		// After we have list of Java files, and they are created, we can format and organise imports in them
		if (!javaFiles.isEmpty())
			ClassWriter.batchWriteClassToFile(workspace, javaFiles, formatAndOrganiseImports, null);
	}

	public void runResourceSetupTasks() {
		GeneratorFileTasks.runFileTasks(this, generatorConfiguration.getResourceSetupTasks());
	}

	@Nullable public ProjectConnection getGradleProjectConnection() {
		if (gradleProjectConnection == null) {
			try {
				gradleConnector = GradleConnector.newConnector().forProjectDirectory(workspace.getWorkspaceFolder())
						.useGradleUserHomeDir(UserFolderManager.getGradleHome());
				gradleProjectConnection = gradleConnector.connect();
			} catch (Exception e) {
				LOG.warn("Failed to load Gradle project", e);
			}
		}

		return gradleProjectConnection;
	}

	@Nullable public ProjectJarManager getProjectJarManager() {
		if (generatorGradleCache != null)
			return generatorGradleCache.projectJarManager;
		else
			return null;
	}

	@Override public void close() {
		if (gradleProjectConnection != null) {
			LOG.info("Closing Gradle project connection");
			gradleProjectConnection.close();

			if (gradleConnector != null)
				gradleConnector.disconnect();
		}
	}

	public void loadOrCreateGradleCaches() throws GradleCacheImportFailedException {
		File cacheFile = new File(workspace.getFolderManager().getWorkspaceCacheDir(), "generatorGradleCache");
		if (cacheFile.isFile()) {
			String cache = FileIO.readFileToString(cacheFile);
			generatorGradleCache = new GsonBuilder().disableHtmlEscaping().create()
					.fromJson(cache, GeneratorGradleCache.class);
			if (generatorGradleCache != null) {
				LOG.info("Gradle cache will be loaded from cache file");
				generatorGradleCache.reinitAfterGSON(this);
				return;
			}
		}

		if (!WorkspaceGeneratorSetup.shouldSetupBeRan(this))
			reloadGradleCaches();
	}

	Logger getLogger() {
		return LOG;
	}

	public void reloadGradleCaches() {
		LOG.info("Reloading generator Gradle cache");

		this.generatorGradleCache = new GeneratorGradleCache(this);
		String cache = new GsonBuilder().disableHtmlEscaping().create().toJson(generatorGradleCache);
		FileIO.writeStringToFile(cache,
				new File(workspace.getFolderManager().getWorkspaceCacheDir(), "generatorGradleCache"));
	}

	public GeneratorGradleCache getGradleCache() {
		return generatorGradleCache;
	}

	public void setGradleCache(GeneratorGradleCache generatorGradleCache) {
		this.generatorGradleCache = generatorGradleCache;
	}

}
