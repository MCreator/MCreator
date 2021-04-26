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

package net.mcreator.util;

public class TimeUtils {

	public final static long ONE_SECOND = 1000;
	public final static long SECONDS = 60;

	public final static long ONE_MINUTE = ONE_SECOND * 60;
	public final static long MINUTES = 60;

	public final static long ONE_HOUR = ONE_MINUTE * 60;
	public final static long HOURS = 24;

	public final static long ONE_DAY = ONE_HOUR * 24;

	public static String millisToLongDHMS(long millis) {
		StringBuilder res = new StringBuilder();
		long remainder;
		if (millis >= ONE_SECOND) {
			remainder = millis / ONE_DAY;
			if (remainder > 0) {
				millis -= remainder * ONE_DAY;
				res.append(remainder).append(" day").append(remainder > 1 ? "s" : "")
						.append(millis >= ONE_MINUTE ? ", " : "");
			}

			remainder = millis / ONE_HOUR;
			if (remainder > 0) {
				millis -= remainder * ONE_HOUR;
				res.append(remainder).append(" hour").append(remainder > 1 ? "s" : "")
						.append(millis >= ONE_MINUTE ? ", " : "");
			}

			remainder = millis / ONE_MINUTE;
			if (remainder > 0) {
				millis -= remainder * ONE_MINUTE;
				res.append(remainder).append(" minute").append(remainder > 1 ? "s" : "");
			}

			if (!res.toString().equals("") && millis >= ONE_SECOND) {
				res.append(" and ");
			}

			remainder = millis / ONE_SECOND;
			if (remainder > 0) {
				res.append(remainder).append(" second").append(remainder > 1 ? "s" : "");
			}
			return res.toString();
		} else {
			return "less than a second";
		}
	}

}
