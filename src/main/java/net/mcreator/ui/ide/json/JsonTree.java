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

package net.mcreator.ui.ide.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.laf.renderer.AstTreeCellRendererCustom;
import org.fife.rsta.ac.AbstractSourceTree;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

public class JsonTree extends AbstractSourceTree {

	private final DefaultTreeModel model;
	private RSyntaxTextArea textArea;
	private final JsonTree.Listener listener;

	public JsonTree() {
		this(false);
	}

	public JsonTree(boolean sorted) {
		setSorted(sorted);
		setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		setRootVisible(false);
		setCellRenderer(new AstTreeCellRendererCustom());
		model = new DefaultTreeModel(new DefaultMutableTreeNode());
		setModel(model);
		listener = new JsonTree.Listener();
		addTreeSelectionListener(listener);
	}

	private void update(String json) {
		try {
			JsonElement element = JsonParser.parseString(json);

			DefaultMutableTreeNode root = new DefaultMutableTreeNode("JSON root");
			addJsonElementToTree(element, null, root);

			model.setRoot(root);

			refresh();
			TreeUtils.expandAllNodes(this, 0, getRowCount());
		} catch (Exception ignored) {
		}
	}

	private void addJsonElementToTree(JsonElement element, String prevKey, DefaultMutableTreeNode root) {
		if (element instanceof JsonObject) {
			JsonObject object = (JsonObject) element;
			JsonObjectNode subroot = new JsonObjectNode(object, "{" + (prevKey == null ? "object" : prevKey) + "}");
			for (String key : object.keySet())
				addJsonElementToTree(object.get(key), key, subroot);
			root.add(subroot);
		} else if (element instanceof JsonArray) {
			JsonArray array = (JsonArray) element;
			JsonArrayNode subroot = new JsonArrayNode(array, "[" + (prevKey == null ? "array" : prevKey) + "]");
			for (JsonElement obj : array)
				addJsonElementToTree(obj, null, subroot);
			root.add(subroot);
		} else {
			root.add(new JsonNode(element, (prevKey == null ? "" : (prevKey + " : ")) + element.toString()));
		}
	}

	@Override public void updateUI() {
		super.updateUI();
		setCellRenderer(new AstTreeCellRendererCustom());
	}

	@Override public void expandInitialNodes() {
		TreeUtils.expandAllNodes(this, 0, getRowCount());
	}

	@Override public boolean gotoSelectedElement() {
		return false;
	}

	@Override public void listenTo(RSyntaxTextArea textArea) {
		if (this.textArea != null) {
			uninstall();
		}

		// Nothing new to listen to
		if (textArea == null) {
			return;
		}

		// Listen for future language changes in the text editor
		this.textArea = textArea;

		update(textArea.getText());
		textArea.getDocument().addDocumentListener(listener);
	}

	@Override public void uninstall() {
		if (textArea != null) {
			textArea.getDocument().removeDocumentListener(listener);
			textArea = null;
		}
	}

	private class Listener implements DocumentListener, TreeSelectionListener {

		@Override public void valueChanged(TreeSelectionEvent e) {
		}

		@Override public void insertUpdate(DocumentEvent documentEvent) {
			update(textArea.getText());
		}

		@Override public void removeUpdate(DocumentEvent documentEvent) {

		}

		@Override public void changedUpdate(DocumentEvent documentEvent) {
			update(textArea.getText());
		}
	}

	public static class JsonNode extends DefaultMutableTreeNode {

		JsonElement element;

		public JsonNode(JsonElement element, String s) {
			super(s);
			this.element = element;
		}

		public JsonElement getElement() {
			return element;
		}
	}

	public static class JsonObjectNode extends JsonNode {

		public JsonObjectNode(JsonElement e, String s) {
			super(e, s);
		}
	}

	public static class JsonArrayNode extends JsonNode {

		public JsonArrayNode(JsonElement e, String s) {
			super(e, s);
		}
	}

}
