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
import net.mcreator.ui.dialogs.BlockItemTextureSelector;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class JItemStatesListEntry extends JPanel implements IValidable {

	private final MCreator mcreator;
	private final JComponent container;
	private final JButton remove;

	final JLabel state = new JLabel();
	final JButton edit = new JButton(UIRES.get("16px.edit.gif"));

	private final TextureHolder texture;
	private final SearchableComboBox<Model> model = new SearchableComboBox<>(ItemGUI.builtInItemModels());

	public JItemStatesListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JItemStatesListEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));
		this.mcreator = mcreator;

		state.setOpaque(true);
		state.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, TextureType.ITEM));
		texture.setValidator(new TileHolderValidator(texture));

		edit.setOpaque(false);
		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setBorder(BorderFactory.createEmptyBorder());
		edit.setContentAreaFilled(false);
		edit.setToolTipText(L10N.t("elementgui.item.custom_states.edit_state"));

		JButton copy = new JButton(UIRES.get("16px.copyclipboard"));
		copy.setOpaque(false);
		copy.setMargin(new Insets(0, 0, 0, 0));
		copy.setBorder(BorderFactory.createEmptyBorder());
		copy.setContentAreaFilled(false);
		copy.setToolTipText(L10N.t("elementgui.item.custom_states.copy_state"));
		copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(state.getText()), null));

		ComponentUtils.deriveFont(model, 16);
		model.setPreferredSize(new Dimension(300, 50));
		model.setRenderer(new ModelComboBoxRenderer());
		reloadDataLists(); // we make sure that combo box can be properly shown

		container = PanelUtils.expandHorizontally(this);

		JScrollPane stateLabel = new JScrollPane(state);
		stateLabel.setOpaque(true);
		stateLabel.setPreferredSize(new Dimension(300, 30));

		JPanel statePane = PanelUtils.join(edit, copy);
		statePane.setOpaque(true);
		statePane.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		add(stateLabel);
		add(statePane);

		add(ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture")));
		add(HelpUtils.stackHelpTextAndComponent(gui.withEntry("item/model"),
				L10N.t("elementgui.item.custom_states.model"), model, 3));

		parent.add(container);
		entryList.add(this);

		remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.item.custom_states.remove"));
		remove.addActionListener(e -> removeState(parent, entryList));
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public void removeState(JPanel parent, List<JItemStatesListEntry> entryList) {
		entryList.remove(this);
		parent.remove(container);
		parent.revalidate();
		parent.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		remove.setEnabled(enabled);

		edit.setEnabled(enabled);
		texture.setEnabled(enabled);
		model.setEnabled(enabled);
	}

	public void propertyRenamed(String property, String newName, int index) {
		String[] stateParts = state.getText().split(",");
		if (index >= stateParts.length || !stateParts[index].startsWith(property + "="))
			index = Arrays.stream(stateParts).map(e -> e.split("=")[0]).toList().indexOf(property);
		stateParts[index] = stateParts[index].replace(property + "=", newName + "=");
		state.setText(String.join(",", stateParts));
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(Arrays.asList(ItemGUI.builtInItemModels()),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ).toList()));
	}

	public Item.ModelEntry getEntry() {
		Item.ModelEntry retVal = new Item.ModelEntry();
		retVal.modelName = Objects.requireNonNull(model.getSelectedItem()).getReadableName();
		retVal.modelTexture = texture.getID();
		retVal.renderType = Item.encodeModelType(Objects.requireNonNull(model.getSelectedItem()).getType());
		return retVal;
	}

	public void setEntry(String state, Item.ModelEntry value) {
		this.state.setText(state);

		this.texture.setTextureFromTextureName(value.modelTexture);
		this.model.setSelectedItem(Model.getModelByParams(mcreator.getWorkspace(), value.modelName,
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