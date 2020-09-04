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

package net.mcreator.minecraft;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnbt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureUtils {

	private static final Logger LOG = LogManager.getLogger("Structure Utils");

	public static void renamePrefixInStructures(File fileToFix, String oldmodid, String newmodid) {
		try {
			FileInputStream fis = new FileInputStream(fileToFix);
			NBTInputStream nbt = new NBTInputStream(fis);
			Tag tag = nbt.readTag();
			Tag out = replaceAllStringTags(tag, oldmodid + ":", newmodid + ":");
			NBTOutputStream nbtOutputStream = new NBTOutputStream(new FileOutputStream(fileToFix));
			nbtOutputStream.writeTag(out);
			nbt.close();
			nbtOutputStream.close();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private static Tag replaceAllStringTags(Tag tag, String from, String to) {
		if (tag instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag) tag;
			Map<String, Tag> map = new HashMap<>(compoundTag.getValue());
			map.replaceAll((key, value) -> replaceAllStringTags(value, from, to));
			return new CompoundTag(compoundTag.getName(), map);
		} else if (tag instanceof ListTag) {
			ListTag listTag = (ListTag) tag;
			List<Tag> list = new ArrayList<>(listTag.getValue());
			list.replaceAll(value -> replaceAllStringTags(value, from, to));
			return new ListTag(listTag.getName(), listTag.getType(), list);
		} else if (tag instanceof StringTag) {
			StringTag stringTag = (StringTag) tag;
			return new StringTag(stringTag.getName(), stringTag.getValue().replace(from, to));
		}
		return tag;
	}

}
