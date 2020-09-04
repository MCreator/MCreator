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

package net.mcreator.ui.component.util;

import javax.swing.*;
import java.awt.*;

public class WrapLayout extends FlowLayout {
	/**
	 * Constructs a new <code>WrapLayout</code> with a left
	 * alignment and a default 5-unit horizontal and vertical gap.
	 */
	public WrapLayout() {
		super();
	}

	/**
	 * Constructs a new <code>FlowLayout</code> with the specified
	 * alignment and a default 5-unit horizontal and vertical gap.
	 * The value of the alignment argument must be one of
	 * <code>WrapLayout</code>, <code>WrapLayout</code>,
	 * or <code>WrapLayout</code>.
	 *
	 * @param align the alignment value
	 */
	public WrapLayout(int align) {
		super(align);
	}

	/**
	 * Creates a new flow layout manager with the indicated alignment
	 * and the indicated horizontal and vertical gaps.
	 * <p>
	 * The value of the alignment argument must be one of
	 * <code>WrapLayout</code>, <code>WrapLayout</code>,
	 * or <code>WrapLayout</code>.
	 *
	 * @param align the alignment value
	 * @param hgap  the horizontal gap between components
	 * @param vgap  the vertical gap between components
	 */
	public WrapLayout(int align, int hgap, int vgap) {
		super(align, hgap, vgap);
	}

	/**
	 * Returns the preferred dimensions for this layout given the
	 * <i>visible</i> components in the specified target container.
	 *
	 * @param target the component which needs to be laid out
	 * @return the preferred dimensions to lay out the
	 * subcomponents of the specified container
	 */
	@Override public Dimension preferredLayoutSize(Container target) {
		return layoutSize(target, true);
	}

	/**
	 * Returns the minimum dimensions needed to layout the <i>visible</i>
	 * components contained in the specified target container.
	 *
	 * @param target the component which needs to be laid out
	 * @return the minimum dimensions to lay out the
	 * subcomponents of the specified container
	 */
	@Override public Dimension minimumLayoutSize(Container target) {
		Dimension minimum = layoutSize(target, false);
		minimum.width -= (getHgap() + 1);
		return minimum;
	}

	/**
	 * Returns the minimum or preferred dimension needed to layout the target
	 * container.
	 *
	 * @param target    target to get layout size for
	 * @param preferred should preferred size be calculated
	 * @return the dimension to layout the target container
	 */
	private Dimension layoutSize(Container target, boolean preferred) {
		synchronized (target.getTreeLock()) {
			int targetWidth = target.getSize().width;
			Container container = target;

			while (container.getSize().width == 0 && container.getParent() != null) {
				container = container.getParent();
			}

			targetWidth = container.getSize().width;

			if (targetWidth == 0)
				targetWidth = Integer.MAX_VALUE;

			int hgap = getHgap();
			int vgap = getVgap();
			Insets insets = target.getInsets();
			int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
			int maxWidth = targetWidth - horizontalInsetsAndGap;

			Dimension dim = new Dimension(0, 0);
			int rowWidth = 0;
			int rowHeight = 0;

			int nmembers = target.getComponentCount();

			for (int i = 0; i < nmembers; i++) {
				Component m = target.getComponent(i);

				if (m.isVisible()) {
					Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();

					if (rowWidth + d.width > maxWidth) {
						addRow(dim, rowWidth, rowHeight);
						rowWidth = 0;
						rowHeight = 0;
					}

					if (rowWidth != 0) {
						rowWidth += hgap;
					}

					rowWidth += d.width;
					rowHeight = Math.max(rowHeight, d.height);
				}
			}

			addRow(dim, rowWidth, rowHeight);

			dim.width += horizontalInsetsAndGap;
			dim.height += insets.top + insets.bottom + vgap * 2;

			Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);

			if (scrollPane != null && target.isValid()) {
				dim.width -= (hgap + 1);
			}

			return dim;
		}
	}

	private void addRow(Dimension dim, int rowWidth, int rowHeight) {
		dim.width = Math.max(dim.width, rowWidth);

		if (dim.height > 0) {
			dim.height += getVgap();
		}

		dim.height += rowHeight;
	}
}
