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

import net.mcreator.io.ResourcePointer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.MCreatorDialog;
import net.mcreator.ui.dialogs.TextureSelectorDialog;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NewLayerDialog extends MCreatorDialog {
	private final String[] layerTypes = { "Transparency", "Color", "Template" };

	private ResourcePointer selection;
	private final List<ResourcePointer> templatesSorted;

	public NewLayerDialog(MCreator window, Canvas canvas) {
		super(window, L10N.t("dialog.imageeditor.new_layer"), true);

		templatesSorted = new ArrayList<>(ImageMakerTexturesCache.CACHE.keySet());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));
		selection = templatesSorted.get(0);
		TextureSelectorDialog templateChooser = new TextureSelectorDialog(templatesSorted, window);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JPanel properties = new JPanel(new GridLayout(2, 2, 5, 5));
		JPanel specialSettings = new JPanel(new CardLayout());
		JPanel constraints = new JPanel(new GridLayout(4, 2, 5, 5));

		//Basic settings
		JTextField layerName = new JTextField("Layer");
		JComboBox<String> layerType = new JComboBox<>(layerTypes);

		//Filler settings
		JPanel colorSettings = new JPanel(new GridLayout(1, 2, 5, 5));
		JColor colorChoser = new JColor(window);
		colorSettings.add(L10N.label("dialog.imageeditor.base_color"));
		colorSettings.add(colorChoser);

		JPanel templateSettings = new JPanel(new GridLayout(1, 2, 5, 5));
		JButton templateChooserButton = new JButton(
				new ImageIcon(ImageUtils.resize(ImageMakerTexturesCache.CACHE.get(selection).getImage(), 32)));
		templateChooserButton.setMargin(new Insets(0, 0, 0, 0));
		templateSettings.add(L10N.label("dialog.imageeditor.base_texture"));
		templateSettings.add(PanelUtils.totalCenterInPanel(templateChooserButton));

		//Constraints
		JSpinner width = new JSpinner(new SpinnerNumberModel(canvas.getWidth(), 0, 10000, 1));
		JSpinner height = new JSpinner(new SpinnerNumberModel(canvas.getHeight(), 0, 10000, 1));
		JSpinner offsetX = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));
		JSpinner offsetY = new JSpinner(new SpinnerNumberModel(0, -10000, 10000, 1));

		JButton cancel = L10N.button(UIManager.getString("OptionPane.cancelButtonText"));
		JButton ok = L10N.button("action.common.create");
		ok.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		ok.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		getRootPane().setDefaultButton(ok);

		GridBagConstraints layoutConstraints = new GridBagConstraints();

		layerType.addActionListener(e -> {
			CardLayout layout = (CardLayout) specialSettings.getLayout();
			layout.show(specialSettings, layerTypes[layerType.getSelectedIndex()]);
			if (layerType.getSelectedIndex() == 2) {
				ImageIcon img = ImageMakerTexturesCache.CACHE.get(selection);
				width.setValue(img.getIconWidth());
				height.setValue(img.getIconHeight());
			}
		});

		templateChooserButton.addActionListener(event -> templateChooser.setVisible(true));

		templateChooser.naprej.addActionListener(arg01 -> {
			templateChooser.setVisible(false);
			selection = templateChooser.list.getSelectedValue();
			ImageIcon icon = ImageMakerTexturesCache.CACHE.get(selection);
			templateChooserButton.setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			width.setValue(icon.getIconWidth());
			height.setValue(icon.getIconHeight());
		});

		cancel.addActionListener(e -> setVisible(false));

		ok.addActionListener(e -> {
			switch (layerType.getSelectedIndex()) {
			case 0:
				canvas.add(new Layer((int) width.getValue(), (int) height.getValue(), (int) offsetX.getValue(),
						(int) offsetY.getValue(), layerName.getText()));
				break;
			case 1:
				canvas.add(new Layer((int) width.getValue(), (int) height.getValue(), (int) offsetX.getValue(),
						(int) offsetY.getValue(), layerName.getText(), colorChoser.getColor()));
				break;
			case 2:
				canvas.add(new Layer((int) width.getValue(), (int) height.getValue(), (int) offsetX.getValue(),
						(int) offsetY.getValue(), layerName.getText(),
						ImageMakerTexturesCache.CACHE.get(selection).getImage()));
				break;
			}
			setVisible(false);
		});

		properties.add(L10N.label("dialog.imageeditor.new_layer_name"));
		properties.add(layerName);
		properties.add(L10N.label("dialog.imageeditor.new_layer_fill_with"));
		properties.add(layerType);

		specialSettings
				.add(PanelUtils.totalCenterInPanel(L10N.label("dialog.imageeditor.new_layer_fills_transparency")),
						layerTypes[0]);
		specialSettings.add(colorSettings, layerTypes[1]);
		specialSettings.add(templateSettings, layerTypes[2]);

		constraints.add(L10N.label("dialog.imageeditor.width"));
		constraints.add(width);
		constraints.add(L10N.label("dialog.imageeditor.height"));
		constraints.add(height);
		constraints.add(L10N.label("dialog.imageeditor.new_layer_x_offset"));
		constraints.add(offsetX);
		constraints.add(L10N.label("dialog.imageeditor.new_layer_y_offset"));
		constraints.add(offsetY);

		layoutConstraints.gridx = 0;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 1.0;
		layoutConstraints.insets = new Insets(5, 5, 0, 0);

		layoutConstraints.gridheight = 2;
		settings.add(properties, layoutConstraints);
		layoutConstraints.gridheight = 1;
		settings.add(specialSettings, layoutConstraints);
		layoutConstraints.gridheight = 4;
		settings.add(constraints, layoutConstraints);

		controls.add(cancel, BorderLayout.WEST);
		controls.add(ok, BorderLayout.EAST);
		add(PanelUtils.maxMargin(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(PanelUtils.maxMargin(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(500, 300);
		setResizable(false);
		setLocationRelativeTo(window);
	}
}
