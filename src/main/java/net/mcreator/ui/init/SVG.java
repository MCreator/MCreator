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

package net.mcreator.ui.init;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.SVGLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.net.URL;

public class SVG {

	public static Icon getBuiltIn(String identifier, int width, int height) {
		URL url = ClassLoader.getSystemClassLoader().getResource("net/mcreator/ui/res/" + identifier + ".svg");
		if (url == null)
			throw new RuntimeException("SVG resource not found: " + identifier);
		SVGLoader loader = new SVGLoader();
		final SVGDocument doc = loader.load(url);
		if (doc == null)
			throw new RuntimeException("SVG resource not found: " + identifier);
		return new Icon() {
			@Override public void paintIcon(Component c, Graphics g, int x, int y) {
				g.drawImage(new BaseMultiResolutionImage(instance(1.0), instance(1.5), instance(1.75), instance(2.0)),
						x, y, width, height, null);
			}

			private BufferedImage instance(double scale) {
				BufferedImage image = new BufferedImage((int) Math.round(width * scale),
						(int) Math.round(height * scale), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = image.createGraphics();
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				doc.render(null, g, new ViewBox(0, 0, image.getWidth(), image.getHeight()));
				g.dispose();
				return image;
			}

			@Override public int getIconWidth() {
				return width;
			}

			@Override public int getIconHeight() {
				return height;
			}
		};
	}

}
