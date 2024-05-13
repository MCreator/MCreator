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

package net.mcreator.ui.laf.renderer;

import com.google.gson.JsonElement;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.ide.json.JsonTree;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.lang.reflect.Method;

public class AstTreeCellRendererCustom extends DefaultTreeCellRenderer {

	private static final Logger LOG = LogManager.getLogger(AstTreeCellRendererCustom.class);

	public AstTreeCellRendererCustom() {
		setBorderSelectionColor(Theme.current().getBackgroundColor());
		setBackground(Theme.current().getBackgroundColor());
		setBackgroundSelectionColor(Theme.current().getInterfaceAccentColor());
		ComponentUtils.deriveFont(this, 11);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		setOpaque(true);

		if (!sel) {
			setBackground(Theme.current().getBackgroundColor());
			setForeground(Theme.current().getForegroundColor());
		} else {
			setBackground(Theme.current().getInterfaceAccentColor());
			setForeground(Theme.current().getBackgroundColor());
		}

		switch (value) {
		case JsonTree.JsonObjectNode ignored -> {
			setIcon(UIRES.get("16px.jsonobj"));
			setText(value.toString());
		}
		case JsonTree.JsonArrayNode ignored -> {
			setIcon(UIRES.get("16px.jsonarray"));
			setText(value.toString());
		}
		case JsonTree.JsonNode node -> {
			JsonElement element = node.getElement();
			String type = null;
			if (element.isJsonNull())
				type = "null";
			if (element.isJsonPrimitive()) {
				if (element.getAsJsonPrimitive().isBoolean())
					type = "bool";
				if (element.getAsJsonPrimitive().isString())
					type = "text";
				if (element.getAsJsonPrimitive().isNumber())
					type = "number";
			}
			setText(value + (type == null ? "" : (" [" + type + "]")));
			setIcon(UIRES.get("16px.jsonel"));
		}
		case null, default -> {
			try {
				Class<?> treeNodeClass = Class.forName("org.fife.rsta.ac.java.tree.JavaTreeNode");
				Method text = treeNodeClass.getMethod("getText", boolean.class);
				Method icon = treeNodeClass.getMethod("getIcon");
				icon.setAccessible(true);
				text.setAccessible(true);
				setText((String) text.invoke(value, sel));

				setIcon(RSTAIcons.themeRSTAIcon((Icon) icon.invoke(value)));
			} catch (Exception e) {
				if (value instanceof DefaultMutableTreeNode) {
					setText(value.toString());
				} else {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		}
		return this;
	}

}
