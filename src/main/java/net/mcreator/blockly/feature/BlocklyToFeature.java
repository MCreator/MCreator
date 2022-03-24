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

import net.mcreator.blockly.BlocklyCompileNote;
import net.mcreator.blockly.IBlockGenerator;
import net.mcreator.blockly.java.BlocklyToJava;
import net.mcreator.generator.template.TemplateGenerator;
import net.mcreator.generator.template.TemplateGeneratorException;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.XMLUtil;
import net.mcreator.workspace.Workspace;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.List;

public class BlocklyToFeature extends BlocklyToJava {
	protected final Logger LOG = LogManager.getLogger("Blockly2Feature");

	private StringBuilder featureConfigurationCode;
	private String featureType;

	public BlocklyToFeature(Workspace workspace, String sourceXML, TemplateGenerator templateGenerator,
			IBlockGenerator... externalGenerators) throws TemplateGeneratorException {
		super(workspace, "feature_container", sourceXML, templateGenerator,
				externalGenerators);
	}

	@Override public void preInitialization() {
		super.preInitialization();
		featureConfigurationCode = new StringBuilder();
		featureType = "";
	}

	@Override public void preBlocksPlacement(Document doc, Element startBlock) throws TemplateGeneratorException {
		super.preBlocksPlacement(doc, startBlock);

		// Add the feature to the feature code
		Element feature = XMLUtil.getFirstChildrenWithName(startBlock, "value");
		if (feature != null) {
			featureConfigurationCode.append(directProcessOutputBlock(this, feature));
			Element featureBlock = XMLUtil.getFirstChildrenWithName(feature, "block");
			if (featureBlock != null)
				this.featureType = featureBlock.getAttribute("type");
		} else
			addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.features.missing_feature")));
	}

	@Override public void postBlocksPlacement(Document doc, Element startBlock, List<Element> baseBlocks) {
		if (baseBlocks.isEmpty())
			addCompileNote(new BlocklyCompileNote(BlocklyCompileNote.Type.ERROR,
					L10N.t("blockly.errors.features.missing_placement")));
	}

	public final String getFeatureConfigurationCode() {
		return featureConfigurationCode.toString();
	}

	public String getFeatureType() {
		return featureType;
	}
}
