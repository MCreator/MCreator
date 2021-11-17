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

package net.mcreator.ui.minecraft.entitydata;

import net.mcreator.element.types.LivingEntity;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class JEntityDataEntry extends JPanel {

	private final VTextField name = new VTextField(20);
	private final JSpinner defaultValue = new JSpinner(
			new SpinnerNumberModel(0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1));

	public JEntityDataEntry(JPanel parent, List<JEntityDataEntry> entryList) {
		super(new FlowLayout(FlowLayout.LEFT));

		final JComponent container = PanelUtils.expandHorizontally(this);

		parent.add(container);
		entryList.add(this);

		name.setValidator(new TextFieldValidator(name, L10N.t("dialog.entity_data.name.needs_name")));
		name.enableRealtimeValidation();
		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("entity/data_name"),
				L10N.label("dialog.entity_data.name")));
		add(name);

		add(HelpUtils.wrapWithHelpButton(IHelpContext.NONE.withEntry("entity/data_default_alue"),
				L10N.label("dialog.entity_data.default_value")));
		add(defaultValue);

		JButton remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.common.remove_entry"));
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

	public LivingEntity.EntityDataEntry getEntry() {
		LivingEntity.EntityDataEntry entry = new LivingEntity.EntityDataEntry();
		entry.name = name.getText().replace(" ", "_");
		entry.defaultValue = (int) defaultValue.getValue();
		return entry;
	}

	public void setEntry(LivingEntity.EntityDataEntry entry) {
		name.setText(entry.name);
		defaultValue.setValue(entry.defaultValue);
	}
}
