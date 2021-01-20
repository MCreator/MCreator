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

package net.mcreator.workspace.elements;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FolderElement implements IElement {

	public static final FolderElement ROOT = new FolderElement("<root>", null);

	private String name;
	private final List<FolderElement> children = new ArrayList<>();

	private FolderElement optionalParent;

	public FolderElement(String name, FolderElement parent) {
		this.name = name;
		this.optionalParent = parent;
	}

	public void updateStructure() {
		children.forEach(child -> {
			child.optionalParent = this;
			child.updateStructure();
		});
	}

	@Override public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void addChild(FolderElement child) {
		this.children.add(child);
		child.optionalParent = this;
	}

	public void removeChild(FolderElement child) {
		this.children.remove(child);
		child.optionalParent = null;
	}

	public List<FolderElement> getDirectFolderChildren() {
		return children;
	}

	/**
	 * Used to determine all folders from the root element down, when called on the root element
	 *
	 * @return List of all children
	 */
	public List<FolderElement> getRecursiveFolderChildren() {
		List<FolderElement> childrenList = new ArrayList<>(children);
		children.forEach(child -> childrenList.addAll(child.getRecursiveFolderChildren()));
		return childrenList;
	}

	@Nullable public FolderElement getParent() {
		return this.optionalParent;
	}

	@Override public String toString() {
		return getName();
	}

	@Override public boolean equals(Object element) {
		if (element == null && this.getName().equals(ROOT.getName()))
			return true;

		return element instanceof FolderElement && name.equals(((FolderElement) element).getName()) && (
				getParent() == null || getParent().equals(((FolderElement) element).getParent()));
	}

	@Override public int hashCode() {
		// Josh Bloch's Hash Code with prime numbers
		return name.hashCode() + 23 * (getParent() != null ? getParent().hashCode() : 0);
	}

}
