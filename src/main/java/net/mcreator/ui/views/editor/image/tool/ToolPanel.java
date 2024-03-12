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

package net.mcreator.ui.views.editor.image.tool;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.canvas.CanvasRenderer;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.component.ToolGroup;
import net.mcreator.ui.views.editor.image.tool.tools.*;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ToolPanel extends JSplitPane {
	private final MCreator frame;
	private final Canvas canvas;
	private final JZoomPane zoomPane;
	private final CanvasRenderer canvasRenderer;

	private final JPanel toolProperties = new JPanel(new CardLayout());
	private final JPanel toolGroups = new JPanel();

	private PencilTool pencilTool;

	private final ColorSelector cs;
	private AbstractTool currentTool;

	private final ButtonGroup buttonGroup = new ButtonGroup();

	private final ArrayList<AbstractTool> toolList = new ArrayList<>();
	private LayerPanel layerPanel;
	private final VersionManager versionManager;

	public ToolPanel(MCreator frame, Canvas canvas, JZoomPane zoomPane, CanvasRenderer canvasRenderer,
			VersionManager versionManager) {
		super(JSplitPane.VERTICAL_SPLIT);
		this.frame = frame;
		this.canvas = canvas;
		this.zoomPane = zoomPane;
		this.canvasRenderer = canvasRenderer;
		this.versionManager = versionManager;

		cs = new ColorSelector(frame);
		JComponent cswrap = PanelUtils.centerInPanel(cs);
		cswrap.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

		toolGroups.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		toolGroups.setLayout(new BoxLayout(toolGroups, BoxLayout.Y_AXIS));

		JPanel toolsAndColor = new JPanel(new BorderLayout());
		toolsAndColor.add(toolGroups, BorderLayout.CENTER);
		toolsAndColor.add(cswrap, BorderLayout.SOUTH);
		toolsAndColor.setMinimumSize(new Dimension(230, 360));

		setBackground(Theme.current().getSecondAltBackgroundColor());

		init();

		setTopComponent(toolsAndColor);
		setBottomComponent(toolProperties);
		setDividerLocation(360);
	}

	public AbstractTool getCurrentTool() {
		return currentTool;
	}

	public ColorSelector getColorSelector() {
		return cs;
	}

	public void setLayerPanel(LayerPanel layerPanel) {
		this.layerPanel = layerPanel;
	}

	private void init() {
		ToolGroup general = new ToolGroup(L10N.t("dialog.image_maker.tools.general"));
		ToolGroup drawing = new ToolGroup(L10N.t("dialog.image_maker.tools.drawing"));
		ToolGroup filters = new ToolGroup(L10N.t("dialog.image_maker.tools.filters"));
		ToolGroup constraints = new ToolGroup(L10N.t("dialog.image_maker.tools.constraints"));

		pencilTool = new PencilTool(canvas, cs, layerPanel, versionManager);
		register(pencilTool, drawing).setSelected(true);

		JButton undo = addButton(L10N.t("dialog.image_maker.tools.undo"),
				L10N.t("dialog.image_maker.tools.undo_description"), UIRES.get("img_editor.undo"),
				e -> versionManager.undo(), general);
		undo.setEnabled(false);
		JButton redo = addButton(L10N.t("dialog.image_maker.tools.redo"),
				L10N.t("dialog.image_maker.tools.redo_description"), UIRES.get("img_editor.redo"),
				e -> versionManager.redo(), general);
		redo.setEnabled(false);

		register(new LineTool(canvas, cs, layerPanel, versionManager), drawing);
		register(new ShapeTool(canvas, cs, layerPanel, versionManager), drawing);
		register(new EraserTool(canvas, cs, layerPanel, versionManager), drawing);
		register(new StampTool(canvas, cs, layerPanel, versionManager, frame), drawing);
		register(new FloodFillTool(canvas, cs, versionManager), drawing);
		register(new ColorPickerTool(canvas, cs, versionManager), drawing);

		register(new ColorizeTool(canvas, cs, versionManager, frame), filters);
		register(new DesaturateTool(canvas, cs, versionManager, frame), filters);
		register(new HSVNoiseTool(canvas, cs, versionManager, frame), filters);

		register(new MoveTool(canvas, cs, versionManager), constraints);
		register(new SelectionTool(canvas, cs, versionManager), constraints);
		register(new ResizeTool(canvas, cs, versionManager, frame), constraints);
		register(new ResizeCanvasTool(canvas, cs, versionManager, frame), constraints);

		versionManager.setRevisionListener(() -> {
			undo.setEnabled(!versionManager.firstRevision());
			frame.actionRegistry.imageEditorUndo.setEnabled(!versionManager.firstRevision());
			redo.setEnabled(!versionManager.lastRevision());
			frame.actionRegistry.imageEditorRedo.setEnabled(!versionManager.lastRevision());
		});

		toolGroups.add(general);
		toolGroups.add(drawing);
		toolGroups.add(filters);
		toolGroups.add(constraints);
	}

	private JButton addButton(String name, String description, ImageIcon icon, ActionListener actionListener,
			ToolGroup toolGroup) {
		JButton toolButton = new JButton();
		toolButton.setIcon(icon);
		toolButton.setToolTipText(name);
		toolGroup.register(toolButton);
		toolButton.addActionListener(actionListener);
		return toolButton;
	}

	private JToggleButton register(AbstractTool tool, ToolGroup toolGroup) {
		JToggleButton toolButton = new JToggleButton();
		toolButton.setIcon(tool.getIcon());
		toolButton.setToolTipText(tool.getName());
		buttonGroup.add(toolButton);
		toolGroup.register(toolButton);
		toolProperties.add(tool.getPropertiesPanel(), tool.getName());
		toolButton.addActionListener(e -> {
			if (currentTool != null) {
				currentTool.toolDisabled(new ToolActivationEvent(false));
				currentTool.toolActivationChanged(new ToolActivationEvent(false));
			}

			((CardLayout) toolProperties.getLayout()).show(toolProperties, tool.getName());
			currentTool = tool;
			zoomPane.setCursor(tool.getCursor());
			canvasRenderer.setCursor(tool.getCursor());

			tool.toolEnabled(new ToolActivationEvent(true));
			tool.toolActivationChanged(new ToolActivationEvent(true));
		});
		tool.setToolPanelButton(toolButton);
		zoomPane.setCursor(tool.getCursor());
		canvasRenderer.setCursor(tool.getCursor());
		toolList.add(tool);
		return toolButton;
	}

	public void setLayer(Layer layer) {
		for (AbstractTool tool : toolList)
			tool.setLayer(layer);
	}

	public void setCanvas(Canvas canvas) {
		for (AbstractTool tool : toolList)
			tool.setCanvas(canvas);
	}

	public void setToolByClass(Class<? extends AbstractTool> tool) {
		AbstractTool foundTool = getToolByClass(tool);
		if (foundTool != null)
			setTool(foundTool);
	}

	public AbstractTool getToolByClass(Class<? extends AbstractTool> tool) {
		for (AbstractTool toolFromToolList : toolList) {
			if (toolFromToolList.getClass().isAssignableFrom(tool)) {
				return toolFromToolList;
			}
		}
		return null;
	}

	public void setTool(AbstractTool tool) {
		if (tool != null) {
			tool.getToolPanelButton().doClick();
		}
	}

	public void initTools() {
		setTool(pencilTool);
	}
}
