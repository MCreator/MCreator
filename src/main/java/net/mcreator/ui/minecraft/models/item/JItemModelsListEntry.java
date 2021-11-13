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

package net.mcreator.ui.minecraft.models.item;

import net.mcreator.element.types.Item;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.StateEditorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.ModelComboBoxRenderer;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.util.ListUtils;
import net.mcreator.util.Tuple;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JItemModelsListEntry extends JPanel {

	private final JTextField state;
	private final SearchableComboBox<Model> model = new SearchableComboBox<>();
	private final MCreator mcreator;

	public JItemModelsListEntry(MCreator mcreator, JPanel parent, List<JItemModelsListEntry> entryList) {
		super(new BorderLayout());

		this.mcreator = mcreator;

		state = new JTextField(30);
		state.setEditable(false);
		state.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		state.setToolTipText(L10N.t("elementgui.item.custom_models.entry.edit_states"));
		state.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					state.setText(StateEditorDialog.open(mcreator, state.getText()));
			}
		});

		ComponentUtils.deriveFont(model, 16);
		model.setPreferredSize(new Dimension(350, 42));
		model.setRenderer(new ModelComboBoxRenderer());
		reloadDataLists();

		final JComponent container = PanelUtils.expandHorizontally(this);
		JPanel east = new JPanel();

		add("Center", new JScrollPane(state));

		east.add(L10N.label("elementgui.item.custom_models.entry"));
		east.add(model);

		parent.add(container);
		entryList.add(this);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.item.custom_models.remove"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		east.add(remove);

		add("East", east);

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(ItemGUI.builtInItemModels(),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	public void addEntry(Map<Map<String, Float>, Tuple<String, Integer>> map) {
		Model selectedModel = Objects.requireNonNull(model.getSelectedItem());
		Map<String, Float> stateMap = Stream.of(state.getText().split(","))
				.collect(Collectors.toMap(k -> k.split("=")[0], v -> Float.parseFloat(v.split("=")[1])));
		map.put(stateMap, new Tuple<>(selectedModel.getReadableName(), Item.encodeModelType(selectedModel.getType())));
	}

	public void setEntry(Map<String, Float> state, Tuple<String, Integer> model) {
		this.state.setText(state.entrySet().stream().map(e -> String.format("%s=%f", e.getKey(), e.getValue()))
				.collect(Collectors.joining(",")));
		this.model.setSelectedItem(
				Model.getModelByParams(mcreator.getWorkspace(), model.x(), Item.decodeModelType(model.y())));
	}
}