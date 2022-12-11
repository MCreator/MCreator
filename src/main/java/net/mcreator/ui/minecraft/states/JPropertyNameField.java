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
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class JPropertyNameField extends JPanel {
	private final VTextField field;
	private String cachedName;
	private final JButton rename = new JButton(UIRES.get("16px.edit.gif")) {
		@Override public String getName() {
			return "TechnicalButton";
		}
	};

	public JPropertyNameField(String initialPropertyName) {
		super(new FlowLayout(FlowLayout.CENTER, 7, 7));
		setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));

		field = new VTextField(20) {
			@Override public void setEditable(boolean b) {
				super.setEditable(b);
				rename.setEnabled(!b);
			}
		};
		renameTo(initialPropertyName);
		field.setOpaque(true);
		field.setToolTipText(L10N.t("elementgui.item.custom_property.name_renaming"));
		field.setEditable(false);
		field.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		field.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				field.setEditable(false);
				field.setText(cachedName);
				field.getValidationStatus();
			}
		});
		add(field);

		rename.setOpaque(false);
		rename.setMargin(new Insets(0, 0, 0, 0));
		rename.setBorder(BorderFactory.createEmptyBorder());
		rename.setContentAreaFilled(false);
		rename.setToolTipText(L10N.t("elementgui.item.custom_property.rename"));
		rename.addActionListener(e -> {
			field.setEditable(true);
			field.requestFocus();
		});

		JPanel butPan = new JPanel();
		butPan.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		butPan.add(rename);
		add(butPan);
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		field.setEnabled(enabled);
		rename.setEnabled(enabled);
	}

	public VTextField getTextField() {
		return field;
	}

	public String getPropertyName() {
		return field.getText();
	}

	public String getCachedName() {
		return cachedName;
	}

	public void renameTo(String newName) {
		field.setText(cachedName = newName);
	}

	public void finishRenaming() {
		rename.requestFocus();
	}
}
