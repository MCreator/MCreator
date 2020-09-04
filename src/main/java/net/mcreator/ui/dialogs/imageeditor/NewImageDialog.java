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
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class NewImageDialog extends MCreatorDialog {
	private final String[] layerTypes = { "Transparency", "Color", "Template", "No Layer" };

	private ResourcePointer selection;
	private final List<ResourcePointer> templatesSorted;

	public NewImageDialog(MCreator window) {
		this(window, new ImageMakerView(window));
	}

	public NewImageDialog(MCreator window, ImageMakerView imageMakerView) {
		super(window, "New Image", true);

		templatesSorted = new ArrayList<>(ImageMakerTexturesCache.CACHE.keySet());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));
		selection = templatesSorted.get(0);
		TextureSelectorDialog templateChooser = new TextureSelectorDialog(templatesSorted, window);

		JPanel settings = new JPanel(new GridBagLayout());
		JPanel controls = new JPanel(new BorderLayout());

		JPanel properties = new JPanel(new GridLayout(1, 2, 5, 5));
		JPanel specialSettings = new JPanel(new CardLayout());
		JPanel constraints = new JPanel(new GridLayout(2, 2, 5, 5));

		//Basic settings
		JComboBox<String> layerType = new JComboBox<>(layerTypes);

		//Filler settings
		JPanel colorSettings = new JPanel(new GridLayout(1, 2, 5, 5));
		JColor colorChoser = new JColor(window);
		colorSettings.add(new JLabel("Base color:"));
		colorSettings.add(colorChoser);

		JPanel templateSettings = new JPanel(new GridLayout(1, 2, 5, 5));
		JButton templateChooserButton = new JButton(
				new ImageIcon(ImageUtils.resize(ImageMakerTexturesCache.CACHE.get(selection).getImage(), 32)));
		templateChooserButton.setMargin(new Insets(0, 0, 0, 0));
		templateSettings.add(new JLabel("Base texture:"));
		templateSettings.add(PanelUtils.totalCenterInPanel(templateChooserButton));

		//Constraints
		JSpinner width = new JSpinner(new SpinnerNumberModel(16, 0, 10000, 1));
		JSpinner height = new JSpinner(new SpinnerNumberModel(16, 0, 10000, 1));

		JButton cancel = new JButton("Cancel");
		JButton ok = new JButton("Create");
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
				imageMakerView.newImage(new Layer((int) width.getValue(), (int) height.getValue(), 0, 0, "Layer"));
				break;
			case 1:
				imageMakerView.newImage(new Layer((int) width.getValue(), (int) height.getValue(), 0, 0, "Layer",
						colorChoser.getColor()));
				break;
			case 2:
				imageMakerView.newImage(new Layer((int) width.getValue(), (int) height.getValue(), 0, 0, "Layer",
						ImageMakerTexturesCache.CACHE.get(selection).getImage()));
				break;
			default:
				imageMakerView.newImage((int) width.getValue(), (int) height.getValue(), "Layer");
				break;
			}

			setVisible(false);
			imageMakerView.showView();
		});

		properties.add(new JLabel("Fill with:"));
		properties.add(layerType);

		specialSettings.add(PanelUtils.totalCenterInPanel(new JLabel("Adds a transparent layer")), layerTypes[0]);
		specialSettings.add(colorSettings, layerTypes[1]);
		specialSettings.add(templateSettings, layerTypes[2]);
		specialSettings.add(PanelUtils.totalCenterInPanel(new JLabel("Adds no layers")), layerTypes[3]);

		constraints.add(new JLabel("Width:"));
		constraints.add(width);
		constraints.add(new JLabel("Height:"));
		constraints.add(height);

		layoutConstraints.gridx = 0;
		layoutConstraints.fill = GridBagConstraints.HORIZONTAL;
		layoutConstraints.weightx = 1.0;
		layoutConstraints.weighty = 1.0;
		layoutConstraints.insets = new Insets(5, 5, 0, 0);

		layoutConstraints.gridheight = 1;
		settings.add(properties, layoutConstraints);
		layoutConstraints.gridheight = 1;
		settings.add(specialSettings, layoutConstraints);
		layoutConstraints.gridheight = 2;
		settings.add(constraints, layoutConstraints);

		controls.add(cancel, BorderLayout.WEST);
		controls.add(ok, BorderLayout.EAST);
		add(PanelUtils.maxMargin(settings, 5, true, true, true, true), BorderLayout.CENTER);
		add(PanelUtils.maxMargin(controls, 5, true, true, true, true), BorderLayout.SOUTH);
		setSize(500, 200);
		setResizable(false);
		setLocationRelativeTo(window);
	}
}