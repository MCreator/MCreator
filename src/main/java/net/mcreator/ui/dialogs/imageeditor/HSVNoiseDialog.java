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
import java.util.UUID;

public class HSVNoiseDialog extends MCreatorDialog {

	public HSVNoiseDialog(MCreator window, Canvas canvas, Layer layer, VersionManager versionManager) {
		super(window, L10N.t("dialog.imageeditor.noise_apply"), true);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JSlidingSpinner hue = new JSlidingSpinner(L10N.t("dialog.imageeditor.hsv_noise_hue"), 5, 0, 255, 1);
		JSlidingSpinner sat = new JSlidingSpinner(L10N.t("dialog.imageeditor.hsv_noise_saturation"), 30, 0, 255, 1);
		JSlidingSpinner val = new JSlidingSpinner(L10N.t("dialog.imageeditor.hsv_noise_value"), 30, 0, 255, 1);

		JPanel seedControls = new JPanel(new GridLayout(2, 1, 5, 5));
		JButton randomize = L10N.button("dialog.imageeditor.noise_randomize_seed");
		JTextField textField = new JTextField();
		textField.setText(generateSeed());
		randomize.addActionListener(e -> textField.setText(generateSeed()));

		seedControls.add(L10N.label("dialog.imageeditor.noise_seed"));
		seedControls.add(textField);
		seedControls.add(new JLabel(""));
		seedControls.add(randomize);

		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		JButton ok = L10N.button("action.common.apply");
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
			g2d.drawImage(ImageUtils.noiseHSV(bim, (float) (hue.getValue() / 255.0), (float) (sat.getValue() / 255.0),
					(float) (val.getValue() / 255.0), generateSum(textField.getText())), 0, 0, null);

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

		settings.add(hue, layoutConstraints);
		settings.add(sat, layoutConstraints);
		settings.add(val, layoutConstraints);
		settings.add(seedControls, layoutConstraints);

		controls.add(cancel, BorderLayout.WEST);
		controls.add(ok, BorderLayout.EAST);
		add(ComponentUtils.applyPadding(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(ComponentUtils.applyPadding(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(400, 250);
		setResizable(false);
		setLocationRelativeTo(window);
	}

	private static String generateSeed() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	private static long generateSum(String text) {
		long sum = 0;
		for (char c : text.toCharArray())
			sum += c;
		return sum;
	}
}