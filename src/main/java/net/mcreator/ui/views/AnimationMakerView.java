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

package net.mcreator.ui.views;

import com.google.gson.*;
import net.mcreator.io.FileIO;
import net.mcreator.io.ResourcePointer;
import net.mcreator.io.TemplatesLoader;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.JColor;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.FileDialogs;
import net.mcreator.ui.dialogs.ProgressDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.util.GifUtil;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
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
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationMakerView extends ViewBase {

	private static final Logger LOG = LogManager.getLogger("Animation Maker View");

	private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private final DefaultListModel<AnimationMakerView.AnimationFrame> timelinevector = new DefaultListModel<>();
	private final JList<AnimationMakerView.AnimationFrame> timeline = new JList<>(timelinevector);
	private final JSpinner bd1 = new JSpinner(new SpinnerNumberModel(2, 1, 10000, 1));

	private int animindex = 0;
	private boolean playanim = true;

	private Thread animator;

	private int width = 16;
	private boolean active;

	private final JCheckBox interpolate = new JCheckBox();

	private int zoom = 400;

	public AnimationMakerView(final MCreator fra) {
		super(fra);

		JPanel editor = new JPanel(new BorderLayout());
		editor.setOpaque(false);

		JLabel prv = new JLabel(new EmptyIcon.ColorIcon(zoom, zoom, new Color(0x6B6B6B)));

		JPanel prvmg = new JPanel(new BorderLayout());
		prvmg.setOpaque(false);

		JScrollPane sp = new JScrollPane(PanelUtils.maxMargin(prv, 0, false, false, false, false));
		sp.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		sp.getViewport().setOpaque(false);
		sp.setOpaque(false);
		sp.setBorder(null);
		sp.getViewport().setBorder(null);

		sp.addMouseWheelListener(event -> {
			int x = zoom + (event.getWheelRotation() * 48);
			if (x <= 16)
				x = 16;
			if (x > 1024)
				x = 1024;

			zoom = x;
			try {
				prv.setIcon(new ImageIcon(ImageUtils.resize(timelinevector.getElementAt(animindex).image, zoom)));
			} catch (Exception ignored) {
			}
		});

		prvmg.add("Center", sp);

		JToolBar controls = new JToolBar();
		controls.setFloatable(false);
		prvmg.add("South", controls);

		JPanel preview2 = new JPanel(new GridLayout()) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		preview2.setOpaque(false);
		preview2.add(prvmg);

		interpolate.setOpaque(false);

		JPanel settings = new JPanel(new GridLayout(2, 2, 15, 20));
		settings.setOpaque(false);
		settings.add(L10N.label("dialog.animation_maker.frame_duration"));
		settings.add(bd1);
		settings.add(L10N.label("dialog.animation_maker.interpolate_frame"));
		settings.add(interpolate);

		JComponent stp = PanelUtils.centerInPanel(settings);
		stp.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.animation_maker.settings"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		editor.add("Center", PanelUtils.centerAndEastElement(preview2, stp));

		animator = new Thread(() -> {
			active = true;
			while (active) {
				if (timelinevector.getSize() > 0 && playanim) {
					animindex++;
					if (animindex >= timelinevector.getSize())
						animindex = 0;
					SwingUtilities.invokeLater(() -> {
						prv.setIcon(
								new ImageIcon(ImageUtils.resize(timelinevector.getElementAt(animindex).image, zoom)));
						timeline.repaint();
					});
					try {
						Thread.sleep(((Integer) bd1.getValue()) * (50));
					} catch (InterruptedException e) {
						LOG.error(e.getMessage(), e);
					}
				}
			}
		});

		JButton play = new JButton("");
		play.setIcon(UIRES.get("16px.play"));
		play.addActionListener(event -> {
			if (!animator.isAlive())
				animator.start();
			else
				playanim = true;
		});
		controls.add(play);

		JButton pause = new JButton("");
		pause.addActionListener(event -> playanim = false);
		pause.setIcon(UIRES.get("16px.pause"));
		controls.add(pause);

		JButton stop = new JButton("");
		stop.addActionListener(event -> {
			animindex = 0;
			playanim = false;
			timeline.repaint();
		});
		stop.setIcon(UIRES.get("16px.stop"));
		controls.add(stop);

		controls.addSeparator();

		JButton next = L10N.button("dialog.animation_maker.next_frame");
		next.addActionListener(event -> {
			animindex++;
			if (animindex >= timelinevector.getSize())
				animindex--;
			prv.setIcon(new ImageIcon(ImageUtils.resize(timelinevector.getElementAt(animindex).image, zoom)));
			timeline.repaint();
		});
		next.setIcon(UIRES.get("16px.fwd"));
		controls.add(next);

		JButton prev = L10N.button("dialog.animation_maker.previous_frame");
		prev.addActionListener(event -> {
			animindex--;
			if (animindex < 0)
				animindex = 0;
			prv.setIcon(new ImageIcon(ImageUtils.resize(timelinevector.getElementAt(animindex).image, zoom)));
			timeline.repaint();
		});
		prev.setIcon(UIRES.get("16px.rwd"));
		controls.add(prev);

		JPanel timelinee = new JPanel(new BorderLayout());
		timelinee.setOpaque(false);
		timelinee.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"), 2),
				L10N.t("dialog.animation_maker.animation_timeline"), 0, 0, getFont().deriveFont(12.0f),
				(Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR")));

		JToolBar timelinebar = new JToolBar();
		timelinebar.setFloatable(false);
		JButton add = L10N.button("dialog.animation_maker.add_frames");
		add.addActionListener(event -> {
			File[] frames = FileDialogs.getMultiOpenDialog(fra, new String[] { ".png" });
			if (frames != null) {
				Arrays.stream(frames).forEach(frame -> {
					try {
						timelinevector.addElement(new AnimationFrame(ImageIO.read(frame)));
					} catch (IOException e) {
						LOG.error(e.getMessage(), e);
					}
				});
			}
		});
		add.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add);

		JButton add3 = L10N.button("dialog.animation_maker.add_frames_from_template");
		add3.addActionListener(event -> addFramesFromTemplate());
		add3.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add3);

		JButton add4 = L10N.button("dialog.animation_maker.add_frames_from_strip");
		add4.addActionListener(event -> addFramesFromStrip());
		add4.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add4);

		JButton add2 = L10N.button("dialog.animation_maker.add_frames_from_gif");
		add2.addActionListener(event -> {
			File frame = FileDialogs.getOpenDialog(fra, new String[] { ".gif" });
			if (frame != null) {
				ProgressDialog dial = new ProgressDialog(fra, "GIF import");
				Thread t = new Thread(() -> {
					try {
						ProgressDialog.ProgressUnit p1 = new ProgressDialog.ProgressUnit("Reading GIF");
						dial.addProgress(p1);
						BufferedImage[] frames = GifUtil.readAnimatedGif(frame);
						if (frames.length > 0)
							p1.ok();
						else {
							p1.err();
							dial.setTopInfoText(L10N.t("dialog.animation_maker.gif_format_unsupported"));
							Thread.sleep(3500);
							dial.hideAll();
							return;
						}
						dial.refreshDisplay();
						int frameCount = frames.length;
						ProgressDialog.ProgressUnit p2 = new ProgressDialog.ProgressUnit("Processing GIF");
						dial.addProgress(p2);
						for (int i = 0; i < frameCount; i++) {
							int finalI = i;
							SwingUtilities
									.invokeLater(() -> timelinevector.addElement(new AnimationFrame(frames[finalI])));
							p2.setPercent((int) (((float) i / (float) frameCount) * 100.0f));
						}
						p2.ok();
						dial.refreshDisplay();
						dial.hideAll();
					} catch (Exception e) {
						dial.hideAll();
						LOG.error(e.getMessage(), e);
					}

				});
				t.start();
				dial.setVisible(true);
			}
		});
		add2.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add2);

		JButton remove = L10N.button("dialog.animation_maker.remove_selected_frames");
		remove.addActionListener(event -> {
			if (timeline.getSelectedValue() != null)
				timeline.getSelectedValuesList().forEach(timelinevector::removeElement);
		});
		remove.setIcon(UIRES.get("18px.remove"));
		timelinebar.add(remove);

		timelinebar.addSeparator();

		timelinee.add("North", timelinebar);

		timeline.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		timeline.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		timeline.setVisibleRowCount(1);
		timeline.setCellRenderer(new ComboBoxRenderer());
		timeline.setOpaque(false);
		JScrollPane pan = new JScrollPane(timeline);
		pan.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		pan.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		pan.setOpaque(false);
		pan.getViewport().setOpaque(false);

		timelinee.add("Center", pan);

		timelinee.setPreferredSize(new Dimension(9000, 260));

		editor.add("South", timelinee);

		JButton save = L10N.button("dialog.animation_maker.save_animated_texture");
		save.setMargin(new Insets(1, 40, 1, 40));
		save.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		save.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		save.setFocusPainted(false);
		add("North", PanelUtils.maxMargin(
				PanelUtils.westAndEastElement(new JEmptyBox(0, 0), PanelUtils.centerInPanelPadding(save, 0, 0)), 5,
				true, true, false, true));
		save.addActionListener(event -> use());

		add("Center", editor);
	}

	protected void use() {
		Object[] options = { "Block", "Item", "Other" };
		int n = JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.animation_maker.kind_of_texture"),
				L10N.t("dialog.animation_maker.type_of_texture"), JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		if (n < 0)
			return;

		String namec = JOptionPane.showInputDialog(L10N.t("dialog.animation_maker.enter_texture_name"));
		if (namec != null) {
			File exportFile;
			namec = RegistryNameFixer.fix(namec);
			if (n == 0)
				exportFile = mcreator.getFolderManager().getTextureFileFromType(namec, "block");
			else if (n == 1)
				exportFile = mcreator.getFolderManager().getTextureFileFromType(namec, "item");
			else
				exportFile = mcreator.getFolderManager().getTextureFileFromType(namec, "other");

			if (exportFile.isFile()) {
				JOptionPane.showMessageDialog(mcreator,
						L10N.t("dialog.animation_maker.texture_already_exists", options[n]),
						L10N.t("dialog.animation_maker.resource_error"), JOptionPane.ERROR_MESSAGE);
			} else {
				Object[] possibilities = { "4 x 4", "8 x 8", "16 x 16", "32 x 32", "64 x 64", "128 x 128", "256 x 256",
						"512 x 512" };
				String s = (String) JOptionPane
						.showInputDialog(mcreator, L10N.t("dialog.animation_maker.animation_size"),
								L10N.t("dialog.animation_maker.size_selection"), JOptionPane.PLAIN_MESSAGE, null,
								possibilities, "16 x 16");
				int sizetwocubes = 16;
				if (s != null) {
					switch (s) {
					case "4 x 4":
						sizetwocubes = 4;
						break;
					case "8 x 8":
						sizetwocubes = 8;
						break;
					case "16 x 16":
						sizetwocubes = 16;
						break;
					case "32 x 32":
						sizetwocubes = 32;
						break;
					case "64 x 64":
						sizetwocubes = 64;
						break;
					case "128 x 128":
						sizetwocubes = 128;
						break;

					case "256 x 256":
						sizetwocubes = 256;
						break;

					case "512 x 512":
						sizetwocubes = 512;
						break;
					}
				}
				Image image = makeAnimationIcon(timelinevector.getSize(), timelinevector, sizetwocubes).getImage();
				String mcmetacode = generateAnimationMcmeta((Integer) bd1.getValue(), timelinevector.size(),
						interpolate.isSelected());
				FileIO.writeStringToFile(mcmetacode, new File(exportFile.getAbsolutePath() + ".mcmeta"));
				FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image), exportFile);
			}
		}
	}

	private void addFramesFromTemplate() {
		List<ResourcePointer> templatesSorted = TemplatesLoader.loadTemplates("textures.animations", "png");

		JPanel od = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(3, 2, 4, 4));

		JLabel lab1 = L10N.label("dialog.animation_maker.template_color_choice");
		JLabel lab2 = L10N.label("dialog.animation_maker.template");
		JLabel lab3 = L10N.label("dialog.animation_maker.color");
		JLabel lab4 = L10N.label("dialog.animation_maker.saturation_lightness_lock");

		JLabel preview = new JLabel();
		preview.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1),
				L10N.t("dialog.animation_maker.preview"), 0, 0, getFont().deriveFont(12.0f), Color.gray));

		JComboBox<ResourcePointer> types = new JComboBox<>();
		templatesSorted.forEach(types::addItem);

		JColor colors = new JColor(mcreator);
		JCheckBox cbox = new JCheckBox();
		ActionListener al = event -> {
			try {
				width = ImageIO.read(templatesSorted.get(types.getSelectedIndex()).getStream()).getWidth();
				preview.setIcon(new ImageIcon(ImageUtils.resize(ImageUtils.colorize(
						new TiledImageUtils(templatesSorted.get(types.getSelectedIndex()).getStream(), width, width)
								.getIcon(1, 1), colors.getColor(), !cbox.isSelected()).getImage(), 128)));
			} catch (InvalidTileSizeException | IOException e) {
				LOG.error(e.getMessage(), e);
			}
		};

		al.actionPerformed(new ActionEvent("", 0, null));

		types.addActionListener(al);
		cbox.addActionListener(al);
		colors.setColorSelectedListener(al);

		od.add("North", lab1);
		od.add("Center", centerPanel);
		od.add("South", PanelUtils.centerInPanel(preview));

		centerPanel.add(lab2);
		centerPanel.add(types);
		centerPanel.add(lab3);
		centerPanel.add(colors);
		centerPanel.add(lab4);
		centerPanel.add(cbox);

		if (JOptionPane.showOptionDialog(mcreator, od, L10N.t("dialog.animation_maker.add_frames_from_template"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "Add", "Cancel" },
				"Add") == 0) {
			try {
				BufferedImage imge = TiledImageUtils
						.convert(ImageIO.read(templatesSorted.get(types.getSelectedIndex()).getStream()),
								BufferedImage.TYPE_INT_ARGB);
				addFramesFromBufferedImage(imge, true, !cbox.isSelected(), colors.getColor());
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void addFramesFromStrip() {
		AtomicReference<File> f = new AtomicReference<>();

		JPanel od = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout(4, 2, 4, 4));

		JLabel lab1 = L10N.label("dialog.animation_maker.strip_color_choice");
		JLabel lab2 = L10N.label("dialog.animation_maker.strip");
		JLabel lab3 = L10N.label("dialog.animation_maker.color");
		JLabel lab4 = L10N.label("dialog.animation_maker.saturation_lightness_lock");
		JLabel lab5 = L10N.label("dialog.animation_maker.colorize");

		JLabel preview = new JLabel(new ImageIcon(new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB)));
		preview.setBorder(BorderFactory
				.createTitledBorder(BorderFactory.createLineBorder(Color.gray, 1), "Preview", 0, 0,
						getFont().deriveFont(12.0f), Color.gray));

		JButton selectFile = new JButton("...");
		JColor colors = new JColor(mcreator);
		JCheckBox cbox = new JCheckBox();
		JCheckBox cbox2 = new JCheckBox();

		AtomicReference<TiledImageUtils> tilImgUtl = new AtomicReference<>();

		selectFile.addActionListener(event -> {
			f.set(FileDialogs.getOpenDialog(mcreator, new String[] { ".png" }));
			try {
				if (f.get() != null) {
					BufferedImage imge = TiledImageUtils.convert(ImageIO.read(f.get()), BufferedImage.TYPE_INT_ARGB);
					int x = Math.min(imge.getHeight(), imge.getWidth());
					selectFile.setText(StringUtils.abbreviateString(f.get().getName(), 25));
					tilImgUtl.set(new TiledImageUtils(imge, x, x));
					if (cbox2.isSelected())
						preview.setIcon(new ImageIcon(ImageUtils.resize(ImageUtils
								.colorize(tilImgUtl.get().getIcon(1, 1), colors.getColor(), !cbox.isSelected())
								.getImage(), 128)));
					else
						preview.setIcon(
								new ImageIcon(ImageUtils.resize(tilImgUtl.get().getIcon(1, 1).getImage(), 128)));
				}
			} catch (InvalidTileSizeException | IOException e) {
				LOG.error(e.getMessage(), e);
			}
		});

		ActionListener al = e -> {
			if (f.get() != null && tilImgUtl.get() != null)
				if (cbox2.isSelected())
					preview.setIcon(new ImageIcon(ImageUtils.resize(ImageUtils
									.colorize(tilImgUtl.get().getIcon(1, 1), colors.getColor(), !cbox.isSelected()).getImage(),
							128)));
				else
					preview.setIcon(new ImageIcon(ImageUtils.resize(tilImgUtl.get().getIcon(1, 1).getImage(), 128)));
		};

		colors.setColorSelectedListener(al);
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

		if (JOptionPane.showOptionDialog(mcreator, od, L10N.t("dialog.animation_maker.add_frames_from_file"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "Add", "Cancel" },
				"Add") == 0) {
			try {
				BufferedImage imge = TiledImageUtils.convert(ImageIO.read(f.get()), BufferedImage.TYPE_INT_ARGB);
				addFramesFromBufferedImage(imge, cbox2.isSelected(), !cbox.isSelected(), colors.getColor());
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
	}

	private void addFramesFromBufferedImage(BufferedImage bufferedImage, boolean colorize, boolean colorizerType,
			Color color) {
		BufferedImage b;
		if (colorize)
			b = ImageUtils.toBufferedImage(
					ImageUtils.colorize(new ImageIcon(bufferedImage), color, colorizerType).getImage());
		else
			b = bufferedImage;
		int x = Math.min(b.getHeight(), b.getWidth());
		TiledImageUtils tiledImageUtils = null;
		try {
			tiledImageUtils = new TiledImageUtils(b, x, x);
		} catch (InvalidTileSizeException e) {
			LOG.error(e.getMessage(), e);
		}
		if (tiledImageUtils != null)
			for (int i = 1; i <= tiledImageUtils.getWidthInTiles(); i++)
				for (int j = 1; j <= tiledImageUtils.getHeightInTiles(); j++) {
					BufferedImage buf;
					if (colorize)
						buf = ImageUtils.toBufferedImage(tiledImageUtils.getIcon(i, j).getImage());
					else
						buf = ImageUtils.toBufferedImage(tiledImageUtils.getIcon(i, j).getImage());
					timelinevector.addElement(new AnimationFrame(buf));
				}
	}

	private class ComboBoxRenderer extends JPanel implements ListCellRenderer<AnimationFrame> {

		ComboBoxRenderer() {
			setOpaque(true);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends AnimationFrame> list, AnimationFrame value,
				int index, boolean isSelected, boolean cellHasFocus) {
			removeAll();
			if (isSelected && index == animindex) {
				setBackground(new Color(255, 0, 255));
			} else if (isSelected) {
				setBackground(Color.red);
			} else if (index == animindex) {
				setBackground(Color.blue);
			} else {
				setBackground(Color.gray);
			}
			setPreferredSize(new Dimension(170, 170));
			add(new JLabel(new ImageIcon(ImageUtils.resize(value.image, 170))));

			return this;
		}

	}

	private static ImageIcon makeAnimationIcon(int stevilo, DefaultListModel<AnimationFrame> timelinevector, int size) {
		BufferedImage resizedImage = new BufferedImage(size, size * stevilo, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = resizedImage.createGraphics();
		for (int i = 0; i < timelinevector.getSize(); i++)
			g.drawImage(timelinevector.get(i).image, 0, i * size, size, size, new JLabel());
		g.dispose();
		return new ImageIcon(Toolkit.getDefaultToolkit().createImage(resizedImage.getSource()));
	}

	static class AnimationFrame {
		BufferedImage image;

		AnimationFrame(BufferedImage s) {
			image = s;
		}
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

	@Override public ViewBase showView() {
		MCreatorTabs.Tab tab = new MCreatorTabs.Tab(this);
		tab.setTabClosedListener(tab1 -> {
			this.active = false;
			this.animator = null;
		});
		mcreator.mcreatorTabs.addTab(tab);
		return this;
	}

	@Override public String getViewName() {
		return "Animation maker";
	}

}
