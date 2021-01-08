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

package net.mcreator.ui.browser;

import javax.swing.tree.DefaultMutableTreeNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProjectBrowserFilterTreeNode extends DefaultMutableTreeNode {

	private String filter;
	private boolean passed = true;
	private final List<ProjectBrowserFilterTreeNode> filteredChildren = new ArrayList<>();

	ProjectBrowserFilterTreeNode(Object userObject) {
		super(userObject);
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter == null ? "" : filter;
		passed = false;
		filteredChildren.clear();
		if (filter == null) {
			passed = true;
			passFilterDown(null);
		} else if (pass(this)) {
			passed = true;
			passFilterDown(filter);
		} else {
			passFilterDown(filter);
			passed = filteredChildren.size() != 0;
		}
	}

	private boolean pass(ProjectBrowserFilterTreeNode node) {
		if (getFilter().trim().equals(""))
			return true;

		return node.getUserObject().toString().toLowerCase(Locale.ENGLISH).trim()
				.contains(getFilter().toLowerCase(Locale.ENGLISH).trim());

	}

	private void passFilterDown(String filter) {
		int realChildCount = super.getChildCount();
		for (int i = 0; i < realChildCount; i++) {
			ProjectBrowserFilterTreeNode realChild = (ProjectBrowserFilterTreeNode) super.getChildAt(i);
			realChild.setFilter(filter);
			if (realChild.isPassed()) {
				filteredChildren.add(realChild);
			}
		}
	}

	public void add(ProjectBrowserFilterTreeNode node) {
		super.add(node);
		node.setFilter(filter);
		if (node.isPassed()) {
			filteredChildren.add(node);
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.tree.DefaultMutableTreeNode#remove(int)
	 */
	@Override public void remove(int childIndex) {
		if (filter != null) {
			setFilter("");
		}
		super.remove(childIndex);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.tree.DefaultMutableTreeNode#getChildCount()
	 */
	@Override public int getChildCount() {
		if (filter == null) {
			return super.getChildCount();
		}
		return (filteredChildren.size());
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see javax.swing.tree.DefaultMutableTreeNode#getChildAt(int)
	 */
	@Override public ProjectBrowserFilterTreeNode getChildAt(int index) {
		if (filter == null) {
			return (ProjectBrowserFilterTreeNode) super.getChildAt(index);
		}
		return filteredChildren.get(index);
	}

	private boolean isPassed() {
		return passed;
	}
}