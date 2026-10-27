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
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WYSIWYG extends JComponent implements MouseMotionListener, MouseListener, IZoomable {

	public final static int W = 427;
	public final static int H = 240;

	public static final FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, false);

	public static Font fontMC;

	boolean isNotOverlayType = true;

	private boolean positioningModeSettingWidth = false;
	private boolean positioningModeSettingHeight = false;

	private boolean componentMoveMode;
	private boolean componentDragMode;

	public boolean showGrid = false;

	@Nullable private GUIComponent selected;

	private int ox, oy;
	private int ow, oh;
	private int dragOffsetX, dragOffsetY;

	private final WYSIWYGEditor wysiwygEditor;

	private final Image background = UIRES.get("guieditor").getImage();
	private final Image inventorySlots = MinecraftImageGenerator.generateInventorySlots();

	private JZoomport owner;

	int grid_x_spacing = 9;
	int grid_y_spacing = 9;

	int grid_x_offset = 0;
	int grid_y_offset = 0;

	WYSIWYG(WYSIWYGEditor wysiwygEditor) {
		if (fontMC == null)
			fontMC = Theme.current().getConsoleFont().deriveFont(8f).deriveFont(Font.BOLD);

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

	public void moveMode() {
		if (selected != null && selected.locked)
			return;

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

	@Override public void paint(Graphics gx) {
		Graphics2D g = (Graphics2D) gx;

		g.drawImage(background, 0, 0, null);

		g.setColor(Theme.current().getInterfaceAccentColor());
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
			g.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
			g.setColor(Color.white);

			if (selected.isSizeKnown()) {
				g.drawRect(cx * 2, cy * 2, cw * 2, ch * 2);
			} else {
				g.drawLine(cx * 2, cy * 2 + ch * 2, cx * 2 + 20, cy * 2 + ch * 2);
				g.drawLine(cx * 2, cy * 2, cx * 2, cy * 2 + ch * 2);
			}

			g.setStroke(original);
		}

		if (componentMoveMode) {
			float[] dash = { 2.0f };
			g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
			g.setColor(new Color(255, 255, 255, 150));

			g.drawLine(0, oy * 2, getWidth(), oy * 2);
			g.drawLine(ox * 2, 0, ox * 2, getHeight());

			if (positioningModeSettingWidth || positioningModeSettingHeight) {
				if ((ow != 0 || oh != 0) && (selected == null || selected.isSizeKnown())) {
					g.drawLine(0, oy * 2 + oh * 2, getWidth(), oy * 2 + oh * 2);
					g.drawLine(ox * 2 + ow * 2, 0, ox * 2 + ow * 2, getHeight());

					g.setStroke(
							new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 3 }, 0));
					g.setColor(Color.white);
					g.drawRect(ox * 2, oy * 2, ow * 2, oh * 2);
				}
			} else if (selected != null && selected.isSizeKnown()) {
				g.drawLine(0, oy * 2 + selected.getHeight(wysiwygEditor.mcreator.getWorkspace()) * 2, getWidth(),
						oy * 2 + selected.getHeight(wysiwygEditor.mcreator.getWorkspace()) * 2);
				g.drawLine(ox * 2 + selected.getWidth(wysiwygEditor.mcreator.getWorkspace()) * 2, 0,
						ox * 2 + selected.getWidth(wysiwygEditor.mcreator.getWorkspace()) * 2, getHeight());
			}

			if (!isNotOverlayType && selected != null) {
				GUIComponent.AnchorPoint anchorPoint = selected.getAnchorPoint();
				if (anchorPoint != null) {
					Point anchor = anchorPoint.getAnchorPoint(W, H);

					g.setColor(new Color(255, 153, 250, 230));
					g.fillRect(anchor.x * 2 - 4, anchor.y * 2 - 4, 8, 8);
					g.drawLine(anchor.x * 2, anchor.y * 2, ox * 2, anchor.y * 2);
					g.drawLine(anchor.x * 2, anchor.y * 2, anchor.x * 2, oy * 2);
				}
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
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

		g.setColor(Color.gray.brighter().brighter());

		int gw = (Integer) wysiwygEditor.spa1.getValue();
		int gh = (Integer) wysiwygEditor.spa2.getValue();

		if (isNotOverlayType) {
			if (wysiwygEditor.renderBgLayer.isSelected()) {
				if (wysiwygEditor.getGUITypeSelector().getSelectedIndex() == 0) {
					g.drawImage(MinecraftImageGenerator.generateBackground(gw, gh), (int) Math.ceil(W / 2.0 - gw / 2.0),
							(int) Math.ceil(H / 2.0 - gh / 2.0), gw, gh, this);
				} else if (wysiwygEditor.getGUITypeSelector().getSelectedIndex() == 1) {
					g.drawImage(MinecraftImageGenerator.generateBackground(gw, gh), (int) Math.ceil(W / 2.0 - gw / 2.0),
							(int) Math.ceil(H / 2.0 - gh / 2.0), gw, gh, this);
					g.drawImage(inventorySlots,
							(int) Math.ceil(W / 2.0 - 176 / 2.0) + (int) wysiwygEditor.invOffX.getValue(),
							(int) Math.ceil(H / 2.0 - 166 / 2.0) + (int) wysiwygEditor.invOffY.getValue(), 176, 166,
							this);
				}
			} else {
				g.setColor(Color.white);
				g.drawRect((int) Math.ceil(W / 2.0 - gw / 2.0), (int) Math.ceil(H / 2.0 - gh / 2.0), gw, gh);
			}
		} else {
			if (wysiwygEditor.overlayBaseTexture.hasTexture()) {
				g.drawImage(wysiwygEditor.overlayBaseTexture.getTexture()
						.getTextureIcon(wysiwygEditor.mcreator.getWorkspace()).getImage(), 0, 0, W, H, this);
			}
		}

		List<GUIComponent> toDraw = new ArrayList<>(wysiwygEditor.components);
		if (selected != null && !toDraw.contains(selected))
			toDraw.add(selected);

		toDraw.stream().sorted().forEach(component -> {
			g.setColor(Color.gray.brighter().brighter());
			Font originalFont = g.getFont();
			Stroke originalStroke = g.getStroke();

			int cx = component.getX(), cy = component.getY();
			if (component.equals(selected) && componentMoveMode) {
				cx = ox;
				cy = oy;
			}

			// paint actual component
			component.paintComponent(cx, cy, wysiwygEditor, g);

			g.setFont(originalFont);
			g.setStroke(originalStroke);
		});
	}

	private Point getGridOrigin() {
		if (!isNotOverlayType)
			return GUIComponent.AnchorPoint.CENTER.getAnchorPoint(W, H);

		if (wysiwygEditor.getGUITypeSelector().getSelectedIndex() == 1) {
			// 7 and 83 are the position of the first slot in MinecraftImageGenerator.generateInventorySlots
			return new Point((int) Math.ceil(W / 2.0 - 176 / 2.0) + (int) wysiwygEditor.invOffX.getValue() + 7,
					(int) Math.ceil(H / 2.0 - 166 / 2.0) + (int) wysiwygEditor.invOffY.getValue() + 83);
		}

		int gw = (Integer) wysiwygEditor.spa1.getValue();
		int gh = (Integer) wysiwygEditor.spa2.getValue();
		return new Point((int) Math.ceil(W / 2.0 - gw / 2.0), (int) Math.ceil(H / 2.0 - gh / 2.0));
	}

	private void drawGrid(Graphics2D g) {
		float[] dash = { 2.0f };
		g.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));

		Point origin = getGridOrigin();
		int originX = origin.x + grid_x_offset;
		int originY = origin.y + grid_y_offset;

		// vertical lines, drawn on the GUI pixel components snap to
		for (int i = Math.floorMod(originX, grid_x_spacing); i < W; i = i + grid_x_spacing) {
			if (Math.floorMod((i - originX) / grid_x_spacing, 2) == 0)
				g.setColor(new Color(255, 255, 255, 80));
			else
				g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(i * 2, 0, i * 2, getHeight());
		}

		// horizontal lines, drawn on the GUI pixel components snap to
		for (int i = Math.floorMod(originY, grid_y_spacing); i < H; i = i + grid_y_spacing) {
			if (Math.floorMod((i - originY) / grid_y_spacing, 2) == 0)
				g.setColor(new Color(255, 255, 255, 80));
			else
				g.setColor(new Color(255, 255, 255, 30));
			g.drawLine(0, i * 2, getWidth(), i * 2);
		}
	}

	private Point toGUIPosition(MouseEvent e) {
		return new Point((int) Math.round(e.getX() / 2.0), (int) Math.round(e.getY() / 2.0));
	}

	private Point snapToGrid(Point position) {
		Point origin = getGridOrigin();
		return new Point(snapToGrid(position.x, origin.x + grid_x_offset, grid_x_spacing),
				snapToGrid(position.y, origin.y + grid_y_offset, grid_y_spacing));
	}

	private static int snapToGrid(int value, int origin, int spacing) {
		return origin + (int) Math.round((value - origin) / (double) spacing) * spacing;
	}

	@Override public void mouseMoved(MouseEvent e) {
		Point mouse = toGUIPosition(e);
		if (showGrid)
			mouse = snapToGrid(mouse);
		int ex = mouse.x;
		int ey = mouse.y;

		if (positioningModeSettingWidth) {
			ow = Math.abs(ox - ex);
			if (positioningModeSettingHeight)
				oh = Math.abs(oy - ey);
		} else if (componentMoveMode) {
			ox = ex;
			oy = ey;
		}

		repaint();
	}

	@Override public void mouseClicked(MouseEvent e) {
		Point mouse = toGUIPosition(e);
		int ex = mouse.x;
		int ey = mouse.y;

		if (componentMoveMode) {
			if (e.getButton() == 1 || !(selected instanceof SizedComponent component) || positioningModeSettingWidth) {
				finishGUIComponentMove();
			} else {
				positioningModeSettingWidth = true;
				if (component.canChangeHeight())
					positioningModeSettingHeight = true;
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
		Point mouse = toGUIPosition(e);
		int ex = mouse.x;
		int ey = mouse.y;

		if (componentDragMode) {
			dragSelectedComponent(ex, ey);
		} else if (componentMoveMode) {
			if (selected instanceof SizedComponent component && !positioningModeSettingWidth) {
				positioningModeSettingWidth = true;
				if (component.canChangeHeight())
					positioningModeSettingHeight = true;
			}
			mouseMoved(e); // if in move mode, we consider dragging as moving the mouse
		} else {
			GUIComponent component = getGUIComponentAt(ex, ey);
			if (component != null) {
				wysiwygEditor.list.setSelectedValue(component, true);

				dragOffsetX = component.getX() - ex;
				dragOffsetY = component.getY() - ey;

				componentDragMode = true;
				moveMode();
				dragSelectedComponent(ex, ey);
			} else {
				wysiwygEditor.list.clearSelection();
				this.selected = null;
				repaint();
			}
		}
	}

	private void dragSelectedComponent(int ex, int ey) {
		Point position = new Point(ex + dragOffsetX, ey + dragOffsetY);
		if (showGrid)
			position = snapToGrid(position);
		ox = position.x;
		oy = position.y;
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
			if (ex >= component.getX() && ex <= component.getX() + component.getWidth(
					wysiwygEditor.mcreator.getWorkspace())) {
				if (ey >= component.getY() && ey <= component.getY() + component.getHeight(
						wysiwygEditor.mcreator.getWorkspace())) {
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
				if (!selected.locked) {
					component.x = ox;
					component.y = oy;
					if (positioningModeSettingWidth
							&& component instanceof net.mcreator.element.parts.gui.SizedComponent) {
						((SizedComponent) component).width = ow;
						((SizedComponent) component).height = oh;
					}
				}
				break;
			}
		}
		componentMoveMode = false;
		positioningModeSettingWidth = false;
		positioningModeSettingHeight = false;

		owner.setCursor(Cursor.getDefaultCursor());

		repaint();
	}

}
