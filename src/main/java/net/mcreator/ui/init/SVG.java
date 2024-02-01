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
import java.awt.geom.AffineTransform;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SVG {

	private static final ExecutorService SVG_LOADER_THREAD = Executors.newSingleThreadExecutor();
	private static final SVGLoader SVG_LOADER = new SVGLoader();

	private static final Map<String, ImageIcon> CACHE = new ConcurrentHashMap<>();

	private static final Double[] SCALES;

	static {
		Set<Double> scales = new TreeSet<>();
		scales.add(1.0);

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice device : env.getScreenDevices()) {
			GraphicsConfiguration config = device.getDefaultConfiguration();
			AffineTransform transform = config.getDefaultTransform();
			scales.add(transform.getScaleX());
		}

		SCALES = scales.toArray(new Double[0]);
	}

	public static ImageIcon getBuiltIn(String identifier, int width, int height) {
		return CACHE.computeIfAbsent(computeKey(identifier, width, height, true), id -> {
			URL url = ClassLoader.getSystemClassLoader().getResource("net/mcreator/ui/res/" + identifier + ".svg");
			return new ImageIcon(new BaseMultiResolutionImage(getResolutionVariants(loadSVG(url), width, height)));
		});
	}

	private static synchronized SVGDocument loadSVG(URL url) {
		try {
			SVGDocument doc = SVG_LOADER_THREAD.submit(() -> SVG_LOADER.load(url)).get();
			if (doc == null)
				throw new RuntimeException("SVG resource not found: " + url);
			return doc;
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	private static Image[] getResolutionVariants(SVGDocument doc, int width, int height) {
		Image[] images = new Image[SCALES.length];
		for (int i = 0; i < SCALES.length; i++)
			images[i] = renderSVG(doc, width * SCALES[i], height * SCALES[i]);
		return images;
	}

	private static BufferedImage renderSVG(SVGDocument doc, double w, double h) {
		BufferedImage image = new BufferedImage((int) Math.ceil(w), (int) Math.ceil(h), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		doc.render(null, g, new ViewBox((float) w, (float) h));
		g.dispose();
		return image;
	}

	private static String computeKey(String identifier, int width, int height, boolean builtin) {
		return (builtin ? "@" : "") + identifier + "." + width + "." + height;
	}

}
