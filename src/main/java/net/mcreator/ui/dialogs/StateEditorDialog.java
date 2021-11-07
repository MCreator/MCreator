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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.util.Tuple;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StateEditorDialog {

	public static String open(MCreator mcreator, String initialState) {
		AtomicReference<String> retVal = new AtomicReference<>(initialState);
		MCreatorDialog dialog = new MCreatorDialog(mcreator, L10N.t("dialog.states.title"), true);
		List<Tuple<VTextField, VTextField>> textFields = new ArrayList<>();
		JPanel stateParts = new JPanel();
		AtomicInteger id = new AtomicInteger(0);
		BiConsumer<String, String> tfProvider = (key, value) -> {
			//panel = new JPanel(new FlowLayout(FlowLayout.LEFT));

			VTextField entryKey = new VTextField(5);
			entryKey.setValidator(new RegistryNameValidator(entryKey, "Property name"));
			entryKey.setText(key);

			VTextField entryValue = new VTextField(5);
			entryValue.setText(value);

			JButton remove = new JButton(UIRES.get("16px.clear"));

			JComponent panel = PanelUtils.join(FlowLayout.LEFT, entryKey, new JLabel("="), entryValue, remove);
			remove.addActionListener(e -> {
				textFields.forEach(el -> {
					if (el.x() == entryKey && el.y() == entryValue)
						textFields.remove(el);
				});
				stateParts.remove(panel);
				dialog.revalidate();
				dialog.repaint();
			});

			textFields.add(new Tuple<>(entryKey, entryValue));
			stateParts.add(panel);
		};
		if (initialState != null && !initialState.equals(""))
			Stream.of(initialState.split(",")).peek(e -> tfProvider.accept(e.split("=")[0], e.split("=")[1]));

		JButton add = new JButton(UIRES.get("16px.add.gif"));
		add.setText(L10N.t("dialog.states.add"));
		add.addActionListener(addAction -> {
			id.set(Math.max(textFields.size(), id.get() + 1));
			tfProvider.accept("property" + id.get(), "null");
		});

		JButton ok = new JButton(UIManager.getString("OptionPane.okButtonText"));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.addActionListener(e -> dialog.setVisible(false));
		dialog.getRootPane().setDefaultButton(ok);

		dialog.add("North", add);
		dialog.add("Center", PanelUtils.totalCenterInPanel(stateParts));
		dialog.add("South", PanelUtils.join(ok, cancel));

		ok.addActionListener(e -> {
			if (textFields.stream().noneMatch(el -> el.x().getValidationStatus().getValidationResultType()
					== Validator.ValidationResultType.ERROR)) {
				retVal.set(textFields.stream().map(el -> el.x().getText() + "=" + el.y().getText())
						.collect(Collectors.joining(",")));
				dialog.setVisible(false);
			}
		});

		dialog.setSize(300, 350);
		dialog.setLocationRelativeTo(mcreator);
		dialog.setVisible(true);

		return retVal.get();
	}

}
