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

package net.mcreator.ui.views;

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.editor.image.ImageMakerView;
import net.mcreator.ui.views.editor.image.animation.AnimationImportUtils;
import net.mcreator.ui.views.editor.image.animation.TimelineRenderer;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.versioning.change.FrameAddition;
import net.mcreator.ui.views.editor.image.versioning.change.FrameRemoval;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.util.image.InvalidTileSizeException;
import net.mcreator.util.image.TiledImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class AnimationMakerView extends JPanel {

	private static final Logger LOG = LogManager.getLogger("Animation Timeline");

	private final ImageMakerView imv;

	private final DefaultListModel<Canvas> timelinevector = new DefaultListModel<>();
	private final JList<Canvas> timeline = new JList<>(timelinevector);

	private final JToolBar controls = new JToolBar();
	private final JButton play = new JButton("");
	private final JButton remove = L10N.button("dialog.animation_maker.remove_selected_frames");
	private final JButton convertButton = L10N.button("dialog.animation_maker.convert");

	private int animindex = 0;
	private int[] framesToPlay;
	private boolean playAllFrames = true;
	private boolean playanim = true;
	private boolean playBackward = false;

	private final Thread animator;

	private boolean active;

	public AnimationMakerView(ImageMakerView imv) {
		this.imv = imv;
		setLayout(new BorderLayout());

		animator = new Thread(() -> {
			active = true;
			while (active) {
				if (timelinevector.getSize() > 0 && playanim) {
					if (playBackward)
						animindex--;
					else
						animindex++;

					// The following code handle when we arrive at the last frame (when forward) or the first frame (when backward)
					// Only frames selected by the user are animated
					if (!playAllFrames && framesToPlay != null && framesToPlay.length > 0) {
						if (!playBackward
								&& animindex >= framesToPlay.length) // If we are at the last frame and go forward
							animindex = framesToPlay[0]; // We go back to the first frame
						else if (playBackward && animindex < 0) // If we play backward and are at the first frame
							animindex = framesToPlay[framesToPlay.length - 1]; // We go back to the last frame
						// All frames are animated
					} else if (!playBackward && animindex
							> timelinevector.getSize() - 1) { // If we play forward, and we are at the last frame
						animindex = 0; // We go back to the first frame
					} else if (playBackward && animindex < 0) { // If we play backward and are at the first frame
						animindex = timelinevector.getSize() - 1; // We go back to the last frame
					}

					SwingUtilities.invokeLater(() -> {
						imv.setDisplayedCanvas(timelinevector.getElementAt(animindex));
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

		play.setIcon(UIRES.get("16px.play"));
		play.addActionListener(_ -> {
			if (timeline.getSelectedIndices().length > 1) {
				framesToPlay = timeline.getSelectedIndices();
				playAllFrames = false;
			} else {
				playAllFrames = true;
			}

			if (!animator.isAlive()) {
				animator.start();
				playanim = true;
				play.setIcon(UIRES.get("16px.pause"));
			} else if (!playanim) {
				playanim = true;
				play.setIcon(UIRES.get("16px.pause"));
			} else {
				playanim = false;
				play.setIcon(UIRES.get("16px.play"));
			}
		});
		controls.add(play);

		JButton stop = new JButton("");
		stop.addActionListener(_ -> {
			animindex = 0;
			playanim = false;
			play.setIcon(UIRES.get("16px.play"));
			timeline.setSelectedValue(timelinevector.getElementAt(0), false);
			imv.setDisplayedCanvas(timelinevector.getElementAt(0));
			timeline.repaint();
		});
		stop.setIcon(UIRES.get("16px.stopanimation"));
		controls.add(stop);

		controls.addSeparator();

		JButton prev = new JButton(UIRES.get("16px.previous"));
		prev.addActionListener(_ -> {
			if (!timelinevector.isEmpty()) {
				animindex--;
				if (animindex < 0)
					animindex = timelinevector.getSize() - 1;
				imv.setDisplayedCanvas(timelinevector.getElementAt(animindex));
				timeline.setSelectedIndex(animindex);
				timeline.repaint();
			}
		});
		controls.add(prev);

		JButton backward = new JButton(UIRES.get("16px.rwd"));
		backward.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (!animator.isAlive())
					animator.start();
				playanim = true;
				playBackward = true;
			}

			@Override public void mouseReleased(MouseEvent e) {
				playanim = false;
				playBackward = false;
			}
		});
		controls.add(backward);

		JButton forward = new JButton(UIRES.get("16px.fwd"));
		forward.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent e) {
				if (!animator.isAlive())
					animator.start();
				playanim = true;
			}

			@Override public void mouseReleased(MouseEvent e) {
				playanim = false;
			}
		});
		controls.add(forward);

		JButton next = new JButton(UIRES.get("16px.next"));
		next.addActionListener(_ -> {
			if (!timelinevector.isEmpty()) {
				animindex++;
				if (animindex >= timelinevector.getSize())
					animindex = 0;
				imv.setDisplayedCanvas(timelinevector.getElementAt(animindex));
				timeline.setSelectedIndex(animindex);
				timeline.repaint();
			}
		});
		controls.add(next);

		JPanel timelinePanel = new JPanel(new BorderLayout());
		timelinePanel.setOpaque(false);

		JToolBar timelinebar = new JToolBar();
		timelinebar.setFloatable(false);

		JButton importButton = new JButton(L10N.t("dialog.animation_maker.import"), UIRES.get("16px.import"));
		importButton.addActionListener(_ -> AnimationImportUtils.importImagesAsFrames(this));
		timelinebar.add(importButton);

		JButton add = L10N.button("dialog.animation_maker.add_new_frame");
		add.addActionListener(_ -> addFrameFromEmptyLayer());
		add.setIcon(UIRES.get("18px.add"));
		timelinebar.add(add);

		JButton addFromTemplates = L10N.button("dialog.animation_maker.create_frames_from_template");
		addFromTemplates.addActionListener(_ -> AnimationImportUtils.addFramesFromTemplate(this));
		addFromTemplates.setIcon(UIRES.get("18px.add"));
		timelinebar.add(addFromTemplates);

		convertButton.setIcon(UIRES.get("18px.add"));
		convertButton.addActionListener(_ -> {
			Canvas current = imv.getCanvas();
			int index = timelinevector.indexOf(current);
			generateTimelineFromBufferedImage(current.render(), imv.getViewName());
			timelinevector.remove(index);
			imv.setDisplayedCanvas(timelinevector.getElementAt(index));
			convertButton.setVisible(false);
		});
		timelinebar.add(convertButton);

		remove.addActionListener(_ -> {
			if (timeline.getSelectedValue() != null) {
				timeline.getSelectedValuesList().forEach(canvas -> {
					if (timelinevector.getSize() > 1) {
						removeFrameFromTimeline(canvas, false);
					} else if (timelinevector.getSize() == 1
							&& PreferencesManager.PREFERENCES.imageEditor.singleFrameDeletionBehaviour.get()
							.equals("Empty frame")) {
						removeFrameFromTimeline(canvas, true);
					}
				});
			}

			if (timelinevector.getSize() == 1) {
				imv.save.setText(L10N.t("dialog.image_maker.save"));
				imv.saveNew.setText(L10N.t("dialog.image_maker.save_as_new"));

				if (PreferencesManager.PREFERENCES.imageEditor.singleFrameDeletionBehaviour.get()
						.equals("Keep existing frame")) {
					updateTimelineButtons();
				}
			}
		});
		remove.setIcon(UIRES.get("18px.remove"));
		updateTimelineButtons();
		timelinebar.add(remove);

		timeline.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		timeline.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		timeline.setVisibleRowCount(1);
		timeline.setCellRenderer(new TimelineRenderer(this));

		JScrollPane pan = new JScrollPane(timeline);
		pan.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		pan.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		pan.setOpaque(false);
		pan.getViewport().setOpaque(false);

		timelinePanel.add("Center", pan);

		timelinePanel.setPreferredSize(new Dimension(9000, 260));

		add("North", PanelUtils.westAndCenterElement(controls, timelinebar));
		add("Center", timelinePanel);

		ComponentUtils.makeSection(this, L10N.t("dialog.animation_maker.animation_timeline"));
	}

	private void updateTimelineButtons() {
		if (timelinevector.getSize() == 1) {
			controls.setVisible(false);
			remove.setVisible(false);
		} else {
			controls.setVisible(true);
			remove.setVisible(true);
		}
		convertButton.setVisible(imv.getCanvas() != null && imv.getCanvas().getWidth() != imv.getCanvas().getHeight());
	}

	public void generateTimelineFromBufferedImage(BufferedImage bufferedImage, String name) {
		int x = Math.min(bufferedImage.getHeight(), bufferedImage.getWidth());
		generateTimelineFromBufferedImage(bufferedImage, x, x, name);
	}

	/**
	 * <p>This method takes a {@link BufferedImage} that contains multiple frames (a tiled image) and divide each image into their own frame.
	 * Then, each individual frame is added to the timeline.</p>
	 *
	 * @param bufferedImage The tiled {@link BufferedImage} containing all frames
	 * @param name          This is the name that will be used for each {@link Layer}
	 */
	public void generateTimelineFromBufferedImage(BufferedImage bufferedImage, int width, int height, String name) {
		try {
			TiledImageUtils tiledImageUtils = new TiledImageUtils(bufferedImage, width, height);
			for (int i = 1; i <= tiledImageUtils.getWidthInTiles(); i++) {
				for (int j = 1; j <= tiledImageUtils.getHeightInTiles(); j++) {
					BufferedImage buf = ImageUtils.toBufferedImage(tiledImageUtils.getIcon(i, j).getImage());
					if (timelinevector.getSize() == 0)
						insertFrameToTimelineNR(createCanvasFromBufferedImage(buf, name), 0);
					else
						addFrameToTimeline(createCanvasFromBufferedImage(buf, name));
				}
			}
		} catch (InvalidTileSizeException e) {
			LOG.warn("Invalid tile size", e);
		}
	}

	/**
	 * <p>This will add the provided {@link Canvas} at the end of the timeline.</p>
	 *
	 * @param canvas The {@link Canvas} to add at the timeline
	 */
	public void addFrameToTimeline(Canvas canvas) {
		if (timelinevector.getSize() == 0) { // This is the original frame, so we don't want the revision
			insertFrameToTimelineNR(canvas, 0);
			return;
		}

		timelinevector.addElement(canvas);
		imv.getVersionManager().addRevision(new FrameAddition(canvas, canvas.getFirst(), timelinevector.size() - 1));
		if (timelinevector.getSize() > 1) {
			imv.save.setText(L10N.t("dialog.image_maker.save_animated_texture"));
			imv.saveNew.setText(L10N.t("dialog.image_maker.save_animation_as_new"));
		}
		updateTimelineButtons();
	}

	public void insertFrameToTimelineNR(Canvas canvas, int index) {
		timelinevector.insertElementAt(canvas, index);
		if (timelinevector.getSize() > 1) {
			imv.save.setText(L10N.t("dialog.image_maker.save_animated_texture"));
			imv.saveNew.setText(L10N.t("dialog.image_maker.save_animation_as_new"));
		}
		updateTimelineButtons();
	}

	/**
	 * <p>This adds an empty {@link Canvas} at the end of the timeline</p>
	 */
	private void addFrameFromEmptyLayer() {
		Layer layer = new Layer(imv.getCanvas().getWidth(), imv.getCanvas().getHeight(), 0, 0, "Layer");
		Canvas canvas = new Canvas(imv, imv.getCanvas().getWidth(), imv.getCanvas().getHeight());
		canvas.add(layer);
		addFrameToTimeline(canvas);
	}

	public void removeFrameFromTimeline(Canvas canvas, boolean addEmptyFrame) {
		timelinevector.removeElement(canvas);
		updateTimelineButtons();
		imv.getVersionManager().addRevision(new FrameRemoval(canvas, canvas.getFirst(), timelinevector.size(), addEmptyFrame));
	}

	public void removeFrameFromTimelineNR(Canvas canvas) {
		timelinevector.removeElement(canvas);
		updateTimelineButtons();
	}

	public void removeFrameFromTimelineNR(int index) {
		timelinevector.remove(index);
		updateTimelineButtons();
	}

	/**
	 * <p>This takes a {@link BufferedImage} and transforms into a {@link Canvas} with a single {@link Layer}.</p>
	 *
	 * @param bufferedImage The image to convert into a {@link Canvas}
	 * @param name          The name to give to the {@link Layer}
	 * @return A {@link Canvas} containing the {@link BufferedImage} as one {@link Layer}
	 */
	public Canvas createCanvasFromBufferedImage(BufferedImage bufferedImage, String name) {
		Layer layer = Layer.toLayer(bufferedImage, name);
		Canvas canvas = new Canvas(imv, layer.getWidth(), layer.getHeight());
		canvas.add(layer);
		return canvas;
	}

	public ImageMakerView getImageMakerView() {
		return imv;
	}

	public DefaultListModel<Canvas> getTimelineModel() {
		return timelinevector;
	}

	public void setTimelineModel(DefaultListModel<Canvas> timelineModel) {
		timeline.setModel(timelineModel);
	}

	public JList<Canvas> getTimeline() {
		return timeline;
	}
}
