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

package net.mcreator.ui.component;

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseWheelEvent;

public class JScrollablePopupMenu extends JPopupMenu {

	private int maximumVisibleRows = 18;

	private boolean showArrows = false;

	public JScrollablePopupMenu() {
		setLayout(new ScrollPopupMenuLayout());
		super.add(getScrollBar());
		addMouseWheelListener(event -> {
			JScrollBar scrollBar = getScrollBar();
			int amount = (event.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) ?
					event.getUnitsToScroll() * scrollBar.getUnitIncrement() :
					(event.getWheelRotation() < 0 ? -1 : 1) * scrollBar.getBlockIncrement();

			scrollBar.setValue(scrollBar.getValue() + amount);
			event.consume();
		});
	}

	private JScrollBar popupScrollBar;

	protected JScrollBar getScrollBar() {
		if (popupScrollBar == null) {
			popupScrollBar = new JScrollBar(JScrollBar.VERTICAL);
			popupScrollBar.setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
					(Color) UIManager.get("MCreatorLAF.MAIN_TINT"), popupScrollBar));
			popupScrollBar.setPreferredSize(new Dimension(5, 0));
			popupScrollBar.addAdjustmentListener(e -> {
				doLayout();
				repaint();
			});
			popupScrollBar.setVisible(false);
		}
		return popupScrollBar;
	}

	public int getMaximumVisibleRows() {
		return maximumVisibleRows;
	}

	public void setShowArrows(boolean showArrows) {
		this.showArrows = showArrows;
	}

	@Override public void paint(Graphics g) {
		super.paint(g);

		if (showArrows && popupScrollBar.isVisible()) {
			if (popupScrollBar.getValue() < popupScrollBar.getMaximum() - popupScrollBar.getModel().getExtent())
				g.drawImage(UIRES.get("more").getImage(), getWidth() / 2 - 7, getHeight() - 18, this);

			if (popupScrollBar.getValue() > popupScrollBar.getMinimum())
				g.drawImage(UIRES.get("less").getImage(), getWidth() / 2 - 7, 4, this);
		}
	}

	public void setMaximumVisibleRows(int maximumVisibleRows) {
		this.maximumVisibleRows = maximumVisibleRows;
	}

	@Override public void scrollRectToVisible(Rectangle t) {
		// need to adjust scrollbar position
		if (t.getY() < 0) {
			// scroll up
			JScrollBar scrollBar = getScrollBar();
			scrollBar.setValue(scrollBar.getValue() + (int) (t.getY()));
		} else if (t.getY() + t.getHeight() > getBounds().getHeight()) {
			// scroll down
			JScrollBar scrollBar = getScrollBar();
			scrollBar.setValue(scrollBar.getValue() - (int) (getBounds().getHeight() - t.getY() - t.getHeight()));
		}
		doLayout();
		repaint();
	}

	public void paintChildren(Graphics g) {
		Insets insets = getInsets();
		g.clipRect(insets.left, insets.top, getWidth(), getHeight() - insets.top - insets.bottom);
		super.paintChildren(g);
	}

	protected void addImpl(Component comp, Object constraints, int index) {
		super.addImpl(comp, constraints, index);

		if (maximumVisibleRows < getComponentCount() - 1) {
			getScrollBar().setVisible(true);
		}
	}

	public void remove(int index) {
		// can't remove the scrollbar
		++index;

		super.remove(index);

		if (maximumVisibleRows >= getComponentCount() - 1) {
			getScrollBar().setVisible(false);
		}
	}

	public void show(Component invoker, int x, int y) {
		JScrollBar scrollBar = getScrollBar();
		if (scrollBar.isVisible()) {
			int extent = 0;
			int max = 0;
			int i = 0;
			int unit = -1;
			int width = 0;
			for (Component comp : getComponents()) {
				if (!(comp instanceof JScrollBar)) {
					Dimension preferredSize = comp.getPreferredSize();
					width = Math.max(width, preferredSize.width);
					if (unit < 0) {
						unit = preferredSize.height;
					}
					if (i++ < maximumVisibleRows) {
						extent += preferredSize.height;
					}
					max += preferredSize.height;
				}
			}

			Insets insets = getInsets();
			int widthMargin = insets.left + insets.right;
			int heightMargin = insets.top + insets.bottom;
			scrollBar.setUnitIncrement(unit);
			scrollBar.setBlockIncrement(extent);
			scrollBar.setValues(0, heightMargin + extent, 0, heightMargin + max);

			width += scrollBar.getPreferredSize().width + widthMargin;
			int height = heightMargin + extent;

			setPopupSize(new Dimension(width, height));
		}

		super.show(invoker, x, y);
	}

	protected static class ScrollPopupMenuLayout implements LayoutManager {
		@Override public void addLayoutComponent(String name, Component comp) {
		}

		@Override public void removeLayoutComponent(Component comp) {
		}

		@Override public Dimension preferredLayoutSize(Container parent) {
			int visibleAmount = Integer.MAX_VALUE;
			Dimension dim = new Dimension();
			for (Component comp : parent.getComponents()) {
				if (comp.isVisible()) {
					if (comp instanceof JScrollBar) {
						JScrollBar scrollBar = (JScrollBar) comp;
						visibleAmount = scrollBar.getVisibleAmount();
					} else {
						Dimension pref = comp.getPreferredSize();
						dim.width = Math.max(dim.width, pref.width);
						dim.height += pref.height;
					}
				}
			}

			Insets insets = parent.getInsets();
			dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);

			return dim;
		}

		@Override public Dimension minimumLayoutSize(Container parent) {
			int visibleAmount = Integer.MAX_VALUE;
			Dimension dim = new Dimension();
			for (Component comp : parent.getComponents()) {
				if (comp.isVisible()) {
					if (comp instanceof JScrollBar) {
						JScrollBar scrollBar = (JScrollBar) comp;
						visibleAmount = scrollBar.getVisibleAmount();
					} else {
						Dimension min = comp.getMinimumSize();
						dim.width = Math.max(dim.width, min.width);
						dim.height += min.height;
					}
				}
			}

			Insets insets = parent.getInsets();
			dim.height = Math.min(dim.height + insets.top + insets.bottom, visibleAmount);

			return dim;
		}

		@Override public void layoutContainer(Container parent) {
			Insets insets = parent.getInsets();

			int width = parent.getWidth() - insets.left - insets.right;
			int height = parent.getHeight() - insets.top - insets.bottom;

			int x = insets.left;
			int y = insets.top;
			int position = 0;

			for (Component comp : parent.getComponents()) {
				if ((comp instanceof JScrollBar) && comp.isVisible()) {
					JScrollBar scrollBar = (JScrollBar) comp;
					Dimension dim = scrollBar.getPreferredSize();
					scrollBar.setBounds(x + width - dim.width, y, dim.width, height);
					width -= dim.width;
					position = scrollBar.getValue();
				}
			}

			y -= position;
			for (Component comp : parent.getComponents()) {
				if (!(comp instanceof JScrollBar) && comp.isVisible()) {
					Dimension pref = comp.getPreferredSize();
					comp.setBounds(x, y, width, pref.height);
					y += pref.height;
				}
			}
		}
	}
}