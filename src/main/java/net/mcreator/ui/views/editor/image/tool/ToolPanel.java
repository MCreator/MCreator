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
import net.mcreator.ui.init.UIRES;
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
import javax.swing.border.EmptyBorder;
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

	private PencilTool pt;

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

		JPanel toolsAndColor = new JPanel(new BorderLayout());

		cs = new ColorSelector(frame);

		toolGroups.setBorder(new EmptyBorder(3, 3, 3, 3));
		BoxLayout boxLayout = new BoxLayout(toolGroups, BoxLayout.Y_AXIS);
		toolGroups.setLayout(boxLayout);

		toolsAndColor.setOpaque(false);
		toolProperties.setOpaque(false);
		toolGroups.setOpaque(false);

		setTopComponent(toolsAndColor);
		setBottomComponent(toolProperties);

		toolsAndColor.add(toolGroups, BorderLayout.CENTER);
		toolsAndColor.add(PanelUtils.centerInPanel(cs), BorderLayout.SOUTH);

		init();
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
		ToolGroup general = new ToolGroup("General");
		ToolGroup drawing = new ToolGroup("Drawing");
		ToolGroup filters = new ToolGroup("Filters");
		ToolGroup constraints = new ToolGroup("Move and resize");

		pt = new PencilTool(canvas, cs, layerPanel, versionManager);
		JToggleButton pencil = register(pt, drawing);

		addButton("Undo (Ctrl + Z)", "Undoes the last action", UIRES.get("img_editor.undo"), e -> versionManager.undo(),
				general);
		addButton("Redo (Ctrl + Y)", "Redoes the last action", UIRES.get("img_editor.redo"), e -> versionManager.redo(),
				general);
		register(new ResizeCanvasTool(canvas, cs, versionManager, frame), general);

		register(new ShapeTool(canvas, cs, layerPanel, versionManager), drawing);
		register(new EraserTool(canvas, cs, layerPanel, versionManager), drawing);
		register(new StampTool(canvas, cs, layerPanel, versionManager, frame), drawing);
		register(new FloodFillTool(canvas, cs, versionManager), drawing);
		register(new ColorPickerTool(canvas, cs, versionManager), drawing);

		register(new ColorizeTool(canvas, cs, versionManager, frame), filters);
		register(new DesaturateTool(canvas, cs, versionManager, frame), filters);
		register(new HSVNoiseTool(canvas, cs, versionManager, frame), filters);

		register(new MoveTool(canvas, cs, versionManager), constraints);
		register(new ResizeTool(canvas, cs, versionManager, frame), constraints);

		pencil.setSelected(true);

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
		toolButton.setOpaque(false);
		toolButton.setMargin(new Insets(5, 5, 5, 5));
		toolButton.setBorder(BorderFactory.createEmptyBorder());
		toolGroup.register(toolButton);
		toolButton.addActionListener(actionListener);
		return toolButton;
	}

	private JToggleButton register(AbstractTool tool, ToolGroup toolGroup) {
		JToggleButton toolButton = new JToggleButton();
		toolButton.setIcon(tool.getIcon());
		toolButton.setToolTipText(tool.getName());
		toolButton.setOpaque(false);
		toolButton.setMargin(new Insets(5, 5, 5, 5));
		toolButton.setBorder(BorderFactory.createEmptyBorder());
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
		setTool(pt);
	}
}
