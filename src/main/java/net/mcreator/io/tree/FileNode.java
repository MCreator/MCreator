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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileNode {

	public List<FileNode> childs;
	public List<FileNode> leafs;
	public String data;
	public String incrementalPath;

	public FileNode(String nodeValue, String incrementalPath) {
		childs = new ArrayList<>();
		leafs = new ArrayList<>();
		data = nodeValue;
		this.incrementalPath = incrementalPath;
	}

	public boolean isLeaf() {
		return childs.isEmpty() && leafs.isEmpty();
	}

	public void addElement(String currentPath, String[] list) {
		while (list[0] == null || list[0].equals(""))
			list = Arrays.copyOfRange(list, 1, list.length);

		FileNode currentChild = new FileNode(list[0], currentPath + "/" + list[0]);
		if (list.length == 1) {
			leafs.add(currentChild);
		} else {
			int index = childs.indexOf(currentChild);
			if (index == -1) {
				childs.add(currentChild);
				currentChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
			} else {
				FileNode nextChild = childs.get(index);
				nextChild.addElement(currentChild.incrementalPath, Arrays.copyOfRange(list, 1, list.length));
			}
		}
	}

	@Override public boolean equals(Object obj) {
		if (obj instanceof FileNode) {
			FileNode cmpObj = (FileNode) obj;
			return incrementalPath.equals(cmpObj.incrementalPath) && data.equals(cmpObj.data);
		}
		return false;
	}

	@Override public String toString() {
		return data;
	}

}