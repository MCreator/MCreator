/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2022, Pylo, opensource contributors
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

import java.util.HashMap;
import java.util.Map;

/**
 * e-mail: 3154934427@qq.com
 * 映射工具类
 *
 * @author cdc123
 * @classname MapUtils
 * @date 2022/8/16 19:08
 */
public class MapUtils {
	public static Map<String,String> createMap(String... values){
		HashMap<String,String> hashMap = new HashMap<>();
		for (int i=0;i< values.length;i+=2){
			hashMap.put(values[i],values[i+1]);
		}
		return hashMap;
	}
}
