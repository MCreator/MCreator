/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

import net.mcreator.ui.component.zoompane.JZoomPane;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class ImagePreviewPanel extends JPanel {

	private final JZoomPane zoomPane;

	private final JLabel imageRenderer = new JLabel();

	private ImageIcon image = null;

	public ImagePreviewPanel() {
		this(null);
	}

	public ImagePreviewPanel(@Nullable ImageIcon image) {
		super(new BorderLayout());
		zoomPane = new JZoomPane(imageRenderer);

		add("Center", zoomPane);

		if (image != null) {
			if (zoomPane.getZoomport().isShowing()) {
				setImage(image);
			} else {
				zoomPane.getZoomport().addComponentListener(new ComponentAdapter() {
					@Override public void componentResized(ComponentEvent e) {
						setImage(image);
						zoomPane.getZoomport().removeComponentListener(this);
					}
				});
			}
		}
	}

	public void setImage(ImageIcon image) {
		if (this.image == image)
			return;

		this.image = image;
		imageRenderer.setIcon(image);
		imageRenderer.setSize(image.getIconWidth(), image.getIconHeight());
		zoomPane.getZoomport().fitZoom();
	}

}
