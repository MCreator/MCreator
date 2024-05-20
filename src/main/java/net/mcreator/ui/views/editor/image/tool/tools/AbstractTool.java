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

package net.mcreator.ui.views.editor.image.tool.tools;

import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.zoompane.ZoomedMouseEvent;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.views.editor.image.canvas.Canvas;
import net.mcreator.ui.views.editor.image.layer.Layer;
import net.mcreator.ui.views.editor.image.layer.LayerPanel;
import net.mcreator.ui.views.editor.image.tool.component.ColorSelector;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationEvent;
import net.mcreator.ui.views.editor.image.tool.tools.event.ToolActivationListener;
import net.mcreator.ui.views.editor.image.versioning.VersionManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public abstract class AbstractTool implements MouseListener, MouseMotionListener, ToolActivationListener {
	protected final String name;
	protected final String description;
	protected final ImageIcon icon;

	private final JPanel propertiesPanel = new JPanel(new BorderLayout());
	protected final JPanel settingsPanel = new JPanel();

	protected Canvas canvas;
	protected Layer layer;
	protected final ColorSelector colorSelector;
	protected LayerPanel layerPanel;
	protected final VersionManager versionManager;

	private JToggleButton toolPanelButton;
	protected boolean startSuccess = false, processSuccess = false;

	public AbstractTool(String name, String description, ImageIcon icon, Canvas canvas, ColorSelector colorSelector,
			VersionManager versionManager) {
		this.name = name;
		this.description = description;
		this.icon = icon;
		this.canvas = canvas;
		this.colorSelector = colorSelector;
		this.versionManager = versionManager;

		propertiesPanel.setOpaque(false);

		settingsPanel.setLayout(new GridLayout(9, 1, 3, 3));
		settingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		settingsPanel.setOpaque(false);

		JLabel lab = new JLabel(name);
		lab.setFont(new Font(lab.getFont().getName(), Font.PLAIN, 20));
		lab.setOpaque(false);
		lab.setBorder(BorderFactory.createEmptyBorder(3, 8, 10, 5));

		JScrollPane scrollPane = new JScrollPane(settingsPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		propertiesPanel.add(lab, BorderLayout.NORTH);
		propertiesPanel.add(scrollPane, BorderLayout.CENTER);
	}

	public void noSettings(boolean no) {
		((BorderLayout) propertiesPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
		if (no)
			propertiesPanel.add(PanelUtils.totalCenterInPanel(L10N.label("dialog.imageeditor_tool.use_canvas")),
					BorderLayout.CENTER);
		else
			propertiesPanel.add(PanelUtils.pullElementUp(settingsPanel), BorderLayout.CENTER);

	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public JPanel getPropertiesPanel() {
		return propertiesPanel;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	public Cursor getCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	public Cursor getUsingCursor() {
		return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
	}

	public Cursor getHoverCursor() {
		return null;
	}

	public void setLayerPanel(LayerPanel layerPanel) {
		this.layerPanel = layerPanel;
	}

	public abstract boolean process(ZoomedMouseEvent mouseEvent);

	private boolean startProcess(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e)) {
			return process((ZoomedMouseEvent) e);
		}
		return false;
	}

	@Override public void mouseClicked(MouseEvent e) {
	}

	@Override public void mousePressed(MouseEvent e) {
		startSuccess = startProcess(e);
		processSuccess = startSuccess;
	}

	@Override public void mouseReleased(MouseEvent e) {
	}

	@Override public void mouseEntered(MouseEvent e) {
	}

	@Override public void mouseExited(MouseEvent e) {
		if (SwingUtilities.isLeftMouseButton(e))
			canvas.enablePreview(false);
	}

	@Override public void mouseDragged(MouseEvent e) {
		processSuccess |= startProcess(e);
	}

	@Override public void mouseMoved(MouseEvent e) {
	}

	@Override public void toolActivationChanged(ToolActivationEvent event) {
	}

	@Override public void toolEnabled(ToolActivationEvent event) {
	}

	@Override public void toolDisabled(ToolActivationEvent event) {
	}

	public JToggleButton getToolPanelButton() {
		return toolPanelButton;
	}

	public void setToolPanelButton(JToggleButton toolPanelButton) {
		this.toolPanelButton = toolPanelButton;
	}
}
