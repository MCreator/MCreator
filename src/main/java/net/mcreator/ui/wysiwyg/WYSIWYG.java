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

package net.mcreator.ui.wysiwyg;

import net.mcreator.element.parts.gui.GUIComponent;
import net.mcreator.element.parts.gui.SizedComponent;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.component.zoompane.IZoomable;
import net.mcreator.ui.component.zoompane.JZoomPane;
import net.mcreator.ui.component.zoompane.JZoomport;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.AbstractMCreatorTheme;
import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WYSIWYG extends JComponent implements MouseMotionListener, MouseListener, IZoomable {

	public final static int W = 427;
	public final static int H = 240;

	private static final AffineTransform affinetransform = new AffineTransform();
	public static final FontRenderContext frc = new FontRenderContext(affinetransform, true, false);

	public static Font fontMC;

	private ActionListener positionDefinedListener = null;

	boolean isNotOverlayType = true;

	private boolean positioningMode = false;
	private boolean positioningModeSettingWidth = false;
	private boolean componentMoveMode;
	private boolean componentDragMode;

	boolean showGrid = false;

	@Nullable private GUIComponent selected;

	public int newlyAddedComponentPosX, newlyAddedComponentPosY;
	private int ox, oy;
	public int ow, oh;
	private int dragOffsetX, dragOffsetY;

	private final WYSIWYGEditor wysiwygEditor;

	private final Image background = UIRES.get("guieditor").getImage();
	private final Image inventorySlots = MinecraftImageGenerator.generateInventorySlots();

	private JZoomport owner;

	int grid_x_spacing = 18;
	int grid_y_spacing = 18;

	int grid_x_offset = 11;
	int grid_y_offset = 15;

	WYSIWYG(WYSIWYGEditor wysiwygEditor) {
		if (fontMC == null)
			fontMC = AbstractMCreatorTheme.console_font.deriveFont(8f);

		this.wysiwygEditor = wysiwygEditor;

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override public void setZoomPane(JZoomPane jZoomPane) {
		this.owner = jZoomPane.getZoomport();
	}

	@Override public Dimension getPreferredSize() {
		return new Dimension(W * 2, H * 2);
	}

	@Override public int getWidth() {
		return W * 2;
	}

	@Override public int getHeight() {
		return H * 2;
	}

	void setSelectedComponent(GUIComponent selected) {
		this.selected = selected;
		repaint();
	}

	void moveMode() {
		owner.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		componentMoveMode = true;

		if (selected instanceof SizedComponent) {
			this.ow = ((SizedComponent) selected).width;
			this.oh = ((SizedComponent) selected).height;
		}
	}

	void removeMode() {
		wysiwygEditor.components.remove(selected);
		selected = null;
		repaint();
	}

	public void addComponent(GUIComponent component) {
		wysiwygEditor.components.add(component);
		repaint();
	}

	public void setPositionDefinedListener(ActionListener positionDefinedListener) {
		this.positionDefinedListener = positionDefinedListener;
	}

	public void setPositioningMode(int ow, int oh) {
		positioningMode = true;
		this.ow = ow;
		this.oh = oh;
		owner.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		repaint();
	}

	@Override public void paint(Graphics gx) {
		Graphics2D g = (Graphics2D) gx;

		g.drawImage(background, 0, 0, null);

		g.setColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		g.drawRect(0, 0, getWidth(), getHeight());

		// draw wysiwyg
		BufferedImage bi = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D wg = bi.createGraphics();
		drawWYSIWYG(wg);
		wg.dispose();
		g.drawImage(bi, 0, 0, this);

		// draw selection box
		if (selected != null) {
			int cx = selected.getX(), cy = selected.getY();
			if (componentMoveMode) {
				cx = ox;
				cy = oy;
			}

			int cw = selected.getWidth(wysiwygEditor.mcreator.getWorkspace());
			int ch = selected.getHeight(wysiwygEditor.mcreator.getWorkspace());

			g.setColor(new Color(255, 255, 255, 100));
			g.fillRect(cx * 2, cy * 2, cw * 2, ch * 2);

			Stroke original = g.getStroke();
			Stroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0);

			g.setStroke(dashed);

			g.setColor(Color.white);
			g.drawRect(cx * 2, cy * 2, cw * 2, ch * 2);

			g.setStroke(original);
		}

		if (positioningMode || componentMoveMode) {
			float[] dash = { 2.0f };
			g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g.setColor(new Color(255, 255, 255, 150));

			g.drawLine(0, oy * 2, getWidth(), oy * 2);
			g.drawLine(ox * 2, 0, ox * 2, getHeight());

			if (positioningMode || positioningModeSettingWidth) {
				g.drawLine(0, oy * 2 + oh * 2, getWidth(), oy * 2 + oh * 2);
				g.drawLine(ox * 2 + ow * 2, 0, ox * 2 + ow * 2, getHeight());

				g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
				g.setColor(Color.white);
				g.drawRect(ox * 2, oy * 2, ow * 2, oh * 2);
			} else if (selected != null) {
				g.drawLine(0, oy * 2 + selected.getHeight(wysiwygEditor.mcreator.getWorkspace()) * 2, getWidth(),
						oy * 2 + selected.getHeight(wysiwygEditor.mcreator.getWorkspace()) * 2);
				g.drawLine(ox * 2 + selected.getWidth(wysiwygEditor.mcreator.getWorkspace()) * 2, 0,
						ox * 2 + selected.getWidth(wysiwygEditor.mcreator.getWorkspace()) * 2, getHeight());
			}
		}

		if (showGrid)
			drawGrid(g);

		if (this.owner != null)
			this.owner.repaint();
	}

	private void drawWYSIWYG(Graphics2D g) {
		g.scale(2, 2);

		g.setFont(fontMC);
		g.setColor(Color.gray.brighter().brighter());

		int gw = (Integer) wysiwygEditor.spa1.getValue();
		int gh = (Integer) wysiwygEditor.spa2.getValue();

		if (isNotOverlayType) {
			if (wysiwygEditor.renderBgLayer.isSelected()) {
				if (wysiwygEditor.getGUITypeSelector().getSelectedIndex() == 0) {
					g.drawImage(MinecraftImageGenerator.generateBackground(gw, gh), W / 2 - gw / 2, H / 2 - gh / 2, gw,
							gh, this);
				} else if (wysiwygEditor.getGUITypeSelector().getSelectedIndex() == 1) {
					g.drawImage(MinecraftImageGenerator.generateBackground(gw, gh), W / 2 - gw / 2, H / 2 - gh / 2, gw,
							gh, this);
					g.drawImage(inventorySlots, W / 2 - 176 / 2 + (int) wysiwygEditor.invOffX.getValue(),
							H / 2 - 166 / 2 + (int) wysiwygEditor.invOffY.getValue(), 176, 166, this);
				}
			} else {
				g.setColor(Color.white);
				g.drawRect(W / 2 - gw / 2, H / 2 - gh / 2, gw, gh);
			}
		} else {
			if (wysiwygEditor.overlayBaseTexture.getSelectedItem() != null && !wysiwygEditor.overlayBaseTexture
					.getSelectedItem().equals("")) {
				g.drawImage(new ImageIcon(wysiwygEditor.mcreator.getFolderManager().getOtherTextureFile(
						FilenameUtils.removeExtension(wysiwygEditor.overlayBaseTexture.getSelectedItem()))
						.getAbsolutePath()).getImage(), 0, 0, W, H, this);
			}
		}

		wysiwygEditor.components.stream().sorted().forEach(component -> {
			g.setColor(Color.gray.brighter().brighter());
			Font tmp = g.getFont();

			int cx = component.getX(), cy = component.getY();
			if (component.equals(selected) && componentMoveMode) {
				cx = ox;
				cy = oy;
			}

			// paint actual component
			component.paintComponent(cx, cy, wysiwygEditor, g);

			g.setFont(tmp);
		});
	}

	private void drawGrid(Graphics2D g) {
		float[] dash = { 2.0f };
		g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

		// vertical lines
		for (int i = 0; i < getWidth(); i = i + grid_x_spacing) {
			if (i % (grid_x_spacing * 2) == 0)
				g.setColor(new Color(255, 255, 255, 80));
			else
				g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(i + grid_x_offset, 0, i + grid_x_offset, getHeight());
		}

		// horizontal lines
		for (int i = 0; i < getHeight(); i = i + grid_y_spacing) {
			if (i % (grid_y_spacing * 2) == 0)
				g.setColor(new Color(255, 255, 255, 80));
			else
				g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(0, i + grid_y_offset, getWidth(), i + grid_y_offset);
		}
	}

	@Override public void mouseMoved(MouseEvent e) {
		int ex = e.getX();
		int ey = e.getY();

		if (showGrid) {
			ex -= grid_x_offset;
			ey -= grid_y_offset;
			ex = Math.round(ex / (float) grid_x_spacing) * grid_x_spacing;
			ey = Math.round(ey / (float) grid_y_spacing) * grid_y_spacing;
			ex += grid_x_offset + 1;
			ey += grid_y_offset + 1;
		}

		// scale to coordinate system of the Minecraft
		ex = (int) Math.round(ex / 2.0);
		ey = (int) Math.round(ey / 2.0);

		if (positioningModeSettingWidth) {
			ow = Math.abs(ox - ex);
		} else if (positioningMode || componentMoveMode) {
			ox = ex;
			oy = ey;
		}

		repaint();
	}

	@Override public void mouseClicked(MouseEvent e) {
		int ex = e.getX();
		int ey = e.getY();

		// scale to coordinate system of the Minecraft
		ex = (int) Math.round(ex / 2.0);
		ey = (int) Math.round(ey / 2.0);

		if (positioningMode && e.getButton() == 1) {
			positioningMode = false;
			positioningModeSettingWidth = false;

			owner.setCursor(Cursor.getDefaultCursor());

			newlyAddedComponentPosX = ox;
			newlyAddedComponentPosY = oy;

			if (positionDefinedListener != null)
				positionDefinedListener.actionPerformed(new ActionEvent("a", ActionEvent.ACTION_PERFORMED, "a"));
		} else if (positioningMode) {
			newlyAddedComponentPosX = ox;
			newlyAddedComponentPosY = oy;

			positioningModeSettingWidth = true;
		} else if (componentMoveMode) {
			if (e.getButton() == 1 || !(selected instanceof net.mcreator.element.parts.gui.SizedComponent)) {
				finishGUIComponentMove();
			} else {
				newlyAddedComponentPosX = ox;
				newlyAddedComponentPosY = oy;

				positioningModeSettingWidth = true;
			}
		} else { // "click-on-component" mode
			GUIComponent component = getGUIComponentAt(ex, ey);
			if (component != null) {
				if (e.getClickCount() > 1) {
					wysiwygEditor.editCurrentlySelectedComponent();
				} else {
					wysiwygEditor.list.setSelectedValue(component, true);
				}
			} else {
				wysiwygEditor.list.clearSelection();
				this.selected = null;
				repaint();
			}
		}
	}

	@Override public void mouseDragged(MouseEvent e) {
		int ex = e.getX();
		int ey = e.getY();

		if (showGrid && componentDragMode) {
			ex -= grid_x_offset;
			ey -= grid_y_offset;
			ex = Math.round(ex / (float) grid_x_spacing) * grid_x_spacing;
			ey = Math.round(ey / (float) grid_y_spacing) * grid_y_spacing;
			ex += grid_x_offset + 1;
			ey += grid_y_offset + 1;
		}

		// scale to coordinate system of the Minecraft
		ex = (int) Math.round(ex / 2.0);
		ey = (int) Math.round(ey / 2.0);

		if (componentDragMode) {
			ox = ex + (showGrid ? 0 : dragOffsetX);
			oy = ey + (showGrid ? 0 : dragOffsetY);
		} else {
			GUIComponent component = getGUIComponentAt(ex, ey);
			if (component != null) {
				wysiwygEditor.list.setSelectedValue(component, true);

				dragOffsetX = component.getX() - ex;
				dragOffsetY = component.getY() - ey;

				ox = ex + (showGrid ? 0 : dragOffsetX);
				oy = ey + (showGrid ? 0 : dragOffsetY);

				componentDragMode = true;
				moveMode();
			} else {
				wysiwygEditor.list.clearSelection();
				this.selected = null;
				repaint();
			}
		}
	}

	@Override public void mouseEntered(MouseEvent e) {

	}

	@Override public void mouseExited(MouseEvent e) {

	}

	@Override public void mousePressed(MouseEvent e) {

	}

	@Override public void mouseReleased(MouseEvent e) {
		if (componentDragMode) {
			componentDragMode = false;
			finishGUIComponentMove();
		}
	}

	@Override public void paintPreZoom(Graphics g, Dimension d) {

	}

	@Override public void paintPostZoom(Graphics g, Dimension d) {

	}

	@Nullable private GUIComponent getGUIComponentAt(int ex, int ey) {
		List<GUIComponent> guiComponentList = new ArrayList<>(wysiwygEditor.components);
		guiComponentList.sort(Collections.reverseOrder());

		for (GUIComponent component : guiComponentList) {
			if (ex >= component.getX() && ex <= component.getX() + component
					.getWidth(wysiwygEditor.mcreator.getWorkspace())) {
				if (ey >= component.getY() && ey <= component.getY() + component
						.getHeight(wysiwygEditor.mcreator.getWorkspace())) {
					return component;
				}
			}
		}
		return null;
	}

	private void finishGUIComponentMove() {
		for (int i = 0; i < wysiwygEditor.components.size(); i++) {
			GUIComponent component = wysiwygEditor.components.get(i);
			if (component.equals(selected)) {
				component.x = ox;
				component.y = oy;
				if (positioningModeSettingWidth && component instanceof net.mcreator.element.parts.gui.SizedComponent) {
					((net.mcreator.element.parts.gui.SizedComponent) component).width = ow;
					((net.mcreator.element.parts.gui.SizedComponent) component).height = oh;
				}
				break;
			}
		}
		componentMoveMode = false;
		positioningModeSettingWidth = false;
		repaint();
		setCursor(Cursor.getDefaultCursor());
	}

}
