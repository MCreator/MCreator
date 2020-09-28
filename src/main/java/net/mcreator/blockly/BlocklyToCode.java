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
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Element;

import java.util.*;
import java.util.stream.Collectors;

public abstract class BlocklyToCode {

	private final StringBuilder code;
	private final List<BlocklyCompileNote> compile_notes;
	private final Set<Dependency> dependencies;

	@Nullable private final TemplateGenerator templateGenerator;
	private final Workspace workspace;

	protected List<IBlockGenerator> blockGenerators;

	protected String lastProceduralBlockType = null;

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

	public String getGeneratedCode() {
		return code.toString();
	}

	public List<BlocklyCompileNote> getCompileNotes() {
		return compile_notes;
	}

	public List<Dependency> getDependencies() {
		return dependencies.stream().sorted()
				// this is here for compatibility with workspaces before 2020.4
				.map(e -> {
					if (e.getRawType().equals("int"))
						return new Dependency(e.getName(), "number");
					return e;
				}).collect(Collectors.toList());
	}

	public BlocklyToCode append(Object data) {
		code.append(data);
		return this;
	}

	public void clearCodeGeneratorBuffer() {
		code.setLength(0);
	}

	public void addCompileNote(BlocklyCompileNote compileNote) {
		compile_notes.add(compileNote);
	}

	public void addDependency(Dependency dependency) {
		dependencies.add(dependency);
	}

	@Nullable public TemplateGenerator getTemplateGenerator() {
		return templateGenerator;
	}

	public Workspace getWorkspace() {
		return workspace;
	}

	public void processBlockProcedure(List<Element> blocks) throws TemplateGeneratorException {
		for (Element block : blocks) {
			String type = block.getAttribute("type");
			boolean generated = false;
			for (IBlockGenerator generator : blockGenerators) {
				if (generator.getBlockType() == IBlockGenerator.BlockType.PROCEDURAL && Arrays
						.asList(generator.getSupportedBlocks()).contains(type)) {
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
						"Unknown block type " + type + ". Skipping this block."));
			}
		}
	}

	public void processOutputBlock(Element condition) throws TemplateGeneratorException {
		List<Element> conditionBlocks = XMLUtil.getChildrenWithName(condition, "block", "shadow");
		if (conditionBlocks.size() < 1)
			return;
		Element block = conditionBlocks.get(0);
		String type = block.getAttribute("type");

		boolean generated = false;
		for (IBlockGenerator generator : blockGenerators) {
			if (generator.getBlockType() == IBlockGenerator.BlockType.OUTPUT && Arrays
					.asList(generator.getSupportedBlocks()).contains(type)) {
				generator.generateBlock(this, block);

				generated = true;
				break;
			}
		}

		if (!generated) {
			addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					"Unknown block type " + type + ". Remove this block!"));
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

}
