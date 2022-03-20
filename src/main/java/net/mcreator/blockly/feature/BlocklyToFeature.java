/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

package net.mcreator.blockly.feature;

import net.mcreator.blockly.BlocklyBlockUtil;
import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.BlocklyToCode;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.feature.blocks.SimpleBlockStateProvider;
import net.mcreator.generator.Generator;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;

public class BlocklyToFeature extends BlocklyToCode {
	protected final Logger LOG = LogManager.getLogger("Blockly2Feature");

	private final StringBuilder featureCode;

	/**
	 * @param workspace          <p>The {@link Workspace} executing the code</p>
	 * @param firstBlockName     <p>The name of the start block</p>
	 * @param sourceXML          <p>The XML code used by Blockly</p>
	 * @param templateGenerator  <p>The folder location in each {@link Generator} containing the code template files<p>
	 */
	public BlocklyToFeature(Workspace workspace, String firstBlockName, String sourceXML,
			TemplateGenerator templateGenerator, IBlockGenerator... externalGenerators)
			throws TemplateGeneratorException {
		super(workspace, templateGenerator, externalGenerators);
		featureCode = new StringBuilder();

		blockGenerators.add(new SimpleBlockStateProvider());
		if (sourceXML != null) {
			try {
				final Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
						.parse(new InputSource(new StringReader(sourceXML)));
				doc.getDocumentElement().normalize();

				Element start_block = BlocklyBlockUtil.getStartBlock(doc, firstBlockName);

				// if there is no start block, we return empty string
				if (start_block == null)
					throw new ParseException("Could not find start block!", -1);

				// Add the feature to the feature code
				Element feature = XMLUtil.getFirstChildrenWithName(start_block, "value");
				if (feature != null)
					featureCode.append(directProcessOutputBlock(this, feature));
				else
					addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.features.missing_feature")));

				List<Element> base_blocks = BlocklyBlockUtil.getBlockProcedureStartingWithNext(start_block);
				if (base_blocks.isEmpty())
					addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
							L10N.t("blockly.errors.features.missing_placement")));
				processBlockProcedure(base_blocks);
			} catch (TemplateGeneratorException e) {
				throw e;
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
						L10N.t("blockly.errors.exception_compiling", e.getMessage())));
			}
		}
	}

	public final String getFeatureCode() {
		return featureCode.toString();
	}
}
