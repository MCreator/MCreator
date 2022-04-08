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

import net.mcreator.ui.action.accelerators.Accelerator;

import javax.swing.*;
import java.awt.event.ActionListener;

public class BasicAction extends UnregisteredAction {

	protected ActionRegistry actionRegistry;
	protected Accelerator accelerator;

	public BasicAction(ActionRegistry actionRegistry, String name, ActionListener listener) {
		this(actionRegistry, name, listener, null);
	}

	public BasicAction(ActionRegistry actionRegistry, String name, ActionListener listener, Accelerator accelerator) {
		super(name, listener);
		this.actionRegistry = actionRegistry;
		this.actionRegistry.addAction(this);
		this.accelerator = accelerator;
	}

	public Accelerator getAccelerator() {
		return accelerator;
	}

	@Override public BasicAction setIcon(ImageIcon icon) {
		super.setIcon(icon);
		return this;
	}

	@Override public BasicAction setTooltip(String tooltip) {
		super.setTooltip(tooltip);
		return this;
	}

}
