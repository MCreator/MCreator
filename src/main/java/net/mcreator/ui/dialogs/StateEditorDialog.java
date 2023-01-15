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

package net.mcreator.ui.dialogs;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.states.PropertyData;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class StateEditorDialog {

	/**
	 * Opens a dialog to edit values of passed properties list used in the future/passed state.
	 *
	 * @param mcreator   The workspace window this method was called from.
	 * @param properties List of properties that can be used to form the resulting state.
	 * @param stateMap   The property-object map representation of the state to be edited.
	 * @param newState   Whether the state is just being created.
	 * @param helpPath   The path to the help context file used as dialog's tooltip.
	 * @return True if user chose OK option after editing properties' values, false if it was cancel/close option.
	 */
	public static boolean open(MCreator mcreator, List<PropertyData<?>> properties,
			LinkedHashMap<PropertyData<?>, Object> stateMap, boolean newState, String helpPath) {
		AtomicBoolean retVal = new AtomicBoolean();
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.state_editor.title"), true);

		Map<PropertyData<?>, StatePart> entryMap = new HashMap<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		for (PropertyData<?> param : properties) {
			StatePart part = new StatePart(param.getName(), param.getComponent(mcreator, stateMap.get(param)));
			part.useEntry.setSelected(stateMap.containsKey(param) || newState);
			entryMap.put(param, part);
			entries.add(PanelUtils.expandHorizontally(part));
		}

		JButton ok = newState ? L10N.button("dialog.state_editor.create") : L10N.button("dialog.state_editor.save");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			stateMap.clear();
			for (PropertyData<?> param : properties) {
				StatePart part = entryMap.get(param);
				if (part.useEntry.isSelected())
					stateMap.put(param, param.getValue(part.entryComponent));
			}
			retVal.set(true);
			dialog.setVisible(false);
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		Component editor = HelpUtils.stackHelpTextAndComponent(IHelpContext.NONE.withEntry(helpPath),
				L10N.t("dialog.state_editor.header"), new JScrollPane(PanelUtils.pullElementUp(entries)), 7);
		dialog.getContentPane().add(PanelUtils.centerAndSouthElement(editor, PanelUtils.join(ok, cancel)));
		dialog.setSize(300, 400);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static final class StatePart extends JPanel {
		private final JCheckBox useEntry = new JCheckBox();
		private final JComponent entryComponent;

		private StatePart(String property, JComponent component) {
			super(new FlowLayout(FlowLayout.LEFT));
			entryComponent = component;

			JPanel settings = new JPanel();
			settings.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			settings.add(new JLabel(property));
			settings.add(new JLabel("="));
			settings.add(entryComponent);

			useEntry.setSelected(true);
			useEntry.setToolTipText(L10N.t("dialog.state_editor.use_entry"));
			useEntry.addChangeListener(e -> {
				entryComponent.setEnabled(useEntry.isSelected());
				settings.setBackground(useEntry.isSelected() ?
						(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT") :
						(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
			});

			add(useEntry);
			add(settings);
		}
	}
}
