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

package net.mcreator.ui.dialogs.imageeditor;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.views.editor.image.canvas.Canvas;

import javax.swing.*;
import java.awt.*;

public class ResizeCanvasDialog extends MCreatorDialog {

	public ResizeCanvasDialog(MCreator window, Canvas canvas) {
		super(window, "Resize Canvas", true);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JPanel constraints = new JPanel(new GridLayout(4, 2, 5, 5));

		JSpinner width = new JSpinner(new SpinnerNumberModel(canvas.getWidth(), 0, 10000, 1));
		JSpinner height = new JSpinner(new SpinnerNumberModel(canvas.getHeight(), 0, 10000, 1));

		JButton cancel = new JButton("Cancel");
		JButton ok = new JButton("Resize");
		ok.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		ok.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		getRootPane().setDefaultButton(ok);

		GridBagConstraints layoutConstraints = new GridBagConstraints();

		cancel.addActionListener(e -> setVisible(false));

		ok.addActionListener(e -> {
			canvas.setSize((int) width.getValue(), (int) height.getValue());
			setVisible(false);
		});

		constraints.add(new JLabel("Width:"));
		constraints.add(width);
		constraints.add(new JLabel("Height:"));
		constraints.add(height);

		layoutConstraints.gridx = 0;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 1.0;
		layoutConstraints.insets = new Insets(2, 2, 2, 2);
		layoutConstraints.gridheight = 1;

		settings.add(constraints, layoutConstraints);

		controls.add(cancel, BorderLayout.WEST);
		controls.add(ok, BorderLayout.EAST);
		add(PanelUtils.maxMargin(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(PanelUtils.maxMargin(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(window);
	}
}
