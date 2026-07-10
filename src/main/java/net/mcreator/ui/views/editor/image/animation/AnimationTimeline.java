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

import com.google.gson.*;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.dialogs.file.FileDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.workspace.resources.TextureType;
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
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationTimeline extends JPanel {

	protected static final Logger LOG = LogManager.getLogger("Animation Timeline");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private final ImageMakerView imv;

	private final DefaultListModel<Canvas> timelinevector = new DefaultListModel<>();
	private final JList<Canvas> timeline = new JList<>(timelinevector);

	private int animindex = 0;
	private boolean playanim = true;

	private final Thread animator;

	private boolean active;

	public AnimationTimeline(ImageMakerView imv) {
		this.imv = imv;
		setLayout(new BorderLayout());

		JToolBar controls = new JToolBar();
		controls.setFloatable(false);

		animator = new Thread(() -> {
			active = true;
			while (active) {
				if (timelinevector.getSize() > 0 && playanim) {
					animindex++;
					if (animindex >= timelinevector.getSize())
						animindex = 0;
					SwingUtilities.invokeLater(() -> {
						changeFrame(timelinevector.getElementAt(animindex));
						timeline.setSelectedIndex(animindex);
						timeline.repaint();
					});
				}

				try {
					//noinspection BusyWait
					Thread.sleep(imv.getAnimationSettings().getFrameDuration() * 50L);
				} catch (InterruptedException ignored) {
				}
			}
		}, "AnimationRenderer");

		JButton play = new JButton("");
		play.setIcon(UIRES.get("16px.play"));
		play.addActionListener(_ -> {
			if (!animator.isAlive())
				animator.start();
			else
				playanim = true;
		});
		controls.add(play);

		JButton pause = new JButton("");
		pause.addActionListener(_ -> playanim = false);
		pause.setIcon(UIRES.get("16px.pause"));
		controls.add(pause);

		JButton stop = new JButton("");
		stop.addActionListener(_ -> {
			animindex = 0;
			playanim = false;
			timeline.repaint();
		});
		stop.setIcon(UIRES.get("16px.stopanimation"));
		controls.add(stop);

		controls.addSeparator();

		JButton next = L10N.button("dialog.animation_maker.next_frame");
		next.addActionListener(_ -> {
			if (!timelinevector.isEmpty()) {
				animindex++;
				if (animindex >= timelinevector.getSize())
					animindex--;
				changeFrame(timelinevector.getElementAt(animindex));
				timeline.repaint();
			}
		});
		next.setIcon(UIRES.get("16px.fwd"));
		controls.add(next);

		JButton prev = L10N.button("dialog.animation_maker.previous_frame");
		prev.addActionListener(_ -> {
			if (!timelinevector.isEmpty()) {
				animindex--;
				if (animindex < 0)
					animindex = 0;
				changeFrame(timelinevector.getElementAt(animindex));
				timeline.repaint();
			}
		});
		prev.setIcon(UIRES.get("16px.rwd"));
		controls.add(prev);

		JPanel timelinePanel = new JPanel(new BorderLayout());
		timelinePanel.setOpaque(false);
		ComponentUtils.makeSection(timelinePanel, L10N.t("dialog.animation_maker.animation_timeline"));

		JToolBar timelinebar = new JToolBar();
		timelinebar.setFloatable(false);
		JButton add = L10N.button("dialog.animation_maker.add_new_frame");
		add.addActionListener(_ -> {
			addFrameFromEmptyLayer();
		});
//		add.addActionListener(_ -> {
//			File[] frames = FileDialogs.getMultiOpenDialog(imv.getMCreator(), new String[] { ".png" });
//			if (frames != null) {
//				Arrays.stream(frames).forEach(frame -> {
//					try {
//						timelinevector.addElement(createCanvasFromBufferedImage(ImageIO.read(frame),
//								FilenameUtilsPatched.removeExtension(frame.getName())));
//					} catch (IOException e) {
//						LOG.error(e.getMessage(), e);
//					}
//				});
//			}
//		});
		add.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add);

		JButton addFromTemplates = L10N.button("dialog.animation_maker.add_frames_from_template");
		addFromTemplates.addActionListener(_ -> AnimationImportDialogs.addFramesFromTemplate(this));
		addFromTemplates.setIcon(UIRES.get("18px.add"));
		timelinebar.add(addFromTemplates);

		JButton addFromStrip = L10N.button("dialog.animation_maker.add_frames_from_strip");
		addFromStrip.addActionListener(_ -> AnimationImportDialogs.addFramesFromStrip(this));
		addFromStrip.setIcon(UIRES.get("18px.add"));
		timelinebar.add(addFromStrip);

		JButton addFromGif = L10N.button("dialog.animation_maker.add_frames_from_gif");
		addFromGif.addActionListener(_ -> {
			File frame = FileDialogs.getOpenDialog(imv.getMCreator(), new String[] { ".gif" });
			if (frame != null) {
				ProgressDialog dial = new ProgressDialog(imv.getMCreator(), L10N.t("dialog.animation_maker.gif_importing"));
				Thread t = new Thread(() -> {
					try {
						ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.animation_maker.gif_reading"));
						dial.addProgressUnit(p1);
						Image[] frames = GifUtil.readAnimatedGif(frame);
						if (frames.length > 0)
							p1.markStateOk();
						else {
							p1.markStateError();
							dial.hideDialog();

							JOptionPane.showMessageDialog(imv, L10N.t("dialog.animation_maker.gif_format_unsupported"),
									L10N.t("common.warning"), JOptionPane.ERROR_MESSAGE);

							return;
						}
						ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit(
								L10N.t("dialog.animation_maker.gif_processing"));
						dial.addProgressUnit(p2);
						for (int i = 0; i < frames.length; i++) {
							int finalI = i;
							SwingUtilities.invokeLater(() -> timelinevector.addElement(
									createCanvasFromBufferedImage(ImageUtils.toBufferedImage(frames[finalI]),
											FilenameUtilsPatched.removeExtension(frame.getName()) + finalI)));
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
		});
		addFromGif.setIcon(UIRES.get("18px.add"));
		timelinebar.add(addFromGif);

		JButton remove = L10N.button("dialog.animation_maker.remove_selected_frames");
		remove.addActionListener(_ -> {
			if (timeline.getSelectedValue() != null)
				timeline.getSelectedValuesList().forEach(timelinevector::removeElement);
		});
		remove.setIcon(UIRES.get("18px.remove"));
		timelinebar.add(remove);

		timelinePanel.add("North", timelinebar);

		timeline.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		timeline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		timeline.setVisibleRowCount(1);
		timeline.setCellRenderer(new TimelineRenderer(this));
		timeline.setOpaque(false);
		JScrollPane pan = new JScrollPane(timeline);
		pan.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		pan.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		pan.setOpaque(false);
		pan.getViewport().setOpaque(false);

		timelinePanel.add("Center", pan);

		timelinePanel.setPreferredSize(new Dimension(9000, 260));

		JButton save = L10N.button("dialog.animation_maker.save_animated_texture");
		save.setMargin(new Insets(1, 40, 1, 40));
		save.setBackground(Theme.current().getInterfaceAccentColor());
		save.setForeground(Theme.current().getSecondAltBackgroundColor());
		save.setFocusPainted(false);
		save.addActionListener(_ -> use());

		add("North", controls);
		add("Center", timelinePanel);
	}

	protected void use() {
		if (timelinevector.isEmpty())
			return;
		TextureType[] options = TextureType.getSupportedTypes(imv.getMCreator().getWorkspace(), false);
		int n = JOptionPane.showOptionDialog(imv, L10N.t("dialog.animation_maker.kind_of_texture"),
				L10N.t("dialog.animation_maker.type_of_texture"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n < 0)
			return;

		String namec = JOptionPane.showInputDialog(L10N.t("dialog.animation_maker.enter_texture_name"));
		if (namec != null) {
			namec = RegistryNameFixer.fix(namec);
			File exportFile = imv.getMCreator().getFolderManager().getTextureFile(namec, options[n]);

			if (exportFile.isFile()) {
				JOptionPane.showMessageDialog(imv, L10N.t("dialog.animation_maker.texture_already_exists", options[n]),
						L10N.t("dialog.animation_maker.resource_error"), JOptionPane.ERROR_MESSAGE);
			} else {
				Object[] possibilities = { "4 x 4", "8 x 8", "16 x 16", "32 x 32", "64 x 64", "128 x 128", "256 x 256",
						"512 x 512" };
				String s = (String) JOptionPane.showInputDialog(imv, L10N.t("dialog.animation_maker.animation_size"),
						L10N.t("dialog.animation_maker.size_selection"), JOptionPane.PLAIN_MESSAGE, null, possibilities,
						"16 x 16");
				int sizetwocubes = 16;
				if (s != null) {
					sizetwocubes = switch (s) {
						case "4 x 4" -> 4;
						case "8 x 8" -> 8;
						case "32 x 32" -> 32;
						case "64 x 64" -> 64;
						case "128 x 128" -> 128;
						case "256 x 256" -> 256;
						case "512 x 512" -> 512;
						default -> 16;
					};
				}
				Image image = makeAnimationIcon(timelinevector.getSize(), timelinevector, sizetwocubes).getImage();
				String mcmetacode = generateAnimationMcmeta(imv.getAnimationSettings().getFrameDuration(),
						timelinevector.size(), imv.getAnimationSettings().doesInterpolate());
				FileIO.writeStringToFile(mcmetacode, new File(exportFile.getAbsolutePath() + ".mcmeta"));
				FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image), exportFile);
			}
		}
	}

	protected void generateTimelineFromBufferedImage(BufferedImage bufferedImage, String name, boolean colorize,
			boolean colorizerType, Color color) {
		BufferedImage b;
		if (colorize)
			b = ImageUtils.toBufferedImage(
					ImageUtils.colorize(new ImageIcon(bufferedImage), color, colorizerType).getImage());
		else
			b = bufferedImage;
		int x = Math.min(b.getHeight(), b.getWidth());
		try {
			TiledImageUtils tiledImageUtils = new TiledImageUtils(b, x, x);
			for (int i = 1; i <= tiledImageUtils.getWidthInTiles(); i++) {
				for (int j = 1; j <= tiledImageUtils.getHeightInTiles(); j++) {
					BufferedImage buf = ImageUtils.toBufferedImage(tiledImageUtils.getIcon(i, j).getImage());
					addFrameToTimeline(createCanvasFromBufferedImage(buf, name));
				}
			}
		} catch (InvalidTileSizeException e) {
			LOG.warn("Invalid tile size", e);
		}
	}

	public void addFrameToTimeline(Canvas canvas) {
		timelinevector.addElement(canvas);
	}

	private void addFrameFromEmptyLayer() {
		Layer layer = new Layer(16, 16, 0, 0, "Layer");
		Canvas canvas = new Canvas(imv, 16, 16);
		canvas.add(layer);
		addFrameToTimeline(canvas);
	}

	public Canvas createCanvasFromBufferedImage(BufferedImage bufferedImage, String name) {
		Layer layer = Layer.toLayer(bufferedImage, name);
		Canvas canvas = new Canvas(imv, layer.getWidth(), layer.getHeight());
		canvas.add(layer);
		return canvas;
	}

	public void changeFrame(Canvas newCanvas) {
		imv.getCanvasRenderer().setCanvas(newCanvas);
		imv.getCanvasRenderer().repaint();
		imv.getToolPanel().setCanvas(newCanvas);
		imv.getLayerPanel().setCanvas(newCanvas);
		imv.getLayerPanel().updateSelection();
		imv.repaint();
	}

	private ImageIcon makeAnimationIcon(int stevilo, DefaultListModel<Canvas> timelinevector, int size) {
		BufferedImage resizedImage = new BufferedImage(size, size * stevilo, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		for (int i = 0; i < timelinevector.getSize(); i++)
			g.drawImage(timelinevector.get(i).render(), 0, i * size, size, size, new JLabel());
		g.dispose();
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(resizedImage.getSource()));
	}

	private String generateAnimationMcmeta(int frametime_tick, int framenum_num, boolean interpolate) {
		JsonObject mcmeta = new JsonObject();
		JsonObject animation = new JsonObject();
		animation.add("frametime", new JsonPrimitive(frametime_tick));
		animation.add("interpolate", new JsonPrimitive(interpolate));
		JsonArray frames = new JsonArray();
		for (int i = 0; i < framenum_num; i++)
			frames.add(i);
		animation.add("frames", frames);
		mcmeta.add("animation", animation);
		return gson.toJson(mcmeta);
	}

	public int getAnimationIndex() {
		return animindex;
	}

	public void setAnimationIndex(int animindex) {
		this.animindex = animindex;
	}

	public ImageMakerView getImageMakerView() {
		return imv;
	}
}
