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
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class ComboBoxFullWidthPopup implements PopupMenuListener {

	private final boolean scrollBarRequired;
	private final boolean popupWider;
	private final int maximumWidth;
	private final boolean popupAbove;
	private JScrollPane scrollPane;

	public ComboBoxFullWidthPopup() {
		this(true, true, -1, false);
	}

	/**
	 * General purpose constructor to set all popup properties at once.
	 *
	 * @param scrollBarRequired display a horizontal scrollbar when the
	 *                          preferred width of popup is greater than width of scrollPane.
	 * @param popupWider        display the popup at its preferred with
	 * @param maximumWidth      limit the popup width to the value specified
	 *                          (minimum size will be the width of the combo box)
	 * @param popupAbove        display the popup above the combo box
	 */
	private ComboBoxFullWidthPopup(boolean scrollBarRequired, boolean popupWider, int maximumWidth,
			boolean popupAbove) {
		this.scrollBarRequired = (scrollBarRequired);
		this.popupWider = (popupWider);
		this.maximumWidth = (maximumWidth);
		this.popupAbove = (popupAbove);
	}

	/**
	 * Alter the bounds of the popup just before it is made visible.
	 */
	@Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
		JComboBox comboBox = (JComboBox) e.getSource();

		if (comboBox.getItemCount() == 0)
			return;

		final Object child = comboBox.getAccessibleContext().getAccessibleChild(0);

		if (child instanceof BasicComboPopup) {
			SwingUtilities.invokeLater(() -> customizePopup((BasicComboPopup) child));
		}
	}

	private void customizePopup(BasicComboPopup popup) {
		scrollPane = getScrollPane(popup);

		if (popupWider)
			popupWider(popup);

		checkHorizontalScrollBar(popup);

		//  For some reason in JDK7 the popup will not display at its preferred
		//  width unless its location has been changed from its default
		//  (ie. for normal "pop down" shift the popup and reset)

		Component comboBox = popup.getInvoker();
		Point location = comboBox.getLocationOnScreen();

		if (popupAbove) {
			int height = popup.getPreferredSize().height;
			popup.setLocation(location.x, location.y - height);
		} else {
			int height = comboBox.getPreferredSize().height;
			popup.setLocation(location.x, location.y + height - 1);
			popup.setLocation(location.x, location.y + height);
		}
	}

	private void popupWider(BasicComboPopup popup) {
		JList list = popup.getList();
		int popupWidth = list.getPreferredSize().width + 5  // make sure horizontal scrollbar doesn't appear
				+ getScrollBarWidth(popup, scrollPane);

		if (maximumWidth != -1) {
			popupWidth = Math.min(popupWidth, maximumWidth);
		}

		Dimension scrollPaneSize = scrollPane.getPreferredSize();
		popupWidth = Math.max(popupWidth, scrollPaneSize.width);

		//  Adjust the width

		scrollPaneSize.width = popupWidth;
		scrollPane.setPreferredSize(scrollPaneSize);
		scrollPane.setMaximumSize(scrollPaneSize);
	}

	private void checkHorizontalScrollBar(BasicComboPopup popup) {
		//  Reset the viewport to the left

		JViewport viewport = scrollPane.getViewport();
		Point p = viewport.getViewPosition();
		p.x = 0;
		viewport.setViewPosition(p);

		//  Remove the scrollbar so it is never painted

		if (!scrollBarRequired) {
			scrollPane.setHorizontalScrollBar(null);
			return;
		}

		//	Make sure a horizontal scrollbar exists in the scrollpane

		JScrollBar horizontal = scrollPane.getHorizontalScrollBar();

		if (horizontal == null) {
			horizontal = new JScrollBar(JScrollBar.HORIZONTAL);
			scrollPane.setHorizontalScrollBar(horizontal);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		}

		if (horizontalScrollBarWillBeVisible(popup, scrollPane)) {
			Dimension scrollPaneSize = scrollPane.getPreferredSize();
			scrollPaneSize.height += horizontal.getPreferredSize().height;
			scrollPane.setPreferredSize(scrollPaneSize);
			scrollPane.setMaximumSize(scrollPaneSize);
			scrollPane.revalidate();
		}
	}

	private JScrollPane getScrollPane(BasicComboPopup popup) {
		JList list = popup.getList();
		Container c = SwingUtilities.getAncestorOfClass(JScrollPane.class, list);

		return (JScrollPane) c;
	}

	private int getScrollBarWidth(BasicComboPopup popup, JScrollPane scrollPane) {
		int scrollBarWidth = 0;
		JComboBox comboBox = (JComboBox) popup.getInvoker();

		if (comboBox.getItemCount() > comboBox.getMaximumRowCount()) {
			JScrollBar vertical = scrollPane.getVerticalScrollBar();
			scrollBarWidth = vertical.getPreferredSize().width;
		}

		return scrollBarWidth;
	}

	private boolean horizontalScrollBarWillBeVisible(BasicComboPopup popup, JScrollPane scrollPane) {
		JList list = popup.getList();
		int scrollBarWidth = getScrollBarWidth(popup, scrollPane);
		int popupWidth = list.getPreferredSize().width + scrollBarWidth;

		return popupWidth > scrollPane.getPreferredSize().width;
	}

	@Override public void popupMenuCanceled(PopupMenuEvent e) {
	}

	@Override public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
		if (scrollPane != null) {
			scrollPane.setHorizontalScrollBar(null);
		}
	}
}
