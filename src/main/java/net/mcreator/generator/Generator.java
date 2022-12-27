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
import net.mcreator.gradle.GradleCacheImportFailedException;
import net.mcreator.io.FileIO;
import net.mcreator.io.UserFolderManager;
import net.mcreator.io.writer.ClassWriter;
import net.mcreator.io.writer.JSONWriter;
import net.mcreator.java.ProjectJarManager;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import net.mcreator.workspace.resources.Model;
import net.mcreator.workspace.resources.ModelUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Generator implements IGenerator, Closeable {

	public static final Map<String, GeneratorConfiguration> GENERATOR_CACHE = Collections.synchronizedMap(
			new LinkedHashMap<>());

	protected final Logger LOG;
	private final String generatorName;
	private final GeneratorConfiguration generatorConfiguration;

	private final Map<String, TemplateGenerator> templateGeneratorMap = new HashMap<>();

	private final MinecraftCodeProvider minecraftCodeProvider;

	private final Workspace workspace;

	@Nullable private ProjectConnection gradleProjectConnection;
	@Nullable private GeneratorGradleCache generatorGradleCache;

	private final BaseDataModelProvider baseDataModelProvider;

	public Generator(@Nonnull Workspace workspace) {
		this.workspace = workspace;
		this.generatorName = workspace.getWorkspaceSettings().getCurrentGenerator();

		this.LOG = LogManager.getLogger("Generator " + generatorName);

		this.generatorConfiguration = GENERATOR_CACHE.get(generatorName);

		this.baseDataModelProvider = new BaseDataModelProvider(this);

		this.minecraftCodeProvider = new MinecraftCodeProvider(workspace);
	}

	public int getStartIDFor(String baseType) {
		try {
			String idstring = (String) generatorConfiguration.getStardIDMap().get(baseType);
			return Integer.parseInt(idstring.trim());
		} catch (Exception e) {
			return -1;
		}
	}

	@Override public @Nonnull Workspace getWorkspace() {
		return workspace;
	}

	@Override public GeneratorConfiguration getGeneratorConfiguration() {
		return generatorConfiguration;
	}

	public TemplateGenerator getTemplateGeneratorFromName(String name) {
		if (templateGeneratorMap.containsKey(name))
			return templateGeneratorMap.get(name);
		else {
			TemplateGenerator tpl = new TemplateGenerator(generatorConfiguration.getTemplateGenConfigFromName(name),
					this);
			templateGeneratorMap.put(name, tpl);
			return tpl;
		}
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
	 * Generates the generator mod base files. Writes files to disk.
	 *
	 * @param formatAndOrganiseImports true if imports should be formatted
	 * @return true if generator generated all files without any errors
	 */
	public boolean generateBase(boolean formatAndOrganiseImports) {
		return this.generateBase(formatAndOrganiseImports, true);
	}

	/**
	 * Generates the generator mod base files and optionally writes them to disk.
	 *
	 * @param formatAndOrganiseImports true if imports should be formatted
	 * @param performFSTasks           true if FS should be affected
	 * @return true if generator generated all files without any errors
	 */
	public boolean generateBase(boolean formatAndOrganiseImports, boolean performFSTasks) {
		AtomicBoolean success = new AtomicBoolean(true);

		List<GeneratorFile> generatorFiles = getModBaseGeneratorTemplatesList(performFSTasks).parallelStream()
				.map(generatorTemplate -> {
					if (((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock") != null
							&& ((Map<?, ?>) generatorTemplate.getTemplateData()).get("canLock")
							.equals("true")) // can this file be locked
						if (this.workspace.getWorkspaceSettings().isLockBaseModFiles()) // are mod base file locked
							return null; // if they are, we skip this file

					String templateFileName = (String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get(
							"template");

					Map<String, Object> dataModel = generatorTemplate.getDataModel();

					extractVariables(generatorTemplate, dataModel);

					try {
						String code = getTemplateGeneratorFromName("templates").generateBaseFromTemplate(
								templateFileName, dataModel);
						return new GeneratorFile(generatorTemplate,
								(String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("writer"), code);
					} catch (TemplateGeneratorException e) {
						success.set(false);
					}

					return null;
				}).filter(Objects::nonNull).collect(Collectors.toList());

		if (performFSTasks) {
			generateFiles(generatorFiles, formatAndOrganiseImports);

			// run other source tasks
			runSetupTasks(generatorConfiguration.getSourceSetupTasks());

			// generate lang files
			LocalizationUtils.generateLanguageFiles(this, workspace,
					generatorConfiguration.getLanguageFileSpecification());
		}

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
			e.printStackTrace();
			return false;
		}
	}

	public List<GeneratorFile> generateElement(GeneratableElement element, boolean formatAndOrganiseImports)
			throws TemplateGeneratorException {
		return this.generateElement(element, formatAndOrganiseImports, true);
	}

	public List<GeneratorFile> generateElement(GeneratableElement element, boolean formatAndOrganiseImports,
			boolean performFSTasks) throws TemplateGeneratorException {
		if (element.getModElement().isCodeLocked()) {
			LOG.debug("Skipping code generation for mod element: " + element.getModElement().getName()
					+ " - the code of this element is locked");
			return Collections.emptyList();
		}

		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider()
				.getModElementDefinition(element.getModElement().getType()); // config map
		if (map == null) {
			LOG.warn("Failed to load element definition for mod element type " + element.getModElement().getType()
					.getRegistryName());
			return Collections.emptyList();
		}

		Set<GeneratorFile> generatorFiles = new HashSet<>();

		// generate all source files
		List<GeneratorTemplate> generatorTemplateList = getModElementGeneratorTemplatesList(element.getModElement(),
				performFSTasks, element);
		if (generatorTemplateList != null) {
			for (GeneratorTemplate generatorTemplate : generatorTemplateList) {
				String templateFileName = (String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("template");

				Map<String, Object> dataModel = generatorTemplate.getDataModel();
				extractVariables(generatorTemplate, dataModel);

				String code;
				if (generatorTemplate instanceof ListTemplate listTemplate) { // list template - generate it for list data item pointed at
					code = getTemplateGeneratorFromName("templates").generateListItemFromTemplate(
							listTemplate.getTemplatesList().listData().get(listTemplate.getListItemIndex()),
							listTemplate.getListItemIndex(), element, templateFileName, dataModel,
							element.getAdditionalTemplateData());
				} else { // regular template
					code = getTemplateGeneratorFromName("templates").generateElementFromTemplate(element,
							templateFileName, dataModel, element.getAdditionalTemplateData());
				}

				GeneratorFile generatorFile = new GeneratorFile(generatorTemplate,
						(String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("writer"), code);

				// only preserve the last instance of template for a file
				generatorFiles.remove(generatorFile);
				generatorFiles.add(generatorFile);
			}
		}

		if (performFSTasks) {
			// remove outdated files
			Object oldFiles = element.getModElement().getMetadata("files");
			if (oldFiles instanceof List<?> fileList)
				// filter by files in workspace so one can not create .mcreator file that would delete files on computer when opened
				fileList.stream().map(e -> new File(getWorkspaceFolder(), (String) e))
						.filter(workspace.getFolderManager()::isFileInWorkspace).forEach(File::delete);

			generateFiles(generatorFiles, formatAndOrganiseImports);

			// store paths of generated files
			element.getModElement().putMetadata("files", generatorFiles.stream()
					.map(e -> getWorkspaceFolder().toPath().relativize(e.getFile().toPath()).toString()).toList());

			LocalizationUtils.extractLocalizationKeys(this, element, (List<?>) map.get("localizationkeys"));

			// do additional tasks if mod element has them
			element.finalizeModElementGeneration();
		}

		return new ArrayList<>(generatorFiles);
	}

	/**
	 * Load any hardcoded variables from template definition into dataModel
	 *
	 * @param generatorTemplate Template from which to get variables
	 * @param dataModel         Data model to place variables into
	 */
	private void extractVariables(GeneratorTemplate generatorTemplate, Map<String, Object> dataModel) {
		String variables = (String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("variables");
		if (variables != null) {
			try {
				String[] vars = variables.split(";");
				for (String var : vars) {
					String[] data = var.split("(?<!/)=");
					dataModel.put("var_" + data[0].trim().replace("/=", "="), data[1].trim().replace("/=", "="));
				}
			} catch (Exception ignored) {
			}
		}
	}

	public void removeElementFilesAndLangKeys(ModElement element) {
		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider().getModElementDefinition(element.getType());

		if (map == null) {
			LOG.warn("Failed to load element definition for mod element type " + element.getType().getRegistryName());
			return;
		}

		Objects.requireNonNull(getModElementGeneratorTemplatesList(element, true, null))
				.forEach(template -> template.getFile().delete());

		// delete localization keys associated with the mod element
		LocalizationUtils.deleteLocalizationKeys(this, element, (List<?>) map.get("localizationkeys"));
	}

	public List<GeneratorTemplate> getModBaseGeneratorTemplatesList(boolean performFSTasks) {
		List<GeneratorTemplate> files = new ArrayList<>();
		AtomicInteger templateID = new AtomicInteger();

		List<?> templates = generatorConfiguration.getBaseTemplates();
		for (Object template : templates) {
			TemplateExpressionParser.Operator operator = TemplateExpressionParser.Operator.AND;
			Object conditionRaw = ((Map<?, ?>) template).get("condition");
			if (conditionRaw == null) {
				conditionRaw = ((Map<?, ?>) template).get("condition_any");
				operator = TemplateExpressionParser.Operator.OR;
			}

			String name = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) template).get("name"));

			if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw,
					workspace.getWorkspaceInfo(), operator)) {
				if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks)
					if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
						new File(name).delete(); // if template is skipped, we delete its potential file
					}
				continue;
			}

			files.add(new GeneratorTemplate(new File(name),
					Integer.toString(templateID.get()) + ((Map<?, ?>) template).get("template"), template));

			templateID.getAndIncrement();
		}

		// Add mod element type specific global files (eg. registries for mod elements)
		for (ModElementType<?> type : ModElementTypeLoader.REGISTRY) {
			List<GeneratorTemplate> globalTemplatesList = getModElementGlobalTemplatesList(type, performFSTasks,
					templateID);

			List<GeneratableElement> elementsList = workspace.getWorkspaceInfo().getElementsOfType(type).stream()
					.sorted(Comparator.comparing(ModElement::getSortID)).map(ModElement::getGeneratableElement)
					.collect(Collectors.toList());

			if (!elementsList.isEmpty()) {
				globalTemplatesList.forEach(e -> e.addDataModelEntry(type.getRegistryName() + "s", elementsList));

				files.addAll(globalTemplatesList);
			} else if (performFSTasks) { // if no elements of this type are present, delete the global template for that type
				for (GeneratorTemplate template : globalTemplatesList) {
					if (workspace.getFolderManager().isFileInWorkspace(template.getFile())) {
						template.getFile().delete();
					}
				}
			}
		}

		Map<BaseType, List<GeneratableElement>> baseTypeListMap = new HashMap<>();
		for (ModElement modElement : workspace.getModElements()) {
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof ICommonType) {
				Collection<BaseType> baseTypes = ((ICommonType) generatableElement).getBaseTypesProvided();
				for (BaseType baseType : baseTypes) {
					if (!baseTypeListMap.containsKey(baseType))
						baseTypeListMap.put(baseType, new ArrayList<>());

					baseTypeListMap.get(baseType).add(generatableElement);
				}
			}
		}

		for (BaseType baseType : BaseType.values()) {
			List<GeneratorTemplate> globalTemplatesList = getGlobalTemplatesList(
					generatorConfiguration.getDefinitionsProvider().getBaseTypeDefinition(baseType), performFSTasks,
					templateID);

			if (!baseTypeListMap.containsKey(baseType) || baseTypeListMap.get(baseType).isEmpty()) {
				if (performFSTasks) { // if no elements of this type are present, delete the base type template for that type
					for (GeneratorTemplate template : globalTemplatesList) {
						if (workspace.getFolderManager().isFileInWorkspace(template.getFile())) {
							template.getFile().delete();
						}
					}
				}
			} else {
				globalTemplatesList.forEach(
						e -> e.addDataModelEntry(baseType.getPluralName().toLowerCase(Locale.ENGLISH),
								baseTypeListMap.get(baseType).stream()
										.sorted(Comparator.comparing(ge -> ge.getModElement().getSortID()))
										.collect(Collectors.toList())));

				files.addAll(globalTemplatesList);
			}
		}

		return files;
	}

	public List<GeneratorTemplate> getModElementGlobalTemplatesList(ModElementType<?> type, boolean performFSTasks,
			AtomicInteger templateID) {
		return getGlobalTemplatesList(generatorConfiguration.getDefinitionsProvider().getModElementDefinition(type),
				performFSTasks, templateID);
	}

	public List<GeneratorTemplate> getGlobalTemplatesList(@Nullable Map<?, ?> map, boolean performFSTasks,
			AtomicInteger templateID) {
		if (map == null)
			return new ArrayList<>();

		Set<GeneratorTemplate> files = new HashSet<>();
		List<?> templates = (List<?>) map.get("global_templates");
		if (templates != null) {
			for (Object template : templates) {
				String name = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) template).get("name"));

				TemplateExpressionParser.Operator operator = TemplateExpressionParser.Operator.AND;
				Object conditionRaw = ((Map<?, ?>) template).get("condition");
				if (conditionRaw == null) {
					conditionRaw = ((Map<?, ?>) template).get("condition_any");
					operator = TemplateExpressionParser.Operator.OR;
				}

				if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw,
						workspace.getWorkspaceInfo(), operator)) {
					if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks)
						if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
							new File(name).delete(); // if template is skipped, we delete its potential file
						}
					continue;
				}

				GeneratorTemplate generatorTemplate = new GeneratorTemplate(new File(name),
						Integer.toString(templateID.get()) + ((Map<?, ?>) template).get("template"), template);

				// only keep the last template for given file
				files.remove(generatorTemplate);
				files.add(generatorTemplate);

				templateID.getAndIncrement();
			}
		}

		return new ArrayList<>(files);
	}

	public List<GeneratorTemplate> getModElementGeneratorTemplatesList(ModElement element) {
		return getModElementGeneratorTemplatesList(element, false, null);
	}

	public List<GeneratorTemplate> getModElementGeneratorTemplatesList(ModElement element,
			GeneratableElement generatableElement) {
		return getModElementGeneratorTemplatesList(element, false, generatableElement);
	}

	private List<GeneratorTemplate> getModElementGeneratorTemplatesList(ModElement element, boolean performFSTasks,
			GeneratableElement generatableElement) {
		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider().getModElementDefinition(element.getType());

		if (map == null) {
			LOG.info("Failed to load element definition for mod element type " + element.getType().getRegistryName());
			return null;
		}

		Set<GeneratorTemplate> files = new HashSet<>();
		List<?> templates = (List<?>) map.get("templates");
		if (templates != null) {
			int templateID = 0;
			for (Object template : templates) {
				String rawname = (String) ((Map<?, ?>) template).get("name");

				TemplateExpressionParser.Operator operator = TemplateExpressionParser.Operator.AND;
				Object conditionRaw = ((Map<?, ?>) template).get("condition");
				if (conditionRaw == null) {
					conditionRaw = ((Map<?, ?>) template).get("condition_any");
					operator = TemplateExpressionParser.Operator.OR;
				}

				if (conditionRaw != null || GeneratorTokens.containsVariableTokens(rawname)) {
					if (generatableElement == null) {
						generatableElement = element.getGeneratableElement();
						if (generatableElement == null && performFSTasks) {
							LOG.warn("Failed to load mod generatable element: " + element.getName()
									+ ". This means all templates will be generated (conditions ignored)");
						}
					}
				}

				String name = GeneratorTokens.replaceVariableTokens(generatableElement,
						GeneratorTokens.replaceTokens(workspace, rawname.replace("@NAME", element.getName())
								.replace("@registryname", element.getRegistryName())));

				if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw, generatableElement,
						operator)) {
					if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks)
						if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
							new File(name).delete(); // if template is skipped, we delete its potential file
						}
					continue;
				}

				// we check for potential excludes to be deleted, this is only called if condition above is passed
				String exclude = (String) ((Map<?, ?>) template).get("exclude");
				if (exclude != null && performFSTasks) {
					String excludename = GeneratorTokens.replaceTokens(workspace,
							exclude.replace("@NAME", element.getName())
									.replace("@registryname", element.getRegistryName()));
					File excludefile = new File(excludename);
					if (workspace.getFolderManager().isFileInWorkspace(excludefile))
						excludefile.delete();
				}

				GeneratorTemplate generatorTemplate = new GeneratorTemplate(new File(name),
						Integer.toString(templateID) + ((Map<?, ?>) template).get("template"), template);

				// only preserve the last template for given file
				files.remove(generatorTemplate);
				files.add(generatorTemplate);

				templateID++;
			}
		}

		// we add all list templates (if any) for given element to the list
		Objects.requireNonNull(getModElementListTemplates(element, performFSTasks, generatableElement))
				.forEach(e -> e.forEachTemplate(files::add, null));

		return new ArrayList<>(files);
	}

	public List<GeneratorTemplatesList> getModElementListTemplates(ModElement element) {
		return getModElementListTemplates(element, false, null);
	}

	public List<GeneratorTemplatesList> getModElementListTemplates(ModElement element,
			GeneratableElement generatableElement) {
		return getModElementListTemplates(element, false, generatableElement);
	}

	private List<GeneratorTemplatesList> getModElementListTemplates(ModElement element, boolean performFSTasks,
			GeneratableElement generatableElement) {

		if (generatableElement == null) {
			generatableElement = element.getGeneratableElement();
			if (generatableElement == null) { // we can't construct list data because we have nothing to process
				LOG.warn("Failed to load mod generatable element: " + element.getName()
						+ ". This means no list templates will be generated");
				return new ArrayList<>();
			}
		}

		Map<?, ?> map = generatorConfiguration.getDefinitionsProvider().getModElementDefinition(element.getType());
		if (map == null) {
			LOG.info("Failed to load element definition for mod element type " + element.getType().getRegistryName());
			return null;
		}

		Set<GeneratorTemplatesList> fileLists = new HashSet<>();

		List<?> templateLists = (List<?>) map.get("list_templates");
		if (templateLists != null) {
			int templateID = 0;
			int listID = 1;
			for (Object list : templateLists) {
				Map<GeneratorTemplate, List<Boolean>> files = new LinkedHashMap<>();
				String groupName = (String) Objects.requireNonNullElse(((Map<?, ?>) list).get("name"),
						"Group " + listID);
				Object listData = TemplateExpressionParser.processFTLExpression(this,
						(String) ((Map<?, ?>) list).get("listData"), generatableElement);
				List<?> templates = (List<?>) ((Map<?, ?>) list).get("forEach");
				// we check type of list data collection and convert it to a list if needed
				List<?> elements = new ArrayList<>();
				if (listData instanceof Map<?, ?> listMap)
					elements = new ArrayList<>(listMap.entrySet());
				else if (listData instanceof Collection<?> collection)
					elements = new ArrayList<>(collection);
				else if (listData instanceof Iterable<?> iterable) // fallback for the worst case
					elements = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
				if (templates != null) {
					for (Object template : templates) {
						String rawname = (String) ((Map<?, ?>) template).get("name");

						TemplateExpressionParser.Operator operator = TemplateExpressionParser.Operator.AND;
						Object conditionRaw = ((Map<?, ?>) template).get("condition");
						if (conditionRaw == null) {
							conditionRaw = ((Map<?, ?>) template).get("condition_any");
							operator = TemplateExpressionParser.Operator.OR;
						}

						// we store file generation conditions for current mod element
						List<Boolean> conditionChecks = new ArrayList<>();
						for (int i = 0; i < elements.size(); i++) {
							if (TemplateExpressionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw,
									elements.get(i), operator)) {
								conditionChecks.add(i, false);
								if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks) {
									String name = GeneratorTokens.replaceVariableTokens(generatableElement,
											elements.get(i), GeneratorTokens.replaceTokens(workspace,
													rawname.replace("@NAME", element.getName())
															.replace("@registryname", element.getRegistryName())
															.replace("@itemindex", Integer.toString(i))));
									if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
										new File(name).delete(); //if template is skipped, we delete its potential file
									}
								}
							} else {
								conditionChecks.add(i, true);
							}
						}
						if (!conditionChecks.contains(true) && performFSTasks)
							continue;

						// we check for potential excludes to be deleted
						// this is only called if condition above is passed
						String exclude = (String) ((Map<?, ?>) template).get("exclude");
						boolean doExclude = "true".equals(((Map<?, ?>) template).get("excludeIfAllPresent")) ?
								!conditionChecks.contains(false) :
								conditionChecks.contains(true);
						if (exclude != null && doExclude && performFSTasks) {
							String excludename = GeneratorTokens.replaceTokens(workspace,
									exclude.replace("@NAME", element.getName())
											.replace("@registryname", element.getRegistryName()));
							File excludefile = new File(excludename);
							if (workspace.getFolderManager().isFileInWorkspace(excludefile))
								excludefile.delete();
						}

						GeneratorTemplate generatorTemplate = new GeneratorTemplate(new File(rawname),
								Integer.toString(templateID) + ((Map<?, ?>) template).get("template"), template);

						// only preserve the last template for given file
						files.remove(generatorTemplate);
						files.put(generatorTemplate, Collections.unmodifiableList(conditionChecks));

						templateID++;
					}

					if (!elements.isEmpty() || !performFSTasks) {
						fileLists.add(new GeneratorTemplatesList(groupName, Collections.unmodifiableList(elements),
								generatableElement, Collections.unmodifiableMap(files)));
					}
				}
				listID++;
			}
		}

		return new ArrayList<>(fileLists);
	}

	public ModElement getModElementThisFileBelongsTo(File file) {
		if (!file.isFile() || !workspace.getFolderManager().isFileInWorkspace(file))
			return null;

		for (ModElement element : workspace.getModElements()) {
			if (generatorConfiguration.getGeneratorStats().getModElementTypeCoverageInfo().get(element.getType())
					== GeneratorStats.CoverageStatus.NONE)
				continue;

			try {
				List<File> modElementFiles = getModElementGeneratorTemplatesList(element).stream()
						.map(GeneratorTemplate::getFile).collect(Collectors.toList());
				if (FileIO.isFileOnFileList(modElementFiles, file))
					return element;

				// if this is GUI, we check for generated UI texture file too
				if (element.getType() == ModElementType.GUI) {
					File guiTextureFile = workspace.getFolderManager()
							.getTextureFile(element.getName().toLowerCase(Locale.ENGLISH), TextureType.SCREEN);
					if (guiTextureFile.getCanonicalPath().equals(file.getCanonicalPath()))
						return element;
				}
			} catch (Exception e) {
				LOG.warn("Failed to get list of mod element files for mod element " + element, e);
			}
		}

		return null;
	}

	private void generateFiles(Collection<GeneratorFile> generatorFiles, boolean formatAndOrganiseImports) {
		// first create Java files if they do not exist already
		// so the imports get properly organised in the next step
		if (formatAndOrganiseImports) {
			generatorFiles.forEach(generatorFile -> {
				if (workspace.getFolderManager().isFileInWorkspace(generatorFile.getFile())) {
					if (generatorFile.writer() == null || generatorFile.writer().equals("java"))
						if (!generatorFile.getFile().isFile())
							FileIO.touchFile(generatorFile.getFile());
				}
			});
		}

		generatorFiles.forEach(generatorFile -> {
			if (workspace.getFolderManager().isFileInWorkspace(generatorFile.getFile())) {
				if (generatorFile.writer() == null || generatorFile.writer().equals("java"))
					ClassWriter.writeClassToFileWithoutQueue(workspace, generatorFile.contents(), generatorFile.getFile(),
							formatAndOrganiseImports);
				else if (generatorFile.writer().equals("json"))
					JSONWriter.writeJSONToFileWithoutQueue(generatorFile.contents(), generatorFile.getFile());
				else if (generatorFile.writer().equals("file"))
					FileIO.writeStringToFile(generatorFile.contents(), generatorFile.getFile());
			}
		});
	}

	public void runResourceSetupTasks() {
		runSetupTasks(generatorConfiguration.getResourceSetupTasks());
	}

	public void runSetupTasks(List<?> setupTaks) {
		if (setupTaks != null) {
			setupTaks.forEach(task -> {
				String taskType = (String) ((Map<?, ?>) task).get("task");
				switch (taskType) {
				case "empty_dir" -> {
					String dir = (String) ((Map<?, ?>) task).get("dir");
					List<?> excludes_raw = (List<?>) ((Map<?, ?>) task).get("excludes");
					List<String> excludes = new ArrayList<>();
					if (excludes_raw != null) {
						for (Object o : excludes_raw)
							excludes.add(GeneratorTokens.replaceTokens(workspace, (String) o));
					}
					if (workspace.getFolderManager()
							.isFileInWorkspace(new File(GeneratorTokens.replaceTokens(workspace, dir)))) {
						FileIO.emptyDirectory(new File(GeneratorTokens.replaceTokens(workspace, dir)),
								excludes.toArray(new String[0]));
					}
				}
				case "sync_dir" -> {
					String from = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("to"));
					if (workspace.getFolderManager().isFileInWorkspace(new File(to))) {
						FileIO.emptyDirectory(
								new File(to)); // first delete existing contents of the destination directory
						FileIO.copyDirectory(new File(from), new File(to));
					}
				}
				case "copy_file" -> {
					String from = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("to"));
					if (workspace.getFolderManager().isFileInWorkspace(new File(to)) && new File(from).isFile())
						FileIO.copyFile(new File(from), new File(to));
				}
				case "copy_and_resize_image" -> {
					String from = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("from"));
					String to = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("to"));
					int w = Integer.parseInt(
							GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("width")));
					int h = Integer.parseInt(
							GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("height")));
					if (workspace.getFolderManager().isFileInWorkspace(new File(to)) && new File(from).isFile()) {
						try {
							BufferedImage image = ImageIO.read(new File(from));
							BufferedImage resized = ImageUtils.resize(image, w, h);
							ImageIO.write(resized, "png", new File(to));
						} catch (IOException e) {
							LOG.warn("Failed to read image file for resizing", e);
						}
					} else if (workspace.getFolderManager().isFileInWorkspace(new File(to))) {
						try {
							BufferedImage resized = ImageUtils.resize(UIRES.getBuiltIn("fallback").getImage(), w, h);
							ImageIO.write(resized, "png", new File(to));
						} catch (IOException e) {
							LOG.warn("Failed to read image file for resizing", e);
						}
					}
				}
				case "copy_models" -> {
					String to = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) task).get("to"));
					if (!workspace.getFolderManager().isFileInWorkspace(new File(to, "model.dummy")))
						break;

					List<Model> modelList = Model.getModels(workspace);

					String type = (String) ((Map<?, ?>) task).get("type");
					switch (type) {
					case "OBJ":
						for (Model model : modelList)
							if (model.getType() == Model.Type.OBJ)
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> FileIO.copyFile(f, new File(to, f.getName())));
						break;
					case "OBJ_inlinetextures":
						String prefix = GeneratorTokens.replaceTokens(workspace,
								(String) ((Map<?, ?>) task).get("prefix"));
						for (Model model : modelList)
							if (model.getType() == Model.Type.OBJ) {
								Arrays.stream(model.getFiles())
										.limit(2) // we only copy fist two elements, we skip last one which is texture mapping if it exists
										.forEach(f -> ModelUtils.copyOBJorMTLApplyTextureMapping(f,
												new File(to, f.getName()), model, prefix));
							}
						break;
					case "JSON":
						for (Model model : modelList)
							if (model.getType() == Model.Type.JSON)
								FileIO.copyFile(model.getFile(), new File(to, model.getFile().getName()));
						break;
					case "JSON_noinlinetextures":
						for (Model model : modelList)
							if (model.getType() == Model.Type.JSON) {
								String jsonorig = FileIO.readFileToString(model.getFile());
								String notextures = ModelUtils.removeInlineTexturesSectionFromJSONModel(jsonorig);
								FileIO.writeStringToFile(notextures, new File(to, model.getFile().getName()));
							}
						break;
					case "JAVA_viatemplate":
						String template = GeneratorTokens.replaceTokens(workspace,
								(String) ((Map<?, ?>) task).get("template"));
						for (Model model : modelList)
							if (model.getType() == Model.Type.JAVA) {
								String modelCode = FileIO.readFileToString(model.getFile());
								try {
									modelCode = getTemplateGeneratorFromName("templates").generateFromTemplate(template,
											new HashMap<>(
													Map.of("modelname", model.getReadableName(), "model", modelCode,
															"modelregistryname",
															RegistryNameFixer.fromCamelCase(model.getReadableName()))));
								} catch (TemplateGeneratorException e) {
									e.printStackTrace();
								}
								ClassWriter.writeClassToFileWithoutQueue(workspace, modelCode,
										new File(to, model.getReadableName() + ".java"), true);
							}
						break;
					}
				}
				}
			});
		}
	}

	@Nullable public ProjectConnection getGradleProjectConnection() {
		if (gradleProjectConnection == null) {
			try {
				gradleProjectConnection = GradleConnector.newConnector()
						.forProjectDirectory(workspace.getWorkspaceFolder())
						.useGradleUserHomeDir(UserFolderManager.getGradleHome()).connect();
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
		if (gradleProjectConnection != null)
			gradleProjectConnection.close();
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
