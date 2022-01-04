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
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BlocklyToCode implements IGeneratorProvider {

	private final StringBuilder code;
	private final List<BlocklyCompileNote> compile_notes;
	private final Set<Dependency> dependencies;

	@Nullable private final TemplateGenerator templateGenerator;
	private final Workspace workspace;

	protected List<IBlockGenerator> blockGenerators;

	protected String lastProceduralBlockType = null;

	private final Stack<DependencyProviderInput> dependencyProviderInputStack = new Stack<>();

	public BlocklyToCode(Workspace workspace, @Nullable TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) {
		this.templateGenerator = templateGenerator;
		this.workspace = workspace;

		code = new StringBuilder();
		compile_notes = new ArrayList<>();
		dependencies = new HashSet<>();

		blockGenerators = new ArrayList<>();

		// add external generators provided by user
		blockGenerators.addAll(Arrays.asList(externalGenerators));
	}

	public final String getGeneratedCode() {
		return code.toString();
	}

	public final List<BlocklyCompileNote> getCompileNotes() {
		return compile_notes;
	}

	public final List<Dependency> getDependencies() {
		return dependencies.stream().sorted()
				// this is here for compatibility with workspaces before 2020.4
				.map(e -> {
					if (e.getRawType().equals("int"))
						return new Dependency(e.getName(), "number");
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
		compile_notes.add(compileNote);
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

	public final void processBlockProcedure(List<Element> blocks) throws TemplateGeneratorException {
		for (Element block : blocks) {
			String type = block.getAttribute("type");
			boolean generated = false;
			for (IBlockGenerator generator : blockGenerators) {
				if (generator.getBlockType() == IBlockGenerator.BlockType.PROCEDURAL && Arrays.asList(
						generator.getSupportedBlocks()).contains(type)) {
					int compile_notes_num = compile_notes.size();
					generator.generateBlock(this, block);
					if (compile_notes_num == compile_notes.size()) // no errors in generation
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

	public final void processOutputBlock(Element condition) throws TemplateGeneratorException {
		List<Element> conditionBlocks = XMLUtil.getChildrenWithName(condition, "block", "shadow");
		if (conditionBlocks.size() < 1)
			return;
		Element block = conditionBlocks.get(0);
		String type = block.getAttribute("type");

		boolean generated = false;
		for (IBlockGenerator generator : blockGenerators) {
			if (generator.getBlockType() == IBlockGenerator.BlockType.OUTPUT && Arrays.asList(
					generator.getSupportedBlocks()).contains(type)) {
				generator.generateBlock(this, block);

				generated = true;
				break;
			}
		}

		if (!generated) {
			addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.warnings.unknown_block_type.remove", type)));
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

}
