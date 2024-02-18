/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.item;

import net.mcreator.element.types.Item;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JItemStatesListEntry extends JPanel implements IValidable {

	private final MCreator mcreator;
	private final JButton remove = new JButton(UIRES.get("16px.clear"));

	private final JStateLabel stateLabel;

	private final TextureHolder texture;

	private final SearchableComboBox<Model> model = new SearchableComboBox<>(ItemGUI.builtinitemmodels);

	public JItemStatesListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JItemStatesListEntry> entryList, JStateLabel stateLabel) {
		super(new BorderLayout(5, 5));
		this.mcreator = mcreator;
		this.stateLabel = stateLabel;

		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.ITEM), 42);
		texture.setValidator(new TileHolderValidator(texture));

		ComponentUtils.deriveFont(model, 16);
		model.setPreferredSize(new Dimension(350, 42));
		model.setRenderer(new ModelComboBoxRenderer());
		reloadDataLists(); // we make sure that combo box can be properly shown

		JComponent ito = PanelUtils.northAndCenterElement(
				PanelUtils.join(FlowLayout.LEFT, L10N.label("elementgui.item.texture")),
				PanelUtils.join(FlowLayout.LEFT, 2, 2, texture));

		JComponent imo = PanelUtils.northAndCenterElement(HelpUtils.wrapWithHelpButton(gui.withEntry("item/model"),
				L10N.label("elementgui.item.custom_state.model")), model);

		remove.setText(L10N.t("elementgui.item.custom_state.remove"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(this);
			parent.revalidate();
			parent.repaint();
		});

		add("North", PanelUtils.maxMargin(stateLabel, 5, true, false, true, false));
		add("West", PanelUtils.westAndCenterElement(ito, imo, 10, 0));
		add("East", PanelUtils.pullElementUp(remove));

		entryList.add(this);
		parent.add(this);
		parent.revalidate();
		parent.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		stateLabel.setEnabled(enabled);
		texture.setEnabled(enabled);
		model.setEnabled(enabled);

		remove.setEnabled(enabled);
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Arrays.asList(ItemGUI.builtinitemmodels),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ).toList()));
	}

	JStateLabel getStateLabel() {
		return stateLabel;
	}

	public Item.StateEntry getEntry() {
		Item.StateEntry retVal = new Item.StateEntry();
		retVal.setWorkspace(mcreator.getWorkspace());
		retVal.customModelName = Objects.requireNonNull(model.getSelectedItem()).getReadableName();
		retVal.texture = texture.getID();
		retVal.renderType = Item.encodeModelType(Objects.requireNonNull(model.getSelectedItem()).getType());
		retVal.stateMap = stateLabel.getStateMap();
		return retVal;
	}

	public void setEntry(Item.StateEntry value) {
		this.stateLabel.setStateMap(value.stateMap);

		this.texture.setTextureFromTextureName(value.texture);
		this.model.setSelectedItem(Model.getModelByParams(mcreator.getWorkspace(), value.customModelName,
				Item.decodeModelType(value.renderType)));
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return texture.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return texture.getValidator();
	}
}