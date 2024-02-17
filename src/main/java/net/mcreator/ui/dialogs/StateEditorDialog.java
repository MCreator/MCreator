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
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.minecraft.states.JStateLabel;
import net.mcreator.ui.minecraft.states.PropertyData;
import net.mcreator.ui.minecraft.states.StateMap;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class StateEditorDialog {

	/**
	 * Opens a dialog to edit values of passed properties list used in the future/provided state.
	 *
	 * @param mcreator          The workspace window this method was called from.
	 * @param properties        List of properties that can be used to form the resulting state.
	 * @param stateMap          The property-to-object map representation of the state to be edited, or {@code null}
	 *                          in case it is just being created.
	 * @param numberMatchSymbol NumberMatchType describing how a number property should relate to the specified value.
	 * @return The resulting properties' values map after editing session is complete, or {@code null} if the operation
	 * has been canceled (via cancel/close button).
	 */
	@Nullable public static StateMap open(MCreator mcreator, List<PropertyData<?>> properties, StateMap stateMap,
			JStateLabel.NumberMatchType numberMatchSymbol) {
		AtomicReference<StateMap> retVal = new AtomicReference<>();
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.state_editor.title"), true);

		Map<PropertyData<?>, StatePart> entryMap = new HashMap<>();
		JPanel entries = new JPanel(new GridLayout(0, 1, 5, 5));
		for (PropertyData<?> data : properties) {
			Object value = stateMap != null ? stateMap.get(data) : null;
			StatePart part = new StatePart(data.getName(), data.getClass() == PropertyData.IntegerType.class
					|| data.getClass() == PropertyData.NumberType.class ? numberMatchSymbol.getSymbol() : "=",
					data.getComponent(mcreator, value));
			part.useEntry.setSelected(value != null);
			entryMap.put(data, part);
			entries.add(PanelUtils.expandHorizontally(part));
		}

		JScrollPane scrollEntries = new JScrollPane(PanelUtils.pullElementUp(entries));
		scrollEntries.getVerticalScrollBar().setUnitIncrement(15);

		JButton ok = L10N.button("dialog.common.save_changes");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		dialog.getRootPane().setDefaultButton(ok);

		ok.addActionListener(e -> {
			retVal.set(new StateMap());
			for (PropertyData<?> data : properties) {
				StatePart part = entryMap.get(data);
				if (part.useEntry.isSelected())
					retVal.get().put(data, data.getValue(part.entryComponent));
			}
			dialog.setVisible(false);
		});
		cancel.addActionListener(e -> dialog.setVisible(false));

		dialog.getContentPane().add("North", PanelUtils.join(FlowLayout.LEFT,
				HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("common/state_definition"),
						L10N.label("dialog.state_editor.header"))));
		dialog.getContentPane().add("Center", scrollEntries);
		dialog.getContentPane().add("South", PanelUtils.join(ok, cancel));
		dialog.setSize(300, 400);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

	private static final class StatePart extends JPanel {
		private final JCheckBox useEntry = new JCheckBox();
		private final JComponent entryComponent;

		private StatePart(String property, String matchSymbol, JComponent component) {
			super(new FlowLayout(FlowLayout.LEFT));
			entryComponent = component;

			JPanel settings = new JPanel();
			settings.setBackground(Theme.current().getAltBackgroundColor());
			settings.add(new JLabel(property.replace("CUSTOM:", "")));
			settings.add(new JLabel(matchSymbol));
			settings.add(entryComponent);

			useEntry.setSelected(true);
			useEntry.setToolTipText(L10N.t("dialog.state_editor.use_entry"));
			useEntry.addChangeListener(e -> {
				entryComponent.setEnabled(useEntry.isSelected());
				settings.setBackground(useEntry.isSelected() ?
						Theme.current().getAltBackgroundColor() :
						Theme.current().getSecondAltBackgroundColor());
			});

			add(useEntry);
			add(settings);
		}
	}
}
