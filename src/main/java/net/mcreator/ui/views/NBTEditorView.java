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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.laf.SlickTreeUI;
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
		tree.setOpaque(false);
		tree.setUI(new SlickTreeUI());

		JScrollPane scrollPane = new JScrollPane(tree);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));

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

			TreeUtils.expandAllNodes(tree, 0, tree.getRowCount());
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}

	private void addTagToNode(Tag tag, DefaultMutableTreeNode root) {
		if (tag instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag) tag;
			NBTTagNode subroot = new NBTTagNode(compoundTag, compoundTag.getName());
			for (String key : compoundTag.getValue().keySet())
				addTagToNode(compoundTag.getValue().get(key), subroot);
			root.add(subroot);
		} else if (tag instanceof ListTag) {
			ListTag listTag = (ListTag) tag;
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
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			setOpaque(false);

			JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			a.setOpaque(true);
			ComponentUtils.deriveFont(a, 11);

			if (node instanceof NBTTagNode) {
				NBTTagNode nbtTagNode = (NBTTagNode) node;
				setText(nbtTagNode.toString());
			}

			if (sel) {
				a.setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				a.setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			} else {
				a.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				a.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
			}
			return a;
		}

	}
}
