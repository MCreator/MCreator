/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.component;

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.component.VTextField;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public abstract class JSelector<T> extends JPanel {

	protected final VTextField tfe = new VTextField(14);
	protected final JButton bt = new JButton("...");
	protected final JButton rm = new JButton(UIRES.get("18px.remove"));

	public JSelector(boolean rmEnabled, boolean tfeEditable) {
		setOpaque(false);

		bt.setOpaque(false);
		bt.addActionListener(this::btListener);

		rm.setOpaque(false);
		rm.setEnabled(rmEnabled);
		rm.setMargin(new Insets(0, 3, 0, 3));
		rm.addActionListener(this::rmListener);

		tfe.setEditable(tfeEditable);
		ComponentUtils.deriveFont(tfe, 16);

		setLayout(new BorderLayout(0, 0));
		add("Center", tfe);
		add("East", PanelUtils.gridElements(1, 2, 0, 0, bt, rm));
	}

	public abstract void btListener(ActionEvent event);

	public abstract void rmListener(ActionEvent event);

	@Override public void setEnabled(boolean enabled) {
		tfe.setEnabled(enabled);
		bt.setEnabled(enabled);
		rm.setEnabled(enabled);
	}

	public VTextField getVTextField() {
		return tfe;
	}

	public abstract T getSelected();

	public abstract void setSelected(T selected);

	public abstract void setSelected(String selected);

	public void setText(String text) {
		this.setSelected(text);
	}

}
