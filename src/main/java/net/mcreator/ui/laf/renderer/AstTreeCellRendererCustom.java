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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.fife.rsta.ac.java.DecoratableIcon;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class AstTreeCellRendererCustom extends DefaultTreeCellRenderer {

	private static final Logger LOG = LogManager.getLogger(AstTreeCellRendererCustom.class);

	public AstTreeCellRendererCustom() {
		setBorderSelectionColor((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		setBackgroundSelectionColor((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
		ComponentUtils.deriveFont(this, 11);
	}

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
			int row, boolean hasFocus) {
		super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
		setOpaque(true);

		if (!sel) {
			setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
			setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));
		} else {
			setBackground((Color) UIManager.get("MCreatorLAF.MAIN_TINT"));
			setForeground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		}

		if (value instanceof JsonTree.JsonObjectNode) {
			setIcon(UIRES.get("16px.jsonobj.gif"));
			setText(value.toString());
		} else if (value instanceof JsonTree.JsonArrayNode) {
			setIcon(UIRES.get("16px.jsonarray.gif"));
			setText(value.toString());
		} else if (value instanceof JsonTree.JsonNode node) {
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
			setText("<html>" + value + (type == null ? "" : ("&nbsp;&nbsp;<small color=gray>[" + type + "]")));
			setIcon(UIRES.get("16px.jsonel.gif"));
		} else {
			try {
				Class<?> treeNodeClass = Class.forName("org.fife.rsta.ac.java.tree.JavaTreeNode");
				Method text = treeNodeClass.getMethod("getText", boolean.class);
				Method icon = treeNodeClass.getMethod("getIcon");
				icon.setAccessible(true);
				text.setAccessible(true);
				setText((String) text.invoke(value, sel));

				Icon icon_obj = (Icon) icon.invoke(value);
				if (icon_obj instanceof DecoratableIcon decoratableIcon) {
					setIcon(rstaIconToThemeIcon(decoratableIcon));
				} else if (icon_obj instanceof ImageIcon imageIcon) {
					setIcon(rstaIconToThemeIcon(imageIcon));
				} else {
					setIcon(icon_obj);
				}
			} catch (Exception e) {
				if (value instanceof DefaultMutableTreeNode) {
					setText(value.toString());
				} else {
					LOG.warn(e.getMessage(), e);
				}
			}
		}
		return this;
	}

	private static final Map<Icon, Icon> lookup_cache = new IdentityHashMap<>();

	private static Icon rstaIconToThemeIcon(ImageIcon imageIcon) {
		if (lookup_cache.containsKey(imageIcon))
			return lookup_cache.get(imageIcon);

		if (imageIcon.getDescription().contains("org/fife/rsta/ac/java")) {
			return UIRES.get("rsta." + FilenameUtils.getName(imageIcon.getDescription()));
		}

		return imageIcon;
	}

	private static Icon rstaIconToThemeIcon(DecoratableIcon icon) {
		if (lookup_cache.containsKey(icon))
			return lookup_cache.get(icon);

		try {
			Class<?> decoratableIconClass = Class.forName("org.fife.rsta.ac.java.DecoratableIcon");

			Field mainIconFiled = decoratableIconClass.getDeclaredField("mainIcon");
			mainIconFiled.setAccessible(true);
			Icon mainIcon = (Icon) mainIconFiled.get(icon);

			DecoratableIcon newIcon;
			if (mainIcon instanceof DecoratableIcon decoratableIcon) {
				newIcon = new DecoratableIcon(rstaIconToThemeIcon(decoratableIcon));
			} else if (mainIcon instanceof ImageIcon imageIcon) {
				newIcon = new DecoratableIcon(rstaIconToThemeIcon(imageIcon));
			} else {
				newIcon = new DecoratableIcon(mainIcon);
			}

			Field decorationsFiled = decoratableIconClass.getDeclaredField("decorations");
			decorationsFiled.setAccessible(true);
			List<?> decorationsList = (List<?>) decorationsFiled.get(icon);

			if (decorationsList != null) {
				for (Object obj : decorationsList) {
					if (obj instanceof DecoratableIcon decoratableIcon) {
						newIcon.addDecorationIcon(rstaIconToThemeIcon(decoratableIcon));
					} else if (obj instanceof ImageIcon imageIcon) {
						newIcon.addDecorationIcon(rstaIconToThemeIcon(imageIcon));
					} else if (obj instanceof Icon _icon) {
						newIcon.addDecorationIcon(_icon);
					}
				}
			}

			lookup_cache.put(icon, newIcon);

			return newIcon;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return icon;
	}

}
