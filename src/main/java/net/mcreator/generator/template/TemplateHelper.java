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

package net.mcreator.generator.template;

import com.google.gson.Gson;
import net.mcreator.util.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

@SuppressWarnings("unused") public class TemplateHelper {

	public String mapToString(double val, double min, double max, String... values) {
		if (val > max)
			val = max;
		if (val < min)
			val = min;

		int idx = (int) Math.round(((val - min) / (max - min)) * (values.length - 1));
		return values[idx];
	}

	public double random(String seed) {
		long hash = 0;
		for (char c : seed.toCharArray()) {
			hash = 31L * hash + c;
		}
		return new Random(hash).nextDouble();
	}

	public long randomlong(String seed) {
		long hash = 0;
		for (char c : seed.toCharArray()) {
			hash = 31L * hash + c;
		}
		return new Random(hash).nextLong();
	}

	public int randompositiveint(String seed) {
		long hash = 0;
		for (char c : seed.toCharArray()) {
			hash = 31L * hash + c;
		}
		return new Random(hash).nextInt(Integer.MAX_VALUE);
	}

	public String colorToHexString(Color color) {
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	public String obj2str(Object object) {
		return new Gson().toJson(object);
	}

	public String lowercaseFirstLetter(String str) {
		return StringUtils.lowercaseFirstLetter(str);
	}

	public <T> List<T> removeDuplicates(List<T> original) {
		return new ArrayList<>(new LinkedHashSet<>(original));
	}

}
