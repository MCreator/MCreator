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
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;
import net.mcreator.ui.views.editor.image.versioning.change.Relocation;

import javax.swing.*;
import java.awt.*;
import java.util.UUID;

public class ResizeDialog extends MCreatorDialog {

	public ResizeDialog(MCreator window, Canvas canvas, Layer layer, VersionManager versionManager) {
		super(window, L10N.t("dialog.imageeditor.resize_layer"), true);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JPanel constraints = new JPanel(new GridLayout(4, 2, 5, 5));

		JSpinner width = new JSpinner(new SpinnerNumberModel(layer.getWidth(), 0, 10000, 1));
		JSpinner height = new JSpinner(new SpinnerNumberModel(layer.getHeight(), 0, 10000, 1));
		JCheckBox type = new JCheckBox();
		JCheckBox affectCanvas = new JCheckBox();

		JButton ok = L10N.button("action.common.resize");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		getRootPane().setDefaultButton(ok);

		GridBagConstraints layoutConstraints = new GridBagConstraints();

		ok.addActionListener(e -> {
			layer.resize((int) width.getValue(), (int) height.getValue(), type.isSelected());
			if (affectCanvas.isSelected() && (layer.getX() < 0 || layer.getY() < 0
					|| (layer.getWidth() + layer.getX()) > canvas.getWidth()
					|| (layer.getHeight() + layer.getY()) > canvas.getHeight())) {
				UUID uuid = UUID.randomUUID();
				versionManager.addRevision(new Modification(canvas, layer).setUUID(uuid));
				int dx = layer.getX();
				int dy = layer.getY();
				if (dx < 0 || dy < 0) {
					for (Layer lay : canvas) {
						Relocation reloc = new Relocation(canvas, lay);
						if (dx < 0)
							lay.setX(lay.getX() - dx);
						if (dy < 0)
							lay.setY(lay.getY() - dy);
						reloc.setAfter(lay);
						versionManager.addRevision(reloc.setUUID(uuid));
					}
				}
				canvas.setSize(Math.max(canvas.getWidth(), layer.getWidth() + dx) + Math.max(-dx, 0),
						Math.max(canvas.getHeight(), layer.getHeight() + dy) + Math.max(-dy, 0), uuid);
			} else {
				versionManager.addRevision(new Modification(canvas, layer));
			}
			setVisible(false);
		});

		cancel.addActionListener(e -> setVisible(false));

		constraints.add(L10N.label("dialog.imageeditor.width"));
		constraints.add(width);
		constraints.add(L10N.label("dialog.imageeditor.height"));
		constraints.add(height);
		constraints.add(L10N.label("dialog.imageeditor.resize_enable_anti_aliasing"));
		constraints.add(type);
		constraints.add(L10N.label("dialog.imageeditor.resize_affect_canvas"));
		constraints.add(affectCanvas);

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
		setSize(400, 150);
		setResizable(false);
		setLocationRelativeTo(window);
	}
}
