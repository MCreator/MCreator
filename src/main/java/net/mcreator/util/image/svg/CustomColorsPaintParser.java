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

import com.github.weisj.jsvg.attributes.paint.AwtSVGPaint;
import com.github.weisj.jsvg.attributes.paint.PaintParser;
import com.github.weisj.jsvg.attributes.paint.SVGPaint;
import com.github.weisj.jsvg.parser.AttributeNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

public record CustomColorsPaintParser(Color paint, PaintParser delegate) implements PaintParser {

	@Override public Color parseColor(@Nonnull String value, @Nonnull AttributeNode attributeNode) {
		return paint;
	}

	@Override public SVGPaint parsePaint(@Nullable String value, @Nonnull AttributeNode attributeNode) {
		SVGPaint retval = delegate.parsePaint(value, attributeNode);
		if (retval == null || retval instanceof AwtSVGPaint) {
			return new AwtSVGPaint(paint);
		} else {
			return retval;
		}
	}

}
