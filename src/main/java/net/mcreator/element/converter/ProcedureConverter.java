/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.element.converter;

import com.google.gson.JsonElement;
import net.mcreator.blockly.data.BlocklyLoader;
import net.mcreator.blockly.java.BlocklyToProcedure;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.types.Procedure;
import net.mcreator.generator.blockly.BlocklyBlockCodeGenerator;
import net.mcreator.generator.blockly.OutputBlockCodeGenerator;
import net.mcreator.generator.blockly.ProceduralBlockCodeGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.blockly.BlocklyEditorType;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Locale;

public abstract class ProcedureConverter implements IConverter {

	private static final Logger LOG = LogManager.getLogger(ProcedureConverter.class);

	private boolean dependencies_changed = false;

	@Override
	public final GeneratableElement convert(Workspace workspace, GeneratableElement input,
			JsonElement jsonElementInput) {
		Procedure procedure = (Procedure) input;

		try {
			procedure.procedurexml = fixXML(procedure, procedure.procedurexml);

			if (dependencies_changed)
				refreshDependencies(procedure);
		} catch (Exception e) {
			LOG.warn("Failed to convert procedure block setup for {}", input.getModElement().getName(), e);
		}

		return procedure;
	}

	protected final void reportDependenciesChanged() {
		dependencies_changed = true;
	}

	abstract protected String fixXML(Procedure procedure, String xml) throws Exception;

	private void refreshDependencies(Procedure procedure) throws TemplateGeneratorException {
		// This will safely parse the blockly XML at blockly block definition only and will
		// not generate any code, meaning it can't cause recursive loading of GE/MEs or generating
		// of procedure code that is not usable yet due to more conversions pending after this one
		BlocklyBlockCodeGenerator blocklyBlockCodeGenerator = new BlocklyBlockCodeGenerator(
				BlocklyLoader.INSTANCE.getBlockLoader(BlocklyEditorType.PROCEDURE).getDefinedBlocks(),
				procedure.getModElement().getGeneratorStats().getBlocklyBlocks(BlocklyEditorType.PROCEDURE));
		BlocklyToProcedure blocklyToJava = new BlocklyToProcedure(procedure.getModElement().getWorkspace(),
				procedure.getModElement(), procedure.procedurexml, procedure.getModElement().getGenerator()
				.getTemplateGeneratorFromName(BlocklyEditorType.PROCEDURE.registryName()),
				new ProceduralBlockCodeGenerator(blocklyBlockCodeGenerator),
				new OutputBlockCodeGenerator(blocklyBlockCodeGenerator));
		procedure.getModElement().clearMetadata().putMetadata("dependencies", blocklyToJava.getDependencies())
				.putMetadata("return_type", blocklyToJava.getReturnType() == null ?
						null :
						blocklyToJava.getReturnType().getName().toLowerCase(Locale.ENGLISH));
	}

}
