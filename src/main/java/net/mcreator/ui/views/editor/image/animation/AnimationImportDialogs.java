/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.views.editor.image.animation;

import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationImportDialogs {

	public static void addFramesFromTemplate(AnimationTimeline timeline) {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.animations", "png");

		JPanel panel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(3, 2, 4, 4));

		JLabel lab1 = L10N.label("dialog.animation_maker.template_color_choice");
		JLabel lab2 = L10N.label("dialog.animation_maker.template");
		JLabel lab3 = L10N.label("dialog.animation_maker.color");
		JLabel lab4 = L10N.label("dialog.animation_maker.saturation_lightness_lock");

		JLabel preview = new JLabel();
		preview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
				L10N.t("dialog.animation_maker.preview"), 0, 0, timeline.getFont().deriveFont(12.0f), Color.gray));

		JComboBox<ResourcePointer> types = new JComboBox<>();
		templatesSorted.forEach(types::addItem);

		JColor colors = new JColor(timeline.getImageMakerView().getMCreator(), false, true);
		JCheckBox cbox = new JCheckBox();
		ActionListener al = _ -> {
			try {
				int width = ImageIO.read(templatesSorted.get(types.getSelectedIndex()).getStream()).getWidth();
				preview.setIcon(new ImageIcon(ImageUtils.resize(ImageUtils.colorize(
						new TiledImageUtils(templatesSorted.get(types.getSelectedIndex()).getStream(), width,
								width).getIcon(1, 1), colors.getColor(), !cbox.isSelected()).getImage(), 128)));
			} catch (InvalidTileSizeException | IOException e) {
				AnimationTimeline.LOG.error(e.getMessage(), e);
			}
		};

		al.actionPerformed(new ActionEvent("", 0, null));

		types.addActionListener(al);
		cbox.addActionListener(al);
		colors.addColorSelectedListener(al);

		panel.add("North", lab1);
		panel.add("Center", centerPanel);
		panel.add("South", PanelUtils.centerInPanel(preview));

		centerPanel.add(lab2);
		centerPanel.add(types);
		centerPanel.add(lab3);
		centerPanel.add(colors);
		centerPanel.add(lab4);
		centerPanel.add(cbox);

		if (JOptionPane.showOptionDialog(timeline.getImageMakerView(), panel,
				L10N.t("dialog.animation_maker.add_frames_from_template"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null,
				new String[] { L10N.t("common.add"), UIManager.getString("OptionPane.cancelButtonText") },
				L10N.t("common.add")) == 0) {
			try {
				ResourcePointer rp = templatesSorted.get(types.getSelectedIndex());
				timeline.generateTimelineFromBufferedImage(
						TiledImageUtils.convert(ImageIO.read(rp.getStream()), BufferedImage.TYPE_INT_ARGB),
						rp.toString(), true, !cbox.isSelected(), colors.getColor());
			} catch (IOException e) {
				AnimationTimeline.LOG.error(e.getMessage(), e);
			}
		}
	}

	public void addFramesFromStrip(AnimationTimeline timeline) {
		AtomicReference<File> f = new AtomicReference<>();

		JPanel od = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(4, 2, 4, 4));

		JLabel lab1 = L10N.label("dialog.animation_maker.strip_color_choice");
		JLabel lab2 = L10N.label("dialog.animation_maker.strip");
		JLabel lab3 = L10N.label("dialog.animation_maker.color");
		JLabel lab4 = L10N.label("dialog.animation_maker.saturation_lightness_lock");
		JLabel lab5 = L10N.label("dialog.animation_maker.colorize");

		JLabel preview = new JLabel(new ImageIcon(new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)));
		preview.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1), "Preview", 0, 0,
						timeline.getFont().deriveFont(12.0f), Color.gray));

		JButton selectFile = new JButton("...");
		JColor colors = new JColor(timeline.getImageMakerView().getMCreator(), false, true);
		JCheckBox cbox = new JCheckBox();
		JCheckBox cbox2 = new JCheckBox();

		AtomicReference<TiledImageUtils> tilImgUtl = new AtomicReference<>();

		selectFile.addActionListener(_ -> {
			f.set(FileDialogs.getOpenDialog(timeline.getImageMakerView().getMCreator(), new String[] { ".png" }));
			try {
				if (f.get() != null) {
					BufferedImage imge = TiledImageUtils.convert(ImageIO.read(f.get()), BufferedImage.TYPE_INT_ARGB);
					int x = Math.min(imge.getHeight(), imge.getWidth());
					selectFile.setText(StringUtils.abbreviateString(f.get().getName(), 25));
					tilImgUtl.set(new TiledImageUtils(imge, x, x));
					if (cbox2.isSelected())
						preview.setIcon(new ImageIcon(ImageUtils.resize(
								ImageUtils.colorize(tilImgUtl.get().getIcon(1, 1), colors.getColor(),
										!cbox.isSelected()).getImage(), 128)));
					else
						preview.setIcon(
								new ImageIcon(ImageUtils.resize(tilImgUtl.get().getIcon(1, 1).getImage(), 128)));
				}
			} catch (InvalidTileSizeException | IOException e) {
				AnimationTimeline.LOG.error(e.getMessage(), e);
			}
		});

		ActionListener al = _ -> {
			if (f.get() != null && tilImgUtl.get() != null)
				if (cbox2.isSelected())
					preview.setIcon(new ImageIcon(ImageUtils.resize(
							ImageUtils.colorize(tilImgUtl.get().getIcon(1, 1), colors.getColor(), !cbox.isSelected())
									.getImage(), 128)));
				else
					preview.setIcon(new ImageIcon(ImageUtils.resize(tilImgUtl.get().getIcon(1, 1).getImage(), 128)));
		};

		colors.addColorSelectedListener(al);
		cbox.addActionListener(al);
		cbox2.addActionListener(al);

		od.add("North", lab1);
		od.add("Center", centerPanel);
		od.add("South", preview);

		centerPanel.add(lab2);
		centerPanel.add(selectFile);
		centerPanel.add(lab3);
		centerPanel.add(colors);
		centerPanel.add(lab4);
		centerPanel.add(cbox);
		centerPanel.add(lab5);
		centerPanel.add(cbox2);

		if (JOptionPane.showOptionDialog(timeline.getImageMakerView(), od, L10N.t("dialog.animation_maker.add_frames_from_file"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "Add", "Cancel" },
				"Add") == 0) {
			try {
				timeline.generateTimelineFromBufferedImage(TiledImageUtils.convert(ImageIO.read(f.get()), BufferedImage.TYPE_INT_ARGB), FilenameUtilsPatched.removeExtension(f.get().getName()), cbox2.isSelected(),
						!cbox.isSelected(), colors.getColor());
			} catch (IOException e) {
				AnimationTimeline.LOG.error(e.getMessage(), e);
			}
		}
	}
}
