/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.block;

import net.mcreator.element.types.Block;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.entries.JSimpleListEntry;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.BlockTexturesSelector;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.ValidationResult;
import net.mcreator.ui.validation.Validator;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static net.mcreator.ui.modgui.BlockGUI.normal;

public class JBlockStatesListEntry extends JSimpleListEntry<Block.StateEntry> implements IValidable {

	public static final Model[] supportedbuiltinitemmodels = new Model[] { normal };

	private final MCreator mcreator;

	private final JStateLabel stateLabel;

	private final BlockTexturesSelector textures;

	private final SearchableComboBox<Model> renderType = new SearchableComboBox<>(supportedbuiltinitemmodels);

	public JBlockStatesListEntry(MCreator mcreator, IHelpContext helpContext, JPanel parent,
			List<JBlockStatesListEntry> entryList, JStateLabel stateLabel) {
		super(parent, entryList);
		this.mcreator = mcreator;
		this.stateLabel = stateLabel;

		this.textures = new BlockTexturesSelector(mcreator);

		ComponentUtils.deriveFont(renderType, 16);
		renderType.setPreferredSize(new Dimension(350, 42));
		renderType.setRenderer(new ModelComboBoxRenderer());

		line.setLayout(new BorderLayout());

		line.add("North", ComponentUtils.applyPadding(stateLabel, 5, true, false, true, false));

		JPanel parameters = new JPanel(new BorderLayout(10, 10));
		parameters.setOpaque(false);
		parameters.add("West", textures);
		parameters.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.gridElements(1, 2, 2, 2,
				HelpUtils.wrapWithHelpButton(helpContext.withEntry("block/state_model"),
						L10N.label("elementgui.block.state_model")), renderType)));

		line.add("Center", parameters);

		renderType.addActionListener(e -> {
			if (normal.equals(renderType.getSelectedItem())) {
				textures.setTextureFormat(BlockTexturesSelector.TextureFormat.ALL);
			} else {
				textures.setTextureFormat(BlockTexturesSelector.TextureFormat.SINGLE_TEXTURE);
			}
		});

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
	}

	@Override public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(renderType, ListUtils.merge(Arrays.asList(supportedbuiltinitemmodels),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	@Override protected void setEntryEnabled(boolean enabled) {
		stateLabel.setEnabled(enabled);

		textures.setEnabled(enabled);
		renderType.setEnabled(enabled);
	}

	JStateLabel getStateLabel() {
		return stateLabel;
	}

	@Override public Block.StateEntry getEntry() {
		Block.StateEntry retVal = new Block.StateEntry();
		retVal.setWorkspace(mcreator.getWorkspace());
		retVal.stateMap = stateLabel.getStateMap();

		retVal.texture = textures.getTexture();
		retVal.textureTop = textures.getTextureTop();
		retVal.textureLeft = textures.getTextureLeft();
		retVal.textureFront = textures.getTextureFront();
		retVal.textureRight = textures.getTextureRight();
		retVal.textureBack = textures.getTextureBack();

		Model model = Objects.requireNonNull(renderType.getSelectedItem());
		retVal.renderType = 10;
		if (model.getType() == Model.Type.JSON)
			retVal.renderType = 2;
		else if (model.getType() == Model.Type.OBJ)
			retVal.renderType = 3;
		retVal.customModelName = model.getReadableName();

		return retVal;
	}

	@Override public void setEntry(Block.StateEntry value) {
		this.stateLabel.setStateMap(value.stateMap);

		textures.setTextures(value.texture, value.textureTop, value.textureLeft, value.textureFront, value.textureRight,
				value.textureBack);
		Model model = value.getItemModel();
		if (model != null)
			renderType.setSelectedItem(model);
	}

	@Override public ValidationResult getValidationStatus() {
		return textures.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return textures.getValidator();
	}

}