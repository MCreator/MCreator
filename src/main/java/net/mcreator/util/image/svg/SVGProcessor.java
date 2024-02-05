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

package net.mcreator.util.image.svg;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.parser.DefaultParserProvider;
import com.github.weisj.jsvg.parser.SVGLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BaseMultiResolutionImage;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SVGProcessor {

	private static final Logger LOG = LogManager.getLogger(SVGProcessor.class);

	private static final ExecutorService SVG_LOADER_THREAD = Executors.newSingleThreadExecutor();
	private static final SVGLoader SVG_LOADER = new SVGLoader();

	private static final Double[] SCALES;

	static {
		Set<Double> scales = new TreeSet<>();
		scales.add(1.0);
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		for (GraphicsDevice device : env.getScreenDevices()) {
			GraphicsConfiguration config = device.getDefaultConfiguration();
			AffineTransform transform = config.getDefaultTransform();
			double scale = transform.getScaleX();
			if (scale < 4.0)
				scales.add(transform.getScaleX());
		}
		LOG.debug("Loaded screen scales: " + scales);
		SCALES = scales.toArray(new Double[0]);
	}

	public static synchronized SVGDocument loadSVG(URL url, @Nullable Color paint) {
		try {
			SVGDocument doc = SVG_LOADER_THREAD.submit(() -> {
				if (paint == null) {
					return SVG_LOADER.load(url);
				} else {
					return SVG_LOADER.load(url, new DefaultParserProvider() {
						@Override public @Nonnull PaintParser createPaintParser() {
							return new CustomColorsPaintParser(paint, super.createPaintParser());
						}
					});
				}
			}).get();
			if (doc == null)
				throw new RuntimeException("SVG resource not found: " + url);
			return doc;
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}

	public static ImageIcon getMultiResolutionIcon(SVGDocument doc, int width, int height) {
		return new ImageIcon(new BaseMultiResolutionImage(SVGProcessor.getResolutionVariants(doc, width, height)));
	}

	public static Image[] getResolutionVariants(SVGDocument doc, int width, int height) {
		if (width == 0)
			width = (int) Math.ceil(doc.size().getWidth());
		if (height == 0)
			height = (int) Math.ceil(doc.size().getHeight());

		Image[] images = new Image[SCALES.length];
		for (int i = 0; i < SCALES.length; i++)
			images[i] = renderSVG(doc, width * SCALES[i], height * SCALES[i]);
		return images;
	}

	public static BufferedImage renderSVG(SVGDocument doc, double w, double h) {
		BufferedImage image = new BufferedImage((int) Math.ceil(w), (int) Math.ceil(h), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_DEFAULT);
		doc.render(null, g, new ViewBox((float) w, (float) h));
		g.dispose();
		return image;
	}

}
