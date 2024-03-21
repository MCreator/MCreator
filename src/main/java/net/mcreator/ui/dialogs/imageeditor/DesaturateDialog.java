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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.Selection;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.tool.component.JSlidingSpinner;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.ui.views.editor.image.versioning.change.Modification;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class DesaturateDialog extends MCreatorDialog {

	public DesaturateDialog(MCreator window, Canvas canvas, Layer layer, VersionManager versionManager) {
		super(window, L10N.t("dialog.imageeditor.desaturate_title"), true);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JSlidingSpinner spinner = new JSlidingSpinner(L10N.t("dialog.imageeditor.saturation"));
		spinner.setValue(50);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		JButton ok = L10N.button("dialog.imageeditor.desaturate_action");
		getRootPane().setDefaultButton(ok);

		GridBagConstraints layoutConstraints = new GridBagConstraints();

		cancel.addActionListener(e -> setVisible(false));

		ok.addActionListener(e -> {
			BufferedImage bim = ImageUtils.deepCopy(layer.getRaster());

			Selection selection = canvas.getSelection();
			Shape validArea = selection.getLayerMask(layer);

			Graphics2D g2d = layer.createGraphics();

			Shape previousShape = g2d.getClip();

			if (validArea != null)
				g2d.setClip(validArea);

			g2d.setBackground(new Color(0, 0, 0, 0));
			g2d.clearRect(0, 0, getWidth(), getHeight());
			g2d.drawImage(ImageUtils.toBufferedImage(
							ImageUtils.changeSaturation(new ImageIcon(bim), (float) spinner.getValue() / 100).getImage()), null,
					0, 0);

			if (validArea != null)
				g2d.setClip(previousShape);

			g2d.dispose();
			layer.mergeOverlay();
			versionManager.addRevision(new Modification(canvas, layer));
			setVisible(false);
		});

		layoutConstraints.gridx = 0;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 1.0;
		layoutConstraints.insets = new Insets(2, 2, 2, 2);
		layoutConstraints.gridheight = 1;

		settings.add(spinner, layoutConstraints);

		controls.add(cancel, BorderLayout.WEST);
		controls.add(ok, BorderLayout.EAST);
		add(ComponentUtils.applyPadding(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(ComponentUtils.applyPadding(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(300, 150);
		setResizable(false);
		setLocationRelativeTo(window);
	}
}