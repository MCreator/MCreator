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

package net.mcreator.ui.views.editor.image;

import net.mcreator.io.FileIO;
import net.mcreator.minecraft.RegistryNameFixer;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorTabs;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.dialogs.imageeditor.FromTemplateDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.CanvasRenderer;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.ToolPanel;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;
import net.mcreator.util.image.ImageUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageMakerView extends ViewBase implements MouseListener, MouseMotionListener {

	private static final Logger LOG = LogManager.getLogger("Image Maker View");

	private String name = "Texture creator";

	private Canvas canvas;
	private final CanvasRenderer canvasRenderer;
	private final JZoomPane zoomPane;
	private final JSplitPane leftSplitPane;
	private final JSplitPane rightSplitPane;
	private final ToolPanel toolPanel;
	private final LayerPanel layerPanel;

	private final VersionManager versionManager;

	private final JLabel imageInfo = new JLabel("");

	public final JButton save;

	public static final ExecutorService toolExecutor = Executors.newSingleThreadExecutor();
	private MCreatorTabs.Tab tab;
	private File image;

	public ImageMakerView(MCreator f) {
		super(f);

		versionManager = new VersionManager(this);

		JPanel controls = new JPanel(new BorderLayout());
		controls.setBorder(new EmptyBorder(2, 3, 2, 3));
		JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		save = L10N.button("dialog.image_maker.save");
		save.setMargin(new Insets(1, 40, 1, 40));
		save.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		save.setForeground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		save.setFocusPainted(false);

		imageInfo.setForeground(((Color) UIManager.get("MCreatorLAF.GRAY_COLOR")).darker());
		imageInfo.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

		JButton saveNew = L10N.button("dialog.image_maker.save_as_new");
		saveNew.setMargin(new Insets(1, 40, 1, 40));
		saveNew.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		saveNew.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		saveNew.setFocusPainted(false);

		JButton template = L10N.button("dialog.image_maker.generate_from_template");
		template.setMargin(new Insets(1, 40, 1, 40));
		template.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		template.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		template.setFocusPainted(false);

		save.addActionListener(event -> save());
		saveNew.addActionListener(event -> saveAs());
		template.addActionListener(event -> {
			FromTemplateDialog fromTemplateDialog = new FromTemplateDialog(f, canvas, versionManager);
			fromTemplateDialog.setVisible(true);
		});

		leftSplitPane = new JSplitPane();
		rightSplitPane = new JSplitPane() {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};

		canvasRenderer = new CanvasRenderer(this);
		zoomPane = new JZoomPane(canvasRenderer);
		toolPanel = new ToolPanel(f, canvas, zoomPane, canvasRenderer, versionManager);
		layerPanel = new LayerPanel(f, toolPanel, versionManager);

		toolPanel.setLayerPanel(layerPanel);
		versionManager.setLayerPanel(layerPanel);

		leftSplitPane.setOpaque(false);
		rightSplitPane.setOpaque(false);

		leftSplitPane.setLeftComponent(toolPanel);
		leftSplitPane.setRightComponent(rightSplitPane);
		leftSplitPane.setOneTouchExpandable(true);

		rightSplitPane.setLeftComponent(PanelUtils.northAndCenterElement(imageInfo, zoomPane));
		rightSplitPane.setRightComponent(layerPanel);
		rightSplitPane.setResizeWeight(1);
		rightSplitPane.setOneTouchExpandable(true);

		leftControls.add(template);
		rightControls.add(saveNew);

		rightControls.add(save);

		controls.add(leftControls, BorderLayout.WEST);
		controls.add(rightControls, BorderLayout.EAST);

		controls.setBorder(
				BorderFactory.createMatteBorder(0, 0, 1, 0, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")));

		add(controls, BorderLayout.NORTH);
		add(leftSplitPane, BorderLayout.CENTER);
	}

	public void openInEditMode(File image) {
		try {
			this.image = image;
			Layer layer = Layer.toLayer(ImageIO.read(image), image.getName());
			canvas = new Canvas(layer.getWidth(), layer.getHeight(), layerPanel, versionManager);
			canvasRenderer.setCanvas(canvas);
			toolPanel.setCanvas(canvas);
			canvas.add(layer);
			name = image.getName();
			toolPanel.initTools();
			updateInfobar(0, 0);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	public void setSaveLocation(File location) {
		this.image = location;
		name = image.getName();
		refreshTab();
	}

	public void save() {
		try {
			if (image != null) {
				ImageIO.write(canvasRenderer.render(), FilenameUtils.getExtension(image.toString()), image);
				this.name = image.getName();

				//reload image in java cache
				new ImageIcon(image.getAbsolutePath()).getImage().flush();
				mcreator.mv.reloadElements();

				refreshTab();
			} else {
				saveAs();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void saveAs() {
		Image image = canvasRenderer.render();
		Object[] options = { "Block", "Item", "Other" };
		int n = JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.image_maker.texture_kind"), L10N.t("dialog.image_maker.texture_type"),
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
		String namec = VOptionPane
				.showInputDialog(mcreator, L10N.t("dialog.image_maker.enter_name"), L10N.t("dialog.image_maker.image_name"), null,
						new OptionPaneValidatior() {

							@Override public ValidationResult validate(JComponent component) {
								return new RegistryNameValidator((VTextField) component, L10N.t("dialog.image_maker.texture_name")).validate();
							}
						});
		if (namec != null) {
			File exportFile;
			if (n == 0)
				exportFile = mcreator.getFolderManager().getBlockTextureFile(RegistryNameFixer.fix(namec));
			else if (n == 1)
				exportFile = mcreator.getFolderManager().getItemTextureFile(RegistryNameFixer.fix(namec));
			else if (n == 2)
				exportFile = mcreator.getFolderManager().getOtherTextureFile(RegistryNameFixer.fix(namec));
			else
				return;

			if (exportFile.isFile())
				JOptionPane.showMessageDialog(mcreator, L10N.t("dialog.image_maker.texture_type_name_exists"),
						L10N.t("dialog.image_maker.resource_error"), JOptionPane.ERROR_MESSAGE);
			else
				FileIO.writeImageToPNGFile(ImageUtils.toBufferedImage(image), exportFile);
			this.image = exportFile;
			this.name = this.image.getName();
			refreshTab();
		}
	}

	public void newImage(int width, int height, String name) {
		canvas = new Canvas(width, height, layerPanel, versionManager);
		canvasRenderer.setCanvas(canvas);
		toolPanel.setCanvas(canvas);
		this.name = name + ".png";
		toolPanel.initTools();
		updateInfobar(0, 0);
	}

	public void newImage(Layer layer) {
		canvas = new Canvas(layer.getWidth(), layer.getHeight(), layerPanel, versionManager);
		canvasRenderer.setCanvas(canvas);
		toolPanel.setCanvas(canvas);
		canvas.add(layer);
		this.name = "Image maker";
		toolPanel.initTools();
		updateInfobar(0, 0);
	}

	@Override public ViewBase showView() {
		if (image != null)
			this.tab = new MCreatorTabs.Tab(this, image);
		else
			this.tab = new MCreatorTabs.Tab(this);

		MCreatorTabs.Tab existing = mcreator.mcreatorTabs.showTabOrGetExisting(this.tab);
		if (existing == null) {
			mcreator.mcreatorTabs.addTab(this.tab);
			leftSplitPane.setDividerLocation(1.2 / 8);
			rightSplitPane.setDividerLocation(5.7 / 7);
			toolPanel.setDividerLocation(1.0 / 3);
			zoomPane.getZoomport().fitZoom();
			refreshTab();
			return this;
		}
		refreshTab();
		return (ViewBase) existing.getContent();
	}

	@Override public String getViewName() {
		return name;
	}

	@Override public ImageIcon getViewIcon() {
		if (canvasRenderer != null)
			return ImageUtils.fit(canvasRenderer.render(), 24);
		return null;
	}

	public void refreshTab() {
		if (tab != null) {
			tab.setIcon(getViewIcon());
			tab.setText(this.name);
			tab.updateSize();
		}
	}

	@Override public void mouseClicked(MouseEvent e) {
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseClicked(e));
	}

	@Override public void mousePressed(MouseEvent e) {
		zoomPane.setCursor(toolPanel.getCurrentTool().getUsingCursor());
		canvasRenderer.setCursor(toolPanel.getCurrentTool().getUsingCursor());
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mousePressed(e));
	}

	@Override public void mouseReleased(MouseEvent e) {
		zoomPane.setCursor(toolPanel.getCurrentTool().getCursor());
		canvasRenderer.setCursor(toolPanel.getCurrentTool().getCursor());
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseReleased(e));
	}

	@Override public void mouseEntered(MouseEvent e) {
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseEntered(e));
	}

	@Override public void mouseExited(MouseEvent e) {
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseExited(e));
	}

	@Override public void mouseDragged(MouseEvent e) {
		zoomPane.setCursor(toolPanel.getCurrentTool().getUsingCursor());
		canvasRenderer.setCursor(toolPanel.getCurrentTool().getUsingCursor());
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseDragged(e));
		updateInfobar(e.getX(), e.getY());
	}

	@Override public void mouseMoved(MouseEvent e) {
		toolExecutor.execute(() -> toolPanel.getCurrentTool().mouseMoved(e));
		updateInfobar(e.getX(), e.getY());
	}

	private void updateInfobar(int x, int y) {
		imageInfo.setText(
				"[" + (image == null ? "New image" : "File: " + image.getName()) + ", size: " + canvas.getWidth() + "x"
						+ canvas.getHeight() + "] Mouse location: " + x + ", " + y);
	}

	public VersionManager getVersionManager() {
		return versionManager;
	}

	public ToolPanel getToolPanel() {
		return toolPanel;
	}
}
