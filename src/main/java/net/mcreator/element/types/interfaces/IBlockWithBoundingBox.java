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

package net.mcreator.element.types.interfaces;

import javax.annotation.Nonnull;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("unused") public interface IBlockWithBoundingBox {
	// A list of non-empty bounding box entries
	@Nonnull List<BoxEntry> getValidBoundingBoxes();

	// Empty list or all bounding boxes are in subtract mode
	default boolean isBoundingBoxEmpty() {
		return positiveBoundingBoxes().isEmpty();
	}

	default boolean isFullCube() {
		return getValidBoundingBoxes().size() > 0 && getValidBoundingBoxes().stream().allMatch(BoxEntry::isFullCube);
	}

	default List<BoxEntry> positiveBoundingBoxes() {
		return getValidBoundingBoxes().stream().filter(box -> !box.subtract).collect(Collectors.toList());
	}

	default List<BoxEntry> negativeBoundingBoxes() {
		return getValidBoundingBoxes().stream().filter(box -> box.subtract).collect(Collectors.toList());
	}

	class BoxEntry {
		public double mx;
		public double my;
		public double mz;
		public double Mx;
		public double My;
		public double Mz;
		public boolean subtract;

		public BoxEntry() {
			this.Mx = 16;
			this.My = 16;
			this.Mz = 16;
		}

		public boolean isNotEmpty() {
			return mx != Mx && my != My && mz != Mz;
		}

		public boolean isFullCube() {
			return mx == 0 && my == 0 && mz == 0 && Mx == 16 && My == 16 && Mz == 16 && !subtract;
		}
	}
}
