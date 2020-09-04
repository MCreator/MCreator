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

package net.mcreator.ui.component.zoompane;

import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;

import javax.swing.*;
import java.awt.*;

public class JZoomPane extends JPanel {
	private final static int SCROLLBAR_THICKNESS = 10;

	private final JScrollBar horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
	private final JScrollBar verticalScrollBar = new JScrollBar();

	private final JButton quickActions = new JButton();
	private final JPopupMenu popup = new JPopupMenu();

	private JZoomport zoomport;

	public JZoomPane(JComponent zoomable) {
		super(new BorderLayout());
		this.zoomport = new JZoomport(zoomable, this);

		if (zoomable instanceof IZoomable)
			((IZoomable) zoomable).setZoomPane(this);

		popup.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		popup.setBorder(BorderFactory.createMatteBorder(0, 3, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		JMenuItem recenter = new JMenuItem("Center view");
		popup.add(recenter);
		JMenuItem fit = new JMenuItem("Zoom to fit");
		popup.add(fit);
		popup.addSeparator();

		quickActions.addActionListener(e -> popup.show(quickActions, 0, 0));
		recenter.addActionListener(e -> zoomport.recenter());
		fit.addActionListener(e -> zoomport.fitZoom());
		for (double zoom : zoomport.getZoomPresets()) {
			JMenuItem zoomButton = new JMenuItem(zoom + "x");
			zoomButton.addActionListener(e -> zoomport.setZoomAroundCenter(zoom));
			popup.add(zoomButton);
		}

		horizontalScrollBar.addAdjustmentListener(e -> {
			if (zoomport.isUpdateScrollbarX()) {
				zoomport.setViewPosX(e.getValue());
				zoomport.repaint();
			}
			zoomport.setUpdateScrollbarX(true);
		});

		verticalScrollBar.addAdjustmentListener(e -> {
			if (zoomport.isUpdateScrollbarY()) {
				zoomport.setViewPosY(e.getValue());
				zoomport.repaint();
			}
			zoomport.setUpdateScrollbarY(true);
		});

		horizontalScrollBar.setPreferredSize(new Dimension(0, SCROLLBAR_THICKNESS));
		verticalScrollBar.setPreferredSize(new Dimension(SCROLLBAR_THICKNESS, 0));

		quickActions.setPreferredSize(new Dimension(40, (int) horizontalScrollBar.getPreferredSize().getHeight()));

		setOpaque(false);
		horizontalScrollBar.setOpaque(false);
		verticalScrollBar.setOpaque(false);

		quickActions.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		quickActions.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		quickActions.setMargin(new Insets(0, 0, 0, 0));
		ComponentUtils.deriveFont(quickActions, 8);

		add(PanelUtils.centerAndEastElement(horizontalScrollBar, quickActions), BorderLayout.SOUTH);

		add(verticalScrollBar, BorderLayout.EAST);
		add(zoomport, BorderLayout.CENTER);

		updateZoomDisplay(1);
	}

	protected void updateZoomDisplay(double zoom) {
		quickActions.setText(((int) (zoom * 100)) + " %");
	}

	public void setViewportView(JComponent zoomable) {
		remove(((BorderLayout) getLayout()).getLayoutComponent(BorderLayout.CENTER));
		this.zoomport = new JZoomport(zoomable, this);
		add(zoomable, BorderLayout.CENTER);
	}

	public JScrollBar getHorizontalScrollBar() {
		return horizontalScrollBar;
	}

	public JScrollBar getVerticalScrollBar() {
		return verticalScrollBar;
	}

	public JZoomport getZoomport() {
		return zoomport;
	}
}
