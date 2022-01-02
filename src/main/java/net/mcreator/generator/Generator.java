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
import net.mcreator.generator.template.TemplateConditionParser;
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

public class Generator implements IGenerator, Closeable {

	public static final Map<String, GeneratorConfiguration> GENERATOR_CACHE = Collections.synchronizedMap(
			new LinkedHashMap<>());

	protected final Logger LOG;
	private final String generatorName;
	private final GeneratorConfiguration generatorConfiguration;

	private final TemplateGenerator templateGenerator;
	private final TemplateGenerator procedureGenerator;
	private final TemplateGenerator triggerGenerator;
	private final TemplateGenerator aitaskGenerator;
	private final TemplateGenerator jsonTriggerGenerator;

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

		this.templateGenerator = new TemplateGenerator(generatorConfiguration.getTemplateGeneratorConfiguration(),
				this);
		this.procedureGenerator = new TemplateGenerator(generatorConfiguration.getProcedureGeneratorConfiguration(),
				this);
		this.triggerGenerator = new TemplateGenerator(generatorConfiguration.getTriggerGeneratorConfiguration(), this);
		this.jsonTriggerGenerator = new TemplateGenerator(generatorConfiguration.getJSONTriggerGeneratorConfiguration(),
				this);
		this.aitaskGenerator = new TemplateGenerator(generatorConfiguration.getAITaskGeneratorConfiguration(), this);

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

	public TemplateGenerator getProcedureGenerator() {
		return procedureGenerator;
	}

	public TemplateGenerator getTriggerGenerator() {
		return triggerGenerator;
	}

	public TemplateGenerator getAITaskGenerator() {
		return aitaskGenerator;
	}

	public TemplateGenerator getJSONTriggerGenerator() {
		return jsonTriggerGenerator;
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
						String code = templateGenerator.generateBaseFromTemplate(templateFileName, dataModel);
						return new GeneratorFile(code, generatorTemplate.getFile(),
								(String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("writer"));
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
			LanguageFilesGenerator.generateLanguageFiles(this, workspace,
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

				String code = templateGenerator.generateElementFromTemplate(element, templateFileName, dataModel,
						element.getAdditionalTemplateData());

				GeneratorFile generatorFile = new GeneratorFile(code, generatorTemplate.getFile(),
						(String) ((Map<?, ?>) generatorTemplate.getTemplateData()).get("writer"));

				// only preserve the last instance of template for a file
				generatorFiles.remove(generatorFile);
				generatorFiles.add(generatorFile);
			}
		}

		if (performFSTasks) {
			generateFiles(generatorFiles, formatAndOrganiseImports);

			// extract all localization keys
			List<?> localizationkeys = (List<?>) map.get("localizationkeys");
			if (localizationkeys != null) {
				for (Object template : localizationkeys) {
					String key = (String) ((Map<?, ?>) template).get("key");
					String mapto = (String) ((Map<?, ?>) template).get("mapto");
					key = GeneratorTokens.replaceTokens(workspace,
							key.replace("@NAME", element.getModElement().getName())
									.replace("@modid", workspace.getWorkspaceSettings().getModID())
									.replace("@registryname", element.getModElement().getRegistryName()));
					try {
						String value = (String) element.getClass().getField(mapto.trim()).get(element);

						String suffix = (String) ((Map<?, ?>) template).get("suffix");
						if (suffix != null)
							value += suffix;

						String prefix = (String) ((Map<?, ?>) template).get("prefix");
						if (prefix != null)
							value = prefix + value;

						workspace.setLocalization(key, value);
					} catch (IllegalAccessException | NoSuchFieldException e) {
						LOG.error(e.getMessage(), e);
						LOG.error("[" + generatorName + "] " + e.getMessage());
					}
				}
			}

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

		Objects.requireNonNull(getModElementGeneratorTemplatesList(element, true, null)).stream()
				.map(GeneratorTemplate::getFile).forEach(File::delete);

		// delete all localization keys
		List<?> localizationkeys = (List<?>) map.get("localizationkeys");
		if (localizationkeys != null) {
			for (Object template : localizationkeys) {
				String key = (String) ((Map<?, ?>) template).get("key");
				key = GeneratorTokens.replaceTokens(workspace,
						key.replace("@NAME", element.getName()).replace("@registryname", element.getRegistryName()));
				workspace.removeLocalizationEntryByKey(key);
			}
		}
	}

	public List<GeneratorTemplate> getModBaseGeneratorTemplatesList(boolean performFSTasks) {
		List<GeneratorTemplate> files = new ArrayList<>();
		AtomicInteger templateID = new AtomicInteger();

		List<?> templates = generatorConfiguration.getBaseTemplates();
		for (Object template : templates) {
			TemplateConditionParser.Operator operator = TemplateConditionParser.Operator.AND;
			Object conditionRaw = ((Map<?, ?>) template).get("condition");
			if (conditionRaw == null) {
				conditionRaw = ((Map<?, ?>) template).get("condition_any");
				operator = TemplateConditionParser.Operator.OR;
			}

			String name = GeneratorTokens.replaceTokens(workspace, (String) ((Map<?, ?>) template).get("name"));

			if (TemplateConditionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw,
					workspace.getWorkspaceInfo(), operator)) {
				if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks)
					if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
						new File(name).delete(); // if template is skipped, we delete its potential file
					}
				continue;
			}

			files.add(new GeneratorTemplate(new File(name),
					Integer.toString(templateID.get()) + ((Map<?, ?>) template).get("template"), true, template));

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

				TemplateConditionParser.Operator operator = TemplateConditionParser.Operator.AND;
				Object conditionRaw = ((Map<?, ?>) template).get("condition");
				if (conditionRaw == null) {
					conditionRaw = ((Map<?, ?>) template).get("condition_any");
					operator = TemplateConditionParser.Operator.OR;
				}

				if (TemplateConditionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw,
						workspace.getWorkspaceInfo(), operator)) {
					if (((Map<?, ?>) template).get("deleteWhenConditionFalse") != null && performFSTasks)
						if (workspace.getFolderManager().isFileInWorkspace(new File(name))) {
							new File(name).delete(); // if template is skipped, we delete its potential file
						}
					continue;
				}

				GeneratorTemplate generatorTemplate = new GeneratorTemplate(new File(name),
						Integer.toString(templateID.get()) + ((Map<?, ?>) template).get("template"), true, template);

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

				TemplateConditionParser.Operator operator = TemplateConditionParser.Operator.AND;
				Object conditionRaw = ((Map<?, ?>) template).get("condition");
				if (conditionRaw == null) {
					conditionRaw = ((Map<?, ?>) template).get("condition_any");
					operator = TemplateConditionParser.Operator.OR;
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

				if (TemplateConditionParser.shouldSkipTemplateBasedOnCondition(this, conditionRaw, generatableElement,
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
						Integer.toString(templateID) + ((Map<?, ?>) template).get("template"), false, template);

				// only preserve the last template for given file
				files.remove(generatorTemplate);
				files.add(generatorTemplate);

				templateID++;
			}
		}

		return new ArrayList<>(files);
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
							.getOtherTextureFile(element.getName().toLowerCase(Locale.ENGLISH));
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
				if (workspace.getFolderManager().isFileInWorkspace(generatorFile.file())) {
					if (generatorFile.writer() == null || generatorFile.writer().equals("java"))
						if (!generatorFile.file().isFile())
							FileIO.writeStringToFile("", generatorFile.file());
				}
			});
		}

		generatorFiles.forEach(generatorFile -> {
			if (workspace.getFolderManager().isFileInWorkspace(generatorFile.file())) {
				if (generatorFile.writer() == null || generatorFile.writer().equals("java"))
					ClassWriter.writeClassToFileWithoutQueue(workspace, generatorFile.contents(), generatorFile.file(),
							formatAndOrganiseImports);
				else if (generatorFile.writer().equals("json"))
					JSONWriter.writeJSONToFileWithoutQueue(generatorFile.contents(), generatorFile.file());
				else if (generatorFile.writer().equals("file"))
					FileIO.writeStringToFile(generatorFile.contents(), generatorFile.file());
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
									modelCode = templateGenerator.generateFromTemplate(template, new HashMap<>(
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
