/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2020 Pylo and contributors
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

package net.mcreator.ui.action;

import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UnregisteredAction extends AbstractAction implements Comparable {

	protected static final ImageIcon EMPTY = new EmptyIcon.ImageIcon(16, 16);

	private final String name;
	private final ActionListener actionListener;

	public UnregisteredAction(String name, ActionListener listener) {
		putValue(NAME, name);
		putValue(SMALL_ICON, EMPTY);
		this.actionListener = listener;
		setTooltip(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public UnregisteredAction setIcon(ImageIcon icon) {
		if (icon != null) {
			putValue(SMALL_ICON, new ImageIcon(ImageUtils.resizeAA(icon.getImage(), 16, 16)));
			putValue(LARGE_ICON_KEY, icon);
		} else {
			putValue(SMALL_ICON, EMPTY);
		}
		return this;
	}

	public UnregisteredAction setTooltip(String tooltip) {
		putValue(SHORT_DESCRIPTION, tooltip);
		return this;
	}

	public void setAccelerator(KeyStroke accelerator) {
		putValue(ACCELERATOR_KEY, accelerator);
	}

	@Override public void actionPerformed(ActionEvent actionEvent) {
		if (isEnabled())
			actionListener.actionPerformed(actionEvent);
	}

	@Override public int hashCode() {
		return this.name.hashCode();
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof UnregisteredAction)
			return this.name.equals(((UnregisteredAction) obj).name);
		return false;
	}

	@Override public String toString() {
		return this.name;
	}

	public void doAction() {
		this.actionPerformed(new ActionEvent(this, 0, (String) getValue(NAME)));
	}

	@Override public int compareTo(@Nonnull Object o) {
		if (o instanceof UnregisteredAction)
			return this.name.compareTo(((UnregisteredAction) o).name);
		return 0;
	}
}
