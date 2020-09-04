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

package net.mcreator.ui.component;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;

public class CollapsiblePanel extends JPanel {

	private String title = "";
	private final TitledBorder border;

	public CollapsiblePanel(String text, JComponent content) {
		border = BorderFactory.createTitledBorder(title);
		setBorder(border);
		BorderLayout borderLayout = new BorderLayout();
		setLayout(borderLayout);
		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				toggleVisibility();
			}
		});
		setTitle(text);
		add(content);
		updateBorderTitle();
		setOpaque(false);
	}

	private final ComponentListener contentComponentListener = new ComponentAdapter() {
		@Override public void componentShown(ComponentEvent e) {
			updateBorderTitle();
		}

		@Override public void componentHidden(ComponentEvent e) {
			updateBorderTitle();
		}
	};

	public void setTitle(String title) {
		firePropertyChange("title", this.title, this.title = title);
	}

	@Override public Component add(Component comp) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(comp);
		updateBorderTitle();
		return r;
	}

	@Override public Component add(String name, Component comp) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(name, comp);
		updateBorderTitle();
		return r;
	}

	@Override public Component add(Component comp, int index) {
		comp.addComponentListener(contentComponentListener);
		Component r = super.add(comp, index);
		updateBorderTitle();
		return r;
	}

	@Override public void add(Component comp, Object constraints) {
		comp.addComponentListener(contentComponentListener);
		super.add(comp, constraints);
		updateBorderTitle();
	}

	@Override public void add(Component comp, Object constraints, int index) {
		comp.addComponentListener(contentComponentListener);
		super.add(comp, constraints, index);
		updateBorderTitle();
	}

	@Override public void remove(int index) {
		Component comp = getComponent(index);
		comp.removeComponentListener(contentComponentListener);
		super.remove(index);
	}

	@Override public void remove(Component comp) {
		comp.removeComponentListener(contentComponentListener);
		super.remove(comp);
	}

	@Override public void removeAll() {
		for (Component c : getComponents()) {
			c.removeComponentListener(contentComponentListener);
		}
		super.removeAll();
	}

	public void toggleVisibility() {
		toggleVisibility(hasInvisibleComponent());
	}

	public void toggleVisibility(boolean visible) {
		for (Component c : getComponents()) {
			c.setVisible(visible);
		}
		updateBorderTitle();

		if (hasInvisibleComponent()) {
			setPreferredSize(new Dimension(getPreferredSize().width, 24));
		} else {
			setPreferredSize(null);
		}
	}

	protected void updateBorderTitle() {
		String arrow = "";
		if (getComponentCount() > 0)
			arrow = (hasInvisibleComponent() ? "[Click to expand]" : "[Click to collapse]");
		border.setTitle("<html>" + title + " <b>" + arrow);
		repaint();
	}

	protected final boolean hasInvisibleComponent() {
		for (Component c : getComponents()) {
			if (!c.isVisible()) {
				return true;
			}
		}
		return false;
	}

}
