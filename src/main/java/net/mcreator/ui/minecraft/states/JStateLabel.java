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

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class JStateLabel extends JPanel {
	private final Supplier<List<IPropertyData<?>>> properties;
	private LinkedHashMap<IPropertyData<?>, Object> stateMap = new LinkedHashMap<>();

	private final JTextField label = new JTextField();

	public JStateLabel(Supplier<List<IPropertyData<?>>> properties, Runnable editButtonListener) {
		super(new BorderLayout(5, 0));
		this.properties = properties;

		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		label.setEditable(false);
		label.setOpaque(false);
		refreshState();

		JScrollPane statePane = new JScrollPane(label);
		statePane.setOpaque(false);
		statePane.getViewport().setOpaque(false);
		statePane.setPreferredSize(new Dimension(300, 30));
		add("Center", statePane);

		JPanel controls = new JPanel();
		controls.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		add("East", PanelUtils.centerInPanelPadding(controls, 2, 2));

		if (editButtonListener != null) {
			JButton edit = new JButton(UIRES.get("16px.edit.gif")) {
				@Override public String getName() {
					return "TechnicalButton";
				}
			};
			edit.setOpaque(false);
			edit.setMargin(new Insets(0, 0, 0, 0));
			edit.setBorder(BorderFactory.createEmptyBorder());
			edit.setContentAreaFilled(false);
			edit.setToolTipText(L10N.t("components.state_label.edit"));
			edit.addActionListener(e -> editButtonListener.run());
			addPropertyChangeListener("enabled", e -> edit.setEnabled((boolean) e.getNewValue()));
			controls.add(edit);
		}
	}

	public String getState() {
		return stateMap.entrySet().stream().map(e -> e.getKey().getName() + "=" + e.getKey().toString(e.getValue()))
				.collect(Collectors.joining(","));
	}

	public void setState(String state) {
		stateMap = IPropertyData.passStateToMap(state, properties.get());
		refreshState();
	}

	public LinkedHashMap<IPropertyData<?>, Object> getStateMap() {
		return this.stateMap;
	}

	public void setStateMap(LinkedHashMap<IPropertyData<?>, Object> stateMap) {
		this.stateMap = stateMap;
		refreshState();
	}

	public void rename(String property, String newName) {
		for (IPropertyData<?> data : stateMap.keySet()) {
			if (data instanceof PropertyData<?> prop && prop.getName().equals(property)) {
				prop.setName(newName);
				break;
			}
		}
		refreshState();
	}

	private void refreshState() {
		List<String> stateParts = new ArrayList<>();
		stateMap.forEach((k, v) -> stateParts.add(k.getName() + " = " + k.toString(v)));
		label.setText(L10N.t("components.state_label.when",
				stateParts.isEmpty() ? L10N.t("condition.common.true") : String.join("; ", stateParts)));
	}
}
