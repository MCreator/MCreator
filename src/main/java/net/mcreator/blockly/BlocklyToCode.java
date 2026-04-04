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

package net.mcreator.blockly;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.blockly.data.DependencyProviderInput;
import net.mcreator.blockly.data.StatementInput;
import net.mcreator.blockly.java.ProcedureCodeOptimizer;
import net.mcreator.generator.IGeneratorProvider;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import net.mcreator.workspace.elements.ModElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BlocklyToCode implements IGeneratorProvider {

	private static final Logger LOG = LogManager.getLogger(BlocklyToCode.class);

	private final StringBuilder code;
	private final List<BlocklyCompileNote> compileNotes;
	private final Set<Dependency> dependencies;

	private final ModElement parent;

	@Nullable private final TemplateGenerator templateGenerator;
	private final Workspace workspace;

	protected final List<IBlockGenerator> blockGenerators;

	protected final BlocklyEditorType editorType;

	protected String lastProceduralBlockType = null;

	private final Stack<DependencyProviderInput> dependencyProviderInputStack = new Stack<>();

	private final Set<String> usedBlocks = new HashSet<>();
	private final Set<String> usedTemplates = new LinkedHashSet<>(), generatedTemplates = new HashSet<>();

	// These variables hold the current template for the head/tail of the currently processed block
	private String headSection = "";
	private String tailSection = "";

	private int blockCount = 0;

	/**
	 * @param workspace          <p>The {@link Workspace} executing the code</p>
	 * @param editorType         <p>Blockly editor type</p>
	 * @param templateGenerator  <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 * @param externalGenerators <p>Define which block types (procedural and/or output) are supported inside this Blockly editor</p>
	 */
	public BlocklyToCode(Workspace workspace, ModElement parent, BlocklyEditorType editorType,
			@Nullable TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators) {
		this.editorType = editorType;
		this.templateGenerator = templateGenerator;
		this.workspace = workspace;
		this.parent = parent;

		code = new StringBuilder();
		compileNotes = new ArrayList<>();
		dependencies = new HashSet<>();

		blockGenerators = new ArrayList<>();

		// add external generators provided by user
		blockGenerators.addAll(Arrays.asList(externalGenerators));
	}

	/**
	 * @param workspace         <p>The {@link Workspace} executing the code</p>
	 * @param blocklyEditorType <p>Blockly editor type</p>
	 * @param sourceXML         <p>The XML code used by Blockly</p>
	 * @param templateGenerator <p>The folder location in each {@link net.mcreator.generator.Generator} containing the code template files<p>
	 */
	public BlocklyToCode(Workspace workspace, ModElement parent, BlocklyEditorType blocklyEditorType, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		this(workspace, parent, blocklyEditorType, templateGenerator, externalGenerators);

		beforeGenerate();

		if (sourceXML != null && !sourceXML.isBlank()) {
			try {
				final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, blocklyEditorType.startBlockName());

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				// we execute extra actions needed before placing blocks
				preBlocksPlacement(doc, start_block);

				// find all blocks placed under start block
				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				processBlockProcedure(base_blocks);

				// we execute extra actions needed after blocks are placed
				postBlocksPlacement(doc, start_block, base_blocks);
			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error("Failed to parse Blockly XML", e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.exception_compiling", e.getMessage())));
			}
		} else {
			addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR, L10N.t("blockly.errors.editor_not_ready")));
		}

		if (this.getBlockCount() > 4000) {
			addCompileNote(
					new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING, L10N.t("blockly.errors.too_many_blocks")));
		}
	}

	/**
	 * <p>This method contains the code needing to be executed before blocks are placed.</p>
	 *
	 * @param doc        Blockly XML document
	 * @param startBlock The basic block of the editor used to get other blocks.
	 */
	protected void preBlocksPlacement(Document doc, Element startBlock) throws TemplateGeneratorException {}

	/**
	 * <p>This method contains the code needing to be executed after blocks are placed.</p>
	 *
	 * @param doc        Blockly XML document
	 * @param startBlock The basic block of the editor used to get other blocks.
	 * @param baseBlocks A list of all blocks placed under start block.
	 */
	protected void postBlocksPlacement(Document doc, Element startBlock, List<Element> baseBlocks) {}

	/**
	 * <p>This method is executed after the constructor is called, before the code is generated</p>
	 */
	protected void beforeGenerate() {}

	public final String getGeneratedCode() {
		return code.toString();
	}

	public final String getExtraTemplatesCode() throws TemplateGeneratorException {
		StringBuilder code = new StringBuilder();
		if (templateGenerator != null) {
			while (!usedTemplates.isEmpty()) {
				for (String template : List.copyOf(usedTemplates)) {
					generatedTemplates.add(template);
					usedTemplates.remove(template);
					Map<String, Object> dataModel = new HashMap<>();
					dataModel.put("parent", parent);
					dataModel.put("addTemplate", new ExtraTemplatesLinker(this));
					code.append(templateGenerator.generateFromTemplate(template, dataModel));
				}
			}
		}
		return code.toString();
	}

	public final List<BlocklyCompileNote> getCompileNotes() {
		return compileNotes;
	}

	public ModElement getParent() {
		return parent;
	}

	public BlocklyEditorType getEditorType() {
		return editorType;
	}

	public final List<Dependency> getDependencies() {
		return dependencies.stream().sorted()
				// this is here for compatibility with workspaces before 2020.4
				.map(e -> {
					if (e.getRawType().equals("int"))
						return new Dependency(e.name(), "number");
					return e;
				}).collect(Collectors.toList());
	}

	public final BlocklyToCode append(Object data) {
		code.append(data);
		return this;
	}

	public final void clearCodeGeneratorBuffer() {
		code.setLength(0);
	}

	public final void addCompileNote(BlocklyCompileNote compileNote) {
		compileNotes.add(compileNote);
	}

	public final void addDependency(Dependency dependency) {
		// check if used by statement input and skip in this case
		if (checkIfDepProviderInputsProvide(dependency))
			return;

		dependencies.add(dependency);
	}

	@Nullable public final TemplateGenerator getTemplateGenerator() {
		return templateGenerator;
	}

	@Override public final @Nonnull Workspace getWorkspace() {
		return workspace;
	}

	public final void pushDepProviderInputStack(DependencyProviderInput statementInput) {
		dependencyProviderInputStack.push(statementInput);
	}

	public final void popDepProviderInputStack() {
		dependencyProviderInputStack.pop();
	}

	public boolean checkIfDepProviderInputsProvide(Dependency dependency) {
		for (var dependencyProviderInput : dependencyProviderInputStack) {
			if (dependencyProviderInput.provides != null && dependencyProviderInput.provides.contains(dependency))
				return true;
		}

		return false;
	}

	public List<StatementInput> getStatementInputsMatching(Predicate<StatementInput> predicate) {
		return this.dependencyProviderInputStack.stream().filter(i -> i instanceof StatementInput)
				.map(i -> (StatementInput) i).filter(predicate).collect(Collectors.toList());
	}

	public final void addTemplate(String template) {
		if (!generatedTemplates.contains(template))
			usedTemplates.add(template);
	}

	public final void processBlockProcedure(List<Element> blocks) throws TemplateGeneratorException {
		for (Element block : blocks) {
			blockCount++;

			String type = block.getAttribute("type");

			if (block.getAttribute("disabled").equals("true")) { // Skip disabled blocks
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
						L10N.t("blockly.warnings.disabled_block_type.skip", type)));
			} else {
				boolean generated = false;
				for (IBlockGenerator generator : blockGenerators) {
					if (generator.getBlockType() == IBlockGenerator.BlockType.PROCEDURAL && Arrays.asList(
							generator.getSupportedBlocks()).contains(type)) {
						try {
							// if the current procedural block is not of type ProceduralBlockCodeGenerator, append tail,
							// because the following block cannot be part of the current head/tail sections
							if (!(generator instanceof IBlockGeneratorWithSections)) {
								IBlockGeneratorWithSections.terminateSections(this);
							}
							generator.generateBlock(this, block);
						} catch (TemplateGeneratorException e) {
							throw e;
						} catch (Exception e) {
							// Any other exception that can occur during block generation
							throw new TemplateGeneratorException(
									"Uncaught exception while generating block of type: " + type, e);
						}

						usedBlocks.add(type);

						lastProceduralBlockType = type; // update last block type generated

						generated = true;
						break;
					}
				}

				if (!generated) {
					addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.WARNING,
							L10N.t("blockly.warnings.unknown_block_type.skip", type)));
				}
			}
		}

		// Append the last tail at the end of the processing, and clear sections data in case the method will be called again
		append(getTailSection());
		clearSections();
	}

	public final void processOutputBlock(Element condition) throws TemplateGeneratorException {
		blockCount++;

		List<Element> conditionBlocks = XMLUtil.getChildrenWithName(condition, "block", "shadow");
		if (conditionBlocks.isEmpty())
			return;
		Element block = conditionBlocks.getFirst();
		String type = block.getAttribute("type");

		if (block.getAttribute("disabled").equals("true")) { // Add compile error if block is disabled
			addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.disabled_block_type.remove", type)));
		} else {
			boolean generated = false;
			for (IBlockGenerator generator : blockGenerators) {
				if (generator.getBlockType() == IBlockGenerator.BlockType.OUTPUT && Arrays.asList(
						generator.getSupportedBlocks()).contains(type)) {
					try {
						generator.generateBlock(this, block);
					} catch (TemplateGeneratorException e) {
						throw e;
					} catch (Exception e) {
						// Any other exception that can occur during block generation
						throw new TemplateGeneratorException(
								"Uncaught exception while generating block of type: " + type, e);
					}

					usedBlocks.add(type);

					generated = true;
					break;
				}
			}

			if (!generated) {
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.unknown_block_type.remove", type)));
			}
		}
	}

	public static String directProcessOutputBlock(BlocklyToCode master, Element element)
			throws TemplateGeneratorException {
		// we do a little hack to get the code of the input only
		String originalMasterCode = master.getGeneratedCode();
		master.clearCodeGeneratorBuffer(); // we clear all the existing code
		master.processOutputBlock(element);
		String generatedCode = master.getGeneratedCode(); // get the generated code
		master.clearCodeGeneratorBuffer(); // we clear the master again to remove the code we just generated
		master.append(originalMasterCode); // set the master code to the original code
		return generatedCode;
	}

	public static String directProcessStatementBlock(BlocklyToCode master, Element element)
			throws TemplateGeneratorException {
		// first terminate any potential code sections as they in most cases don't work right nested in statements
		IBlockGeneratorWithSections.terminateSections(master);

		// we do a little hack to get the code of the input only
		String originalMasterCode = master.getGeneratedCode();
		master.clearCodeGeneratorBuffer(); // we clear all the existing code
		List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithBlock(element);
		master.processBlockProcedure(base_blocks);
		String generatedCode = master.getGeneratedCode(); // get the generated code
		master.clearCodeGeneratorBuffer(); // we clear the master again to remove the code we just generated
		master.append(originalMasterCode); // set the master code to the original code
		return generatedCode;
	}

	/**
	 * Helper method to process an output block and remove surrounding parentheses if possible
	 *
	 * @param element The element to process
	 * @throws TemplateGeneratorException If the template can't be generated
	 */
	public final void processOutputBlockWithoutParentheses(Element element) throws TemplateGeneratorException {
		String code = directProcessOutputBlock(this, element);
		this.append(ProcedureCodeOptimizer.removeParentheses(code));
	}

	/**
	 * Helper method to process an output block and remove surrounding parentheses if possible
	 *
	 * @param element   The element to process
	 * @param blacklist The characters that can't be contained at the top nesting level when optimizing the element
	 * @throws TemplateGeneratorException If the template can't be generated
	 */
	public final void processOutputBlockWithoutParentheses(Element element, String blacklist)
			throws TemplateGeneratorException {
		String code = directProcessOutputBlock(this, element);
		this.append(ProcedureCodeOptimizer.removeParentheses(code, blacklist));
	}

	/**
	 * Helper method to get the code of an output block and remove surrounding parentheses if possible
	 *
	 * @param element The element to process
	 * @return The generated code of the element with parentheses optimization
	 * @throws TemplateGeneratorException If the template can't be generated
	 */
	public String directProcessOutputBlockWithoutParentheses(Element element) throws TemplateGeneratorException {
		return ProcedureCodeOptimizer.removeParentheses(directProcessOutputBlock(this, element));
	}

	/**
	 * Helper method to process an output block and cast to int when needed
	 *
	 * @param element The element to process
	 * @throws TemplateGeneratorException If the template can't be generated
	 */
	public final void processOutputBlockToInt(Element element) throws TemplateGeneratorException {
		String code = directProcessOutputBlock(this, element);
		this.append(ProcedureCodeOptimizer.toInt(code));
	}

	/**
	 * This method returns collection of machine names of all blocks that are present in the provided Blockly arrangement
	 *
	 * @return Unmodifiable collection of machine names of all blocks that are present in the provided Blockly arrangement
	 */
	public Collection<String> getUsedBlocks() {
		return Collections.unmodifiableSet(usedBlocks);
	}

	public void setHeadSection(String headSection) {
		this.headSection = headSection;
	}

	public void setTailSection(String tailSection) {
		this.tailSection = tailSection;
	}

	public void clearSections() {
		this.headSection = "";
		this.tailSection = "";
	}

	public String getHeadSection() {
		return headSection;
	}

	public String getTailSection() {
		return tailSection;
	}

	/**
	 * Returns the count of blocks currently processed from the block arrangement.
	 *
	 * @return The total number of blocks.
	 */
	public int getBlockCount() {
		return blockCount;
	}

}
