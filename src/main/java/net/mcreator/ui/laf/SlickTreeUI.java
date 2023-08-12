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

package net.mcreator.ui.laf;

import net.mcreator.ui.init.UIRES;

import javax.swing.*;
import javax.swing.plaf.metal.MetalTreeUI;
import javax.swing.tree.AbstractLayoutCache;
import javax.swing.tree.TreePath;
import java.awt.*;

public class SlickTreeUI extends MetalTreeUI {

	private final JScrollPane parentScrollPane;

	public SlickTreeUI(JScrollPane parent) {
		this.parentScrollPane = parent;
	}

	@Override public Icon getCollapsedIcon() {
		return UIRES.get("16px.collapsed");
	}

	@Override public Icon getExpandedIcon() {
		return UIRES.get("16px.expanded");
	}

	@Override protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
	}

	@Override protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
	}

	@Override protected AbstractLayoutCache.NodeDimensions createNodeDimensions() {
		return new NodeDimensionsHandler() {
			@Override
			public Rectangle getNodeDimensions(Object value, int row, int depth, boolean expanded, Rectangle size) {
				Rectangle dimensions = super.getNodeDimensions(value, row, depth, expanded, size);
				dimensions.width = parentScrollPane.getWidth() - getRowX(row, depth);
				return dimensions;
			}
		};
	}

}
