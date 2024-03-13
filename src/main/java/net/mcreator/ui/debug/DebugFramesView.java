/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.debug;

import com.sun.jdi.*;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.component.util.TreeUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.StringUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static net.mcreator.ui.debug.DebugPanel.DEBUG_COLOR;

public class DebugFramesView extends JPanel {

	private final CardLayout layout = new CardLayout();

	private final JTree frames = new JTree();

	public DebugFramesView() {
		setLayout(layout);
		setOpaque(false);

		frames.setCellRenderer(new FramesCellRenderer());
		frames.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		frames.setBackground(Theme.current().getSecondAltBackgroundColor());
		frames.setRootVisible(false);
		frames.setShowsRootHandles(true);
		frames.putClientProperty("FlatLaf.style",
				Map.of("selectionBackground", DEBUG_COLOR, "selectionInactiveBackground", DEBUG_COLOR));

		JLabel noframes = L10N.label("debug.frames.no_frames");
		noframes.setFont(noframes.getFont().deriveFont(13f));
		noframes.setForeground(Theme.current().getAltForegroundColor());

		JScrollPane framesScroll = new JScrollPane(frames);
		framesScroll.setOpaque(false);
		framesScroll.setBorder(null);
		framesScroll.getViewport().setOpaque(false);
		framesScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(PanelUtils.totalCenterInPanel(noframes), "empty");
		add(framesScroll, "frames");
	}

	public void hideFrames() {
		layout.show(this, "empty");
	}

	public void showFrames(List<StackFrame> stackFrames) {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Stack Frames");

		List<DefaultMutableTreeNode> toExpand = new ArrayList<>();

		for (StackFrame frame : stackFrames) {
			DefaultMutableTreeNode methodNode = new DefaultMutableTreeNode(frame.location().method().name());
			root.add(methodNode);

			if (toExpand.size() < 3) {
				toExpand.add(methodNode);
			}

			try {
				List<LocalVariable> variables = frame.visibleVariables();
				for (LocalVariable var : variables) {
					Value value = frame.getValue(var);
					DefaultMutableTreeNode varNode = new DefaultMutableTreeNode(
							"<html>" + var.name() + " = " + StringUtils.abbreviateString(getValueString(value), 100)
									+ "<br><small>" + var.typeName());

					if (value instanceof ArrayReference arrayReference) {
						int i = 0;
						for (Value v : arrayReference.getValues()) {
							DefaultMutableTreeNode arrayNode = new DefaultMutableTreeNode(
									StringUtils.abbreviateString("[" + i + "] = " + getValueString(v), 100));
							varNode.add(arrayNode);
							i++;
						}
					}

					methodNode.add(varNode);
				}
			} catch (AbsentInformationException ignored) {
			}
		}

		frames.setModel(new DefaultTreeModel(root));
		TreeUtils.setExpansionState(frames, toExpand);

		frames.revalidate();
		frames.repaint();

		layout.show(this, "frames");
	}

	private static String getValueString(Value value) {
		if (value == null) {
			return "null";
		} else if (value instanceof StringReference) {
			return ((StringReference) value).value();
		} else if (value instanceof BooleanValue) {
			return Boolean.toString(((BooleanValue) value).value());
		} else if (value instanceof ByteValue) {
			return Byte.toString(((ByteValue) value).value());
		} else if (value instanceof CharValue) {
			return Character.toString(((CharValue) value).value());
		} else if (value instanceof ShortValue) {
			return Short.toString(((ShortValue) value).value());
		} else if (value instanceof IntegerValue) {
			return Integer.toString(((IntegerValue) value).value());
		} else if (value instanceof LongValue) {
			return Long.toString(((LongValue) value).value());
		} else if (value instanceof FloatValue) {
			return Float.toString(((FloatValue) value).value());
		} else if (value instanceof DoubleValue) {
			return Double.toString(((DoubleValue) value).value());
		} else if (value instanceof ArrayReference arrayReference) {
			return "[size: " + arrayReference.getValues().size() + "]";
		} else {
			return value.toString();
		}
	}

	private static class FramesCellRenderer extends DefaultTreeCellRenderer {

		@Override
		public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
				boolean leaf, int row, boolean hasFocus) {
			JLabel a = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			String text = value.toString();
			a.setText(text);
			a.setForeground(Theme.current().getForegroundColor());

			if (text.contains("[size:")) {
				a.setIcon(UIRES.get("rsta.template_obj"));
			} else if (text.contains(" = ")) {
				a.setIcon(UIRES.get("rsta.localvariable_obj"));
			} else {
				a.setIcon(UIRES.get("rsta.methpub_obj"));
			}

			return a;
		}

	}

}
