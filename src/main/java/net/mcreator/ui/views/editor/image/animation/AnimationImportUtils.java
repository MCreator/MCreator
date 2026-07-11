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
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.ImageMakerTexturesCache;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.AnimationMakerView;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.GifUtil;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@SuppressWarnings("SuspiciousNameCombination") public class AnimationImportUtils {

	private static final Logger LOG = LogManager.getLogger("Animation Import Utils");

	public static void addFramesFromTemplate(AnimationMakerView timeline) {
		List<ResourcePointer> templatesSorted = new ArrayList<>(
				ImageMakerTexturesCache.CACHE_ANIMATION.keySet().stream().toList());
		templatesSorted.sort(Comparator.comparing(resourcePointer -> resourcePointer.identifier.toString()));

		JPanel panel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(3, 2, 4, 4));

		JLabel preview = new JLabel();
		preview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
				L10N.t("dialog.animation_maker.preview"), 0, 0, timeline.getFont().deriveFont(12.0f), Color.gray));

		JComboBox<ResourcePointer> types = new JComboBox<>();
		templatesSorted.forEach(types::addItem);

		JColor colors = new JColor(timeline.getImageMakerView().getMCreator(), false, true);
		JCheckBox lockSaturation = new JCheckBox();
		ActionListener al = _ -> {
			try {
				int width = ImageIO.read(templatesSorted.get(types.getSelectedIndex()).getStream()).getWidth();
				preview.setIcon(new ImageIcon(ImageUtils.resize(ImageUtils.colorize(
								new TiledImageUtils(templatesSorted.get(types.getSelectedIndex()).getStream(), width,
										width).getIcon(1, 1), colors.getColor(), !lockSaturation.isSelected()).getImage(),
						128)));
			} catch (InvalidTileSizeException | IOException e) {
				LOG.error(e.getMessage(), e);
			}
		};

		al.actionPerformed(new ActionEvent("", 0, null));

		types.addActionListener(al);
		lockSaturation.addActionListener(al);
		colors.addColorSelectedListener(al);

		panel.add("North", L10N.label("dialog.animation_maker.strip_color_choice"));
		panel.add("Center", centerPanel);
		panel.add("South", PanelUtils.centerInPanel(preview));

		centerPanel.add(L10N.label("dialog.animation_maker.template"));
		centerPanel.add(types);
		centerPanel.add(L10N.label("dialog.animation_maker.color"));
		centerPanel.add(colors);
		centerPanel.add(L10N.label("dialog.animation_maker.saturation_lightness_lock"));
		centerPanel.add(lockSaturation);

		if (JOptionPane.showOptionDialog(timeline.getImageMakerView(), panel,
				L10N.t("dialog.animation_maker.add_frames_from_template"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null,
				new String[] { L10N.t("common.add"), UIManager.getString("OptionPane.cancelButtonText") },
				L10N.t("common.add")) == 0) {
			try {
				ResourcePointer rp = templatesSorted.get(types.getSelectedIndex());
				BufferedImage finalImage = ImageUtils.toBufferedImage(
						ImageUtils.colorize(new ImageIcon(ImageIO.read(rp.getStream())), colors.getColor(),
								!lockSaturation.isSelected()).getImage());
				timeline.generateTimelineFromBufferedImage(finalImage,
						FilenameUtilsPatched.removeExtension(rp.toString()));
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	public static void importImagesAsFrames(AnimationMakerView timeline) {
		File[] files = FileDialogs.getMultiOpenDialog(timeline.getImageMakerView().getMCreator(),
				new String[] { ".png", ".gif" });
		for (File file : files) {
			if (file != null) {
				if (file.getName().endsWith(".gif")) {
					gifToFrames(timeline, file);
				} else if (file.getName().endsWith(".png")) {
					try {
						BufferedImage image = TiledImageUtils.convert(ImageIO.read(file), BufferedImage.TYPE_INT_ARGB);
						if (image.getHeight()
								== image.getWidth()) { // This is a single/normal texture, so we can simply import it normally
							timeline.addFrameToTimeline(timeline.createCanvasFromBufferedImage(image,
									FilenameUtilsPatched.removeExtension(file.getName())));
						} else { // This is a strip texture, so we offer the user to colorize the strip before importing it
							int x = Math.min(image.getHeight(), image.getWidth());
							colorizeFramesDialog(timeline, image, new TiledImageUtils(image, x, x).getIcon(1, 1),
									file.getName());
						}
					} catch (InvalidTileSizeException | IOException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}
		}
	}

	public static void colorizeFramesDialog(AnimationMakerView timeline, BufferedImage bufferedImage,
			ImageIcon previewIcon, String name) throws IOException {
		JPanel optionsPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(4, 2, 4, 4));

		JLabel preview = new JLabel(new ImageIcon(new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)));
		preview.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1), "Preview", 0, 0,
						timeline.getFont().deriveFont(12.0f), Color.gray));

		JCheckBox colorize = new JCheckBox();
		JColor colors = new JColor(timeline.getImageMakerView().getMCreator(), false, true);
		JCheckBox lockSaturation = new JCheckBox();

		if (colorize.isSelected())
			preview.setIcon(new ImageIcon(ImageUtils.resize(
					ImageUtils.colorize(previewIcon, colors.getColor(), !lockSaturation.isSelected()).getImage(),
					128)));
		else
			preview.setIcon(new ImageIcon(ImageUtils.resize(previewIcon.getImage(), 128)));

		ActionListener al = _ -> {
			if (colorize.isSelected()) {
				preview.setIcon(new ImageIcon(ImageUtils.resize(
						ImageUtils.colorize(previewIcon, colors.getColor(), !lockSaturation.isSelected()).getImage(),
						128)));
			} else {
				preview.setIcon(new ImageIcon(ImageUtils.resize(previewIcon.getImage(), 128)));
			}
			updateColorParameters(colorize, lockSaturation, colors);
		};
		updateColorParameters(colorize, lockSaturation, colors);

		colors.addColorSelectedListener(al);
		lockSaturation.addActionListener(al);
		colorize.addActionListener(al);

		centerPanel.add(L10N.label("dialog.animation_maker.strip"));
		centerPanel.add(new JLabel(StringUtils.abbreviateString(name, 25)));
		centerPanel.add(L10N.label("dialog.animation_maker.colorize"));
		centerPanel.add(colorize);
		centerPanel.add(L10N.label("dialog.animation_maker.color"));
		centerPanel.add(colors);
		centerPanel.add(L10N.label("dialog.animation_maker.saturation_lightness_lock"));
		centerPanel.add(lockSaturation);

		optionsPanel.add("North", L10N.label("dialog.animation_maker.optional_colorization"));
		optionsPanel.add("Center", centerPanel);
		optionsPanel.add("South", preview);

		int answer = JOptionPane.showOptionDialog(timeline.getImageMakerView(), optionsPanel,
				L10N.t("dialog.animation_maker.add_frames_from_file"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE, null, new String[] { L10N.t("common.add"), L10N.t("common.cancel") },
				L10N.t("common.add"));

		if (answer == 0) {
			BufferedImage finalImage = bufferedImage;
			if (colorize.isSelected()) {
				finalImage = ImageUtils.toBufferedImage(
						ImageUtils.colorize(new ImageIcon(bufferedImage), colors.getColor(),
								!lockSaturation.isSelected()).getImage());
			}
			timeline.generateTimelineFromBufferedImage(finalImage, FilenameUtilsPatched.removeExtension(name));
		}
	}

	private static void updateColorParameters(JCheckBox colorize, JCheckBox lockSaturation, JColor colors) {
		if (colorize.isSelected()) {
			lockSaturation.setEnabled(true);
			colors.setEnabled(true);
		} else {
			lockSaturation.setEnabled(false);
			colors.setEnabled(false);
		}
	}

	public static void gifToFrames(AnimationMakerView timeline, File file) {
		if (file != null) {
			ProgressDialog dial = new ProgressDialog(timeline.getImageMakerView().getMCreator(),
					L10N.t("dialog.animation_maker.gif_importing"));
			Thread t = new Thread(() -> {
				try {
					ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.animation_maker.gif_reading"));
					dial.addProgressUnit(p1);
					Image[] frames = GifUtil.readAnimatedGif(file);
					if (frames.length > 0)
						p1.markStateOk();
					else {
						p1.markStateError();
						dial.hideDialog();

						JOptionPane.showMessageDialog(timeline.getImageMakerView(),
								L10N.t("dialog.animation_maker.gif_format_unsupported"), L10N.t("common.warning"),
								JOptionPane.ERROR_MESSAGE);

						return;
					}
					ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
							L10N.t("dialog.animation_maker.gif_processing"));
					dial.addProgressUnit(p2);
					for (int i = 0; i < frames.length; i++) {
						int finalI = i;
						SwingUtilities.invokeLater(() -> timeline.addFrameToTimeline(
								timeline.createCanvasFromBufferedImage(ImageUtils.toBufferedImage(frames[finalI]),
										FilenameUtilsPatched.removeExtension(file.getName()) + finalI)));
						p2.setPercent((int) (i / (float) frames.length * 100));
					}
					p2.markStateOk();
					dial.hideDialog();
				} catch (Exception e) {
					dial.hideDialog();
					LOG.error(e.getMessage(), e);
				}

			}, "GIFFramesLoader");
			t.start();
			dial.setVisible(true);
		}
	}
}
