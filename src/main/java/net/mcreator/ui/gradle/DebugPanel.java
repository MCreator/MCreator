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

package net.mcreator.ui.gradle;

import net.mcreator.java.debug.JVMDebugClient;
import net.mcreator.ui.laf.PlainToolbarBorder;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;

public class DebugPanel extends JToolBar {

	@Nullable private JVMDebugClient debugClient = null;

	public DebugPanel() {
		setBorder(new PlainToolbarBorder());

		setLayout(new BorderLayout());

		add(new JLabel("Test"));
	}

	public void startDebug(JVMDebugClient debugClient) {
		this.debugClient = debugClient;

		setVisible(true);
	}

	public void stopDebug() {
		this.debugClient = null;

		setVisible(false);
	}

}
