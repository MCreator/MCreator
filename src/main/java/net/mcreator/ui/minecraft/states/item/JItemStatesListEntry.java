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
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JItemStatesListEntry extends JPanel implements IValidable {

	private final MCreator mcreator;

	final JTextField state;
	final JButton edit = new JButton(UIRES.get("16px.edit.gif"));
	final JButton copy = new JButton(UIRES.get("16px.copyclipboard"));

	private final TextureHolder texture;
	private final SearchableComboBox<Model> model;
	private final JComponent container;

	public JItemStatesListEntry(MCreator mcreator, JPanel parent, List<JItemStatesListEntry> entryList,
			String initialState) {
		super(new BorderLayout());
		this.mcreator = mcreator;

		state = new JTextField(20);
		state.setEditable(false);
		state.setText(initialState);
		state.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		state.setToolTipText(L10N.t("elementgui.item.custom_states.edit_states"));

		texture = new TextureHolder(new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.ITEM));
		texture.setOpaque(false);
		texture.setValidator(new TileHolderValidator(texture));

		edit.setOpaque(false);
		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setBorder(BorderFactory.createEmptyBorder());
		edit.setContentAreaFilled(false);

		copy.setOpaque(false);
		copy.setMargin(new Insets(0, 0, 0, 0));
		copy.setBorder(BorderFactory.createEmptyBorder());
		copy.setContentAreaFilled(false);
		copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(state.getText()), null));

		model = new SearchableComboBox<>();
		ComponentUtils.deriveFont(model, 16);
		model.setPreferredSize(new Dimension(300, 42));
		model.setRenderer(new ModelComboBoxRenderer());
		reloadDataLists();

		container = PanelUtils.expandHorizontally(this);
		JPanel west = new JPanel(new GridLayout(3, 1));
		JPanel east = new JPanel();

		JPanel statePane = PanelUtils.join(edit, copy);
		statePane.setOpaque(false);
		statePane.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));

		west.add(new JLabel(" "));
		west.add(new JScrollPane(PanelUtils.join(state, statePane)));

		east.add(ComponentUtils.squareAndBorder(texture, L10N.t("elementgui.item.texture")));
		east.add(PanelUtils.northAndCenterElement(
				HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("item/model"),
						L10N.label("elementgui.item.custom_states.model"), SwingConstants.LEFT), model));

		parent.add(container);
		entryList.add(this);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.item.custom_states.remove"));
		remove.addActionListener(e -> removeState(parent, entryList));
		east.add(remove);

		add(PanelUtils.westAndCenterElement(west, east));

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(ItemGUI.builtInItemModels(),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ).toList()));
	}

	public void propertyRenamed(String property, String newName) {
		String updated = state.getText();
		if (updated.startsWith(property))
			updated = updated.replace(property + "=", newName + "=");
		else
			updated = updated.replace("," + property + "=", "," + newName + "=");
		state.setText(updated);
	}

	public void removeState(JPanel parent, List<JItemStatesListEntry> entryList) {
		entryList.remove(this);
		parent.remove(container);
		parent.revalidate();
		parent.repaint();
	}

	public void addEntry(Map<Map<String, Float>, Item.ModelEntry> map) {
		Map<String, Float> stateMap = Stream.of(state.getText().split(","))
				.collect(Collectors.toMap(k -> k.split("=")[0], v -> Float.parseFloat(v.split("=")[1])));

		Item.ModelEntry modelEntry = new Item.ModelEntry();
		modelEntry.workspace = mcreator.getWorkspace();
		modelEntry.modelName = Objects.requireNonNull(model.getSelectedItem()).getReadableName();
		modelEntry.modelTexture = texture.getID();
		modelEntry.renderType = Item.encodeModelType(Objects.requireNonNull(model.getSelectedItem()).getType());

		map.put(stateMap, modelEntry);
	}

	public void setEntry(Map<String, Float> state, Item.ModelEntry value) {
		this.state.setText(
				state.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining(",")));
		this.texture.setTextureFromTextureName(value.modelTexture);
		this.model.setSelectedItem(Model.getModelByParams(mcreator.getWorkspace(), value.modelName,
				Item.decodeModelType(value.renderType)));
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return texture.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
		texture.setValidator(validator);
	}

	@Override public Validator getValidator() {
		return texture.getValidator();
	}
}