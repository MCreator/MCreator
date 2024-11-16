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

package net.mcreator.io.tree;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileNode<T> {

	public final List<FileNode<T>> childs;
	public final List<FileNode<T>> leafs;
	public final String data;
	public final String incrementalPath;

	@Nullable public final T object;

	public FileNode(String nodeValue, String incrementalPath) {
		this(nodeValue, incrementalPath, null);
	}

	public FileNode(String nodeValue, String incrementalPath, @Nullable T object) {
		childs = new ArrayList<>();
		leafs = new ArrayList<>();
		data = nodeValue;
		this.incrementalPath = incrementalPath;
		this.object = object;
	}

	@Nullable public T getObject() {
		return object;
	}

	public boolean isLeaf() {
		return childs.isEmpty() && leafs.isEmpty();
	}

	public String[] splitPath() {
		String[] path = incrementalPath.split(":%:");
		int idx = path.length - 1;
		if (path[idx].startsWith("/"))
			path[idx] = path[idx].substring(1);
		return path;
	}

	void addElement(String currentPath, String[] list, @Nullable T object) {
		while (list[0] == null || list[0].isEmpty())
			list = Arrays.copyOfRange(list, 1, list.length);

		FileNode<T> currentChild = new FileNode<>(list[0], currentPath + "/" + list[0], object);
		if (list.length == 1) {
			leafs.add(currentChild);
		} else {
			int index = childs.indexOf(currentChild);
			if (index == -1) {
				childs.add(currentChild);
				currentChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length), object);
			} else {
				FileNode<T> nextChild = childs.get(index);
				nextChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length), object);
			}
		}
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof FileNode<?> cmpObj) {
			return incrementalPath.equals(cmpObj.incrementalPath) && data.equals(cmpObj.data);
		}
		return false;
	}

	@Override public String toString() {
		return data;
	}

}