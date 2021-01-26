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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FolderElement implements IElement {

	public static final FolderElement ROOT = new FolderElement("~", null);

	private String name;
	protected List<FolderElement> children;

	// Must not be serialized due to circular references!
	// Populated by call to updateStructure from workspace loading mechanism
	private transient FolderElement parent;

	public FolderElement(String name, FolderElement parent) {
		this.name = name;
		this.parent = parent;
		this.children = new ArrayList<>();
	}

	public void updateStructure() {
		children.forEach(child -> {
			child.parent = this;
			child.updateStructure();
		});
	}

	public void addChild(FolderElement child) {
		this.children.add(child);
		child.parent = this;
	}

	public void removeChild(FolderElement child) {
		this.children.remove(child);
	}

	public void setChildren(ArrayList<FolderElement> children) {
		this.children = children;
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

	public boolean isRoot() {
		return this.equals(ROOT);
	}

	public FolderElement getParent() {
		return this.parent;
	}

	public String getPath() {
		List<String> elements = buildPath(new ArrayList<>());
		Collections.reverse(elements);
		return String.join("/", elements);
	}

	private List<String> buildPath(List<String> storage) {
		storage.add(this.name);

		if (!this.isRoot())
			return parent.buildPath(storage);
		else
			return storage;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override public String getName() {
		return name;
	}

	@Override public String toString() {
		return name;
	}

	@Override public boolean equals(Object element) {
		if (element == this)
			return true;

		if (element == null && this.name.equals(ROOT.name))
			return true;

		// comparing by path
		if (element instanceof String)
			return element.equals(this.getPath());

		return element instanceof FolderElement && name.equals(((FolderElement) element).name) && (getParent() == null
				|| getParent().equals(((FolderElement) element).getParent()));
	}

	@Override public int hashCode() {
		// Josh Bloch's Hash Code with prime numbers
		return name.hashCode() + 23 * (getParent() != null ? getParent().hashCode() : 0);
	}

}
