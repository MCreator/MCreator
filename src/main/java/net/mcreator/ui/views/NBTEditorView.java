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

package net.mcreator.ui.views;

import net.mcreator.ui.MCreator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jnbt.CompoundTag;
import org.jnbt.ListTag;
import org.jnbt.NBTInputStream;
import org.jnbt.Tag;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class NBTEditorView extends ViewBase {

	private static final Logger LOG = LogManager.getLogger("NBT editor");

	private final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
	private final JTree tree = new JTree(model);
	private final File file;

	public NBTEditorView(MCreator mcreator, File nbtfile) {
		super(mcreator);
		this.file = nbtfile;

		tree.setCellRenderer(new NBTCellRenderer());
		tree.setRowHeight(18);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setOpaque(false);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(BorderFactory.createMatteBorder(10, 10, 10, 10, tree.getBackground()));

		add("Center", scrollPane);

		loadFromFile();
	}

	private void loadFromFile() {
		try {
			FileInputStream fis = new FileInputStream(file);
			NBTInputStream nbt = new NBTInputStream(fis);
			Tag tag = nbt.readTag();

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("JSON root");
			addTagToNode(tag, root);

			model.setRoot(root);

			tree.expandRow(1);
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void addTagToNode(Tag tag, DefaultMutableTreeNode root) {
		if (tag instanceof CompoundTag compoundTag) {
			NBTTagNode subroot = new NBTTagNode(compoundTag, compoundTag.getName());
			for (String key : compoundTag.getValue().keySet())
				addTagToNode(compoundTag.getValue().get(key), subroot);
			root.add(subroot);
		} else if (tag instanceof ListTag listTag) {
			NBTTagNode subroot = new NBTTagNode(listTag, listTag.getName());
			for (Tag taginlist : listTag.getValue())
				addTagToNode(taginlist, subroot);
			root.add(subroot);
		} else {
			root.add(new NBTTagNode(tag, tag.toString()));
		}
	}

	@Override public String getViewName() {
		return file.getName();
	}

	public static class NBTTagNode extends DefaultMutableTreeNode {

		private final Tag tag;

		public NBTTagNode(Tag tag, String s) {
			super(s);
			this.tag = tag;
		}

		public Tag getElement() {
			return tag;
		}
	}

	private static class NBTCellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			setText(value.toString().isBlank() ? "(blank)" : value.toString());
			return a;
		}

	}
}
