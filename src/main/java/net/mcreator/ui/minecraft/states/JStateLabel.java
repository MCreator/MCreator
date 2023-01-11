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

package net.mcreator.ui.minecraft.states;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JStateLabel extends JPanel {
	private final Supplier<List<PropertyData<?, ?>>> properties;
	private LinkedHashMap<PropertyData<?, ?>, Object> stateMap = new LinkedHashMap<>();
	private String state;

	private final JTextField label = new JTextField();
	private final JButton edit = new JButton(UIRES.get("16px.edit.gif")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};

	public JStateLabel(Supplier<List<PropertyData<?, ?>>> properties, Runnable editButtonListener) {
		super(new FlowLayout(FlowLayout.CENTER, 7, 5));
		this.properties = properties;

		label.setEditable(false);
		label.setOpaque(true);
		label.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		JScrollPane statePane = new JScrollPane(label);
		statePane.setPreferredSize(new Dimension(300, 30));
		add(statePane);

		edit.setOpaque(false);
		edit.setMargin(new Insets(0, 0, 0, 0));
		edit.setBorder(BorderFactory.createEmptyBorder());
		edit.setContentAreaFilled(false);
		edit.setToolTipText(L10N.t("elementgui.common.custom_state.edit"));
		edit.addActionListener(e -> editButtonListener.run());

		JButton copy = new JButton(UIRES.get("16px.copyclipboard")) {
			@Override public String getName() {
				return "TechnicalButton";
			}
		};
		copy.setOpaque(false);
		copy.setMargin(new Insets(0, 0, 0, 0));
		copy.setBorder(BorderFactory.createEmptyBorder());
		copy.setContentAreaFilled(false);
		copy.setToolTipText(L10N.t("elementgui.common.custom_state.copy"));
		copy.addActionListener(e -> Toolkit.getDefaultToolkit().getSystemClipboard()
				.setContents(new StringSelection(getState()), null));

		JPanel controls = new JPanel();
		controls.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		controls.add(edit);
		controls.add(copy);
		add(controls);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		edit.setEnabled(enabled);
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
		this.stateMap = new LinkedHashMap<>();
		Map<String, String> values = Arrays.stream(state.split(","))
				.collect(Collectors.toMap(e -> e.split("=")[0], e -> e.split("=")[1]));
		for (PropertyData<?, ?> property : properties.get()) {
			if (values.containsKey(property.getName()))
				stateMap.put(property, property.parseObj(values.get(property.getName())));
		}
		refreshState();
	}

	public LinkedHashMap<PropertyData<?, ?>, Object> getStateMap() {
		return stateMap;
	}

	public void setStateMap(LinkedHashMap<PropertyData<?, ?>, Object> stateMap) {
		this.stateMap = stateMap;
		refreshState();
	}

	public void rename(String property, String newName) {
		for (PropertyData<?, ?> data : stateMap.keySet()) {
			if (data.getName().equals(property)) {
				data.setName(newName);
				break;
			}
		}
		refreshState();
	}

	private void refreshState() {
		this.state = stateMap.entrySet().stream().map(e -> e.getKey().getName() + "=" + e.getValue())
				.collect(Collectors.joining(","));
		label.setText(L10N.t("elementgui.common.custom_state.when") + stateMap.entrySet().stream()
				.map(e -> StringUtils.snakeToCamel(e.getKey().getName()) + " = " + e.getValue())
				.collect(Collectors.joining("; ")));
	}
}
