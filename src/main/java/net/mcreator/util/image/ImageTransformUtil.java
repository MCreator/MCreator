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

	private static class Point3D {
		double x;
		double y;
		double z;

		Point3D(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
	}

	private static class Matrix3D {
		double m00;
		double m01;
		double m02;
		double m10;
		double m11;
		double m12;
		double m20;
		double m21;
		double m22;

		Matrix3D(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21,
				double m22) {
			this.m00 = m00;
			this.m01 = m01;
			this.m02 = m02;
			this.m10 = m10;
			this.m11 = m11;
			this.m12 = m12;
			this.m20 = m20;
			this.m21 = m21;
			this.m22 = m22;
		}

		Matrix3D(Matrix3D m) {
			this.m00 = m.m00;
			this.m01 = m.m01;
			this.m02 = m.m02;
			this.m10 = m.m10;
			this.m11 = m.m11;
			this.m12 = m.m12;
			this.m20 = m.m20;
			this.m21 = m.m21;
			this.m22 = m.m22;
		}

		void invert() {
			double invDet = 1.0 / determinant();
			double nm00 = m22 * m11 - m21 * m12;
			double nm01 = -(m22 * m01 - m21 * m02);
			double nm02 = m12 * m01 - m11 * m02;
			double nm10 = -(m22 * m10 - m20 * m12);
			double nm11 = m22 * m00 - m20 * m02;
			double nm12 = -(m12 * m00 - m10 * m02);
			double nm20 = m21 * m10 - m20 * m11;
			double nm21 = -(m21 * m00 - m20 * m01);
			double nm22 = m11 * m00 - m10 * m01;
			m00 = nm00 * invDet;
			m01 = nm01 * invDet;
			m02 = nm02 * invDet;
			m10 = nm10 * invDet;
			m11 = nm11 * invDet;
			m12 = nm12 * invDet;
			m20 = nm20 * invDet;
			m21 = nm21 * invDet;
			m22 = nm22 * invDet;
		}

		double determinant() {
			return m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
		}

		final void mul(double factor) {
			m00 *= factor;
			m01 *= factor;
			m02 *= factor;

			m10 *= factor;
			m11 *= factor;
			m12 *= factor;

			m20 *= factor;
			m21 *= factor;
			m22 *= factor;
		}

		void transform(Point3D p) {
			double x = m00 * p.x + m01 * p.y + m02 * p.z;
			double y = m10 * p.x + m11 * p.y + m12 * p.z;
			double z = m20 * p.x + m21 * p.y + m22 * p.z;
			p.x = x;
			p.y = y;
			p.z = z;
		}

		void transform(Point2D pp) {
			Point3D p = new Point3D(pp.getX(), pp.getY(), 1.0);
			transform(p);
			pp.setLocation(p.x / p.z, p.y / p.z);
		}

		void mul(Matrix3D m) {
			double nm00 = m00 * m.m00 + m01 * m.m10 + m02 * m.m20;
			double nm01 = m00 * m.m01 + m01 * m.m11 + m02 * m.m21;
			double nm02 = m00 * m.m02 + m01 * m.m12 + m02 * m.m22;

			double nm10 = m10 * m.m00 + m11 * m.m10 + m12 * m.m20;
			double nm11 = m10 * m.m01 + m11 * m.m11 + m12 * m.m21;
			double nm12 = m10 * m.m02 + m11 * m.m12 + m12 * m.m22;

			double nm20 = m20 * m.m00 + m21 * m.m10 + m22 * m.m20;
			double nm21 = m20 * m.m01 + m21 * m.m11 + m22 * m.m21;
			double nm22 = m20 * m.m02 + m21 * m.m12 + m22 * m.m22;

			m00 = nm00;
			m01 = nm01;
			m02 = nm02;
			m10 = nm10;
			m11 = nm11;
			m12 = nm12;
			m20 = nm20;
			m21 = nm21;
			m22 = nm22;
		}
	}
}
