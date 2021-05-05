/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.util.math;

import java.awt.geom.Point2D;

public class Matrix3D {
	public double m00;
	public double m01;
	public double m02;
	public double m10;
	public double m11;
	public double m12;
	public double m20;
	public double m21;
	public double m22;

	public Matrix3D(double m00, double m01, double m02, double m10, double m11, double m12, double m20, double m21,
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

	public Matrix3D(Matrix3D m) {
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

	public void invert() {
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

	public double determinant() {
		return m00 * (m11 * m22 - m12 * m21) + m01 * (m12 * m20 - m10 * m22) + m02 * (m10 * m21 - m11 * m20);
	}

	public void mul(double factor) {
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

	public void transform(Point3D p) {
		double x = m00 * p.x + m01 * p.y + m02 * p.z;
		double y = m10 * p.x + m11 * p.y + m12 * p.z;
		double z = m20 * p.x + m21 * p.y + m22 * p.z;
		p.x = x;
		p.y = y;
		p.z = z;
	}

	public void transform(Point2D pp) {
		Point3D p = new Point3D(pp.getX(), pp.getY(), 1.0);
		transform(p);
		pp.setLocation(p.x / p.z, p.y / p.z);
	}

	public void mul(Matrix3D m) {
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
