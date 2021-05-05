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

package net.mcreator.util.image;

import net.mcreator.util.math.Matrix3D;
import net.mcreator.util.math.Point3D;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

public class ImageTransformUtil {
	public static BufferedImage computeImage(BufferedImage image, Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
		int w = image.getWidth();
		int h = image.getHeight();

		BufferedImage result = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);

		Point2D ip0 = new Point2D.Double(0, 0);
		Point2D ip1 = new Point2D.Double(0, h);
		Point2D ip2 = new Point2D.Double(w, h);
		Point2D ip3 = new Point2D.Double(w, 0);

		Matrix3D m = computeProjectionMatrix(new Point2D[] { p0, p1, p2, p3 }, new Point2D[] { ip0, ip1, ip2, ip3 });
		Matrix3D mInv = new Matrix3D(m);
		mInv.invert();

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				Point2D p = new Point2D.Double(x, y);
				mInv.transform(p);
				int ix = (int) p.getX();
				int iy = (int) p.getY();
				if (ix >= 0 && ix < w && iy >= 0 && iy < h) {
					int rgb = image.getRGB(ix, iy);
					result.setRGB(x, y, rgb);
				}
			}
		}
		return result;
	}

	private static Matrix3D computeProjectionMatrix(Point2D[] p0, Point2D[] p1) {
		Matrix3D m0 = computeProjectionMatrix(p0);
		Matrix3D m1 = computeProjectionMatrix(p1);
		m1.invert();
		m0.mul(m1);
		return m0;
	}

	private static Matrix3D computeProjectionMatrix(Point2D[] p) {
		Matrix3D m = new Matrix3D(p[0].getX(), p[1].getX(), p[2].getX(), p[0].getY(), p[1].getY(), p[2].getY(), 1, 1,
				1);
		Point3D p3 = new Point3D(p[3].getX(), p[3].getY(), 1);
		Matrix3D mInv = new Matrix3D(m);
		mInv.invert();
		mInv.transform(p3);
		m.m00 *= p3.x;
		m.m01 *= p3.y;
		m.m02 *= p3.z;
		m.m10 *= p3.x;
		m.m11 *= p3.y;
		m.m12 *= p3.z;
		m.m20 *= p3.x;
		m.m21 *= p3.y;
		m.m22 *= p3.z;
		return m;
	}

}
