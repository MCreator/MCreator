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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.modgui.ItemGUI;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.CommaSeparatedEntriesValidator;
import net.mcreator.util.ListUtils;
import net.mcreator.workspace.resources.Model;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class JItemModelsListEntry extends JPanel {

	private final VTextField state;
	private final SearchableComboBox<Model> model = new SearchableComboBox<>();
	private final MCreator mcreator;

	public JItemModelsListEntry(MCreator mcreator, JPanel parent, List<JItemModelsListEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));

		this.mcreator = mcreator;

		state = new VTextField();
		state.setValidator(new CommaSeparatedEntriesValidator(state));

		final JComponent container = PanelUtils.expandHorizontally(this);

		container.add(L10N.label("elementgui.item.custom_models.name"));
		container.add(state);

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
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	public void reloadDataLists() {
		ComboBoxUtil.updateComboBoxContents(model, ListUtils.merge(ItemGUI.builtInItemModels(),
				Model.getModelsWithTextureMaps(mcreator.getWorkspace()).stream()
						.filter(el -> el.getType() == Model.Type.JSON || el.getType() == Model.Type.OBJ)
						.collect(Collectors.toList())));
	}

	public void addEntry(Map<Map<String, Float>, Model> map) {
		map.put(decodeState(), this.model.getSelectedItem());
	}

	public void setEntry(Map<String, Float> state, Model model) {
		this.state.setText(state.entrySet().stream().map(e -> String.format("%s=%f", e.getKey(), e.getValue()))
				.collect(Collectors.joining(",")));
		this.model.setSelectedItem(model);
	}

	public Map<String, Float> decodeState() {
		Map<String, Float> retVal = new HashMap<>();
		Stream.of(this.state.getText().split(","))
				.peek(e -> retVal.put(e.split("=")[0], Float.parseFloat(e.split("=")[1])));
		return retVal;
	}
}