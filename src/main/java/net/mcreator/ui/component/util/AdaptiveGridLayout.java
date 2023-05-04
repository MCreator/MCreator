/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.component.util;

import java.awt.*;
import java.util.function.Function;

public class AdaptiveGridLayout implements LayoutManager {

	private final int hgap;
	private final int vgap;
	private final int rows;
	private final int cols;

	public AdaptiveGridLayout(int rows, int cols) {
		this(rows, cols, 0, 0);
	}

	public AdaptiveGridLayout(int rows, int cols, int hgap, int vgap) {
		if ((rows == 0) && (cols == 0)) {
			throw new IllegalArgumentException("rows and cols can not be zero at the same time");
		}

		this.rows = rows;
		this.cols = cols;
		this.hgap = hgap;
		this.vgap = vgap;
	}

	@Override public Dimension preferredLayoutSize(Container parent) {
		return estimateLayoutSize(parent, Component::getPreferredSize);
	}

	@Override public Dimension minimumLayoutSize(Container parent) {
		return estimateLayoutSize(parent, Component::getMinimumSize);
	}

	private Dimension estimateLayoutSize(Container parent, Function<Component, Dimension> componentSizeProvider) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = getVisibleComponents(parent);
			int nrows = rows;
			int ncols = cols;

			if (nrows > 0) {
				ncols = (ncomponents + nrows - 1) / nrows;
			} else {
				nrows = (ncomponents + ncols - 1) / ncols;
			}
			int w = 0;
			int h = 0;
			for (int i = 0; i < parent.getComponentCount(); i++) {
				Component comp = parent.getComponent(i);

				if (!comp.isVisible())
					continue;

				Dimension d = componentSizeProvider.apply(comp);
				if (w < d.width) {
					w = d.width;
				}
				if (h < d.height) {
					h = d.height;
				}
			}

			return new Dimension(insets.left + insets.right + ncols * w + (ncols - 1) * hgap,
					insets.top + insets.bottom + nrows * h + (nrows - 1) * vgap);
		}
	}

	@Override public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int ncomponents = getVisibleComponents(parent);
			int nrows = rows;
			int ncols = cols;
			boolean ltr = parent.getComponentOrientation().isLeftToRight();

			if (ncomponents == 0) {
				return;
			}
			if (nrows > 0) {
				ncols = (ncomponents + nrows - 1) / nrows;
			} else {
				nrows = (ncomponents + ncols - 1) / ncols;
			}

			int w = parent.getSize().width - (insets.left + insets.right);
			int h = parent.getSize().height - (insets.top + insets.bottom);
			w = (w - (ncols - 1) * hgap) / ncols;
			h = (h - (nrows - 1) * vgap) / nrows;

			int i = 0;

			if (ltr) {
				for (int r = 0, y = insets.top; r < nrows; r++, y += h + vgap) {
					int c = 0;
					int x = insets.left;

					while (c < ncols) {
						if (i >= parent.getComponentCount())
							break;

						Component component = parent.getComponent(i);

						if (component.isVisible()) {
							parent.getComponent(i).setBounds(x, y, w, h);
							c++;
							x += w + hgap;
						}

						i++;
					}
				}
			}

		}
	}

	private int getVisibleComponents(Container parent) {
		int visible = 0;

		for (Component c : parent.getComponents()) {
			if (c.isVisible())
				visible++;
		}

		return visible;
	}

	@Override public void addLayoutComponent(String name, Component comp) {
	}

	@Override public void removeLayoutComponent(Component comp) {
	}

}