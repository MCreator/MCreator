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

package net.mcreator.ui.component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class JAdaptiveSplitPane extends JPanel {

	private final Component leftComponent;
	private final Component rightComponent;

	private final JSplitPane splitPane;

	private final double dividerLocation;

	public JAdaptiveSplitPane(int orientation, Component leftComponent, Component rightComponent, double dividerLocation) {
		setLayout(new BorderLayout(0, 0));
		setOpaque(false);
		setBorder(null);

		splitPane = new JSplitPane(orientation);

		this.leftComponent = leftComponent;
		this.rightComponent = rightComponent;
		this.dividerLocation = dividerLocation;

		updateVisibility();

		leftComponent.addComponentListener(new ComponentAdapter() {
			@Override public void componentHidden(ComponentEvent e) {
				updateVisibility();
			}

			@Override public void componentShown(ComponentEvent e) {
				updateVisibility();
			}
		});

		rightComponent.addComponentListener(new ComponentAdapter() {
			@Override public void componentHidden(ComponentEvent e) {
				updateVisibility();
			}

			@Override public void componentShown(ComponentEvent e) {
				updateVisibility();
			}
		});
	}

	private void updateVisibility() {
		boolean leftVisible = leftComponent.isVisible();
		boolean rightVisible = rightComponent.isVisible();

		if (leftVisible && !rightVisible) {
			removeAll();
			add(leftComponent, BorderLayout.CENTER);
		} else if (!leftVisible && rightVisible) {
			removeAll();
			add(rightComponent, BorderLayout.CENTER);
		} else {
			removeAll();
			splitPane.setLeftComponent(leftComponent);
			splitPane.setRightComponent(rightComponent);
			add(splitPane, BorderLayout.CENTER);
			SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(dividerLocation));
		}

		revalidate();
		repaint();
	}

}