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

import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.init.L10N;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollapsiblePanel extends JPanel {

	private String title = "";
	private final TitledBorder border;
	protected final JPanel contentHolder = new JPanel(new BorderLayout());

	public CollapsiblePanel(String text, JComponent content) {
		this(text, content, true);
	}

	public CollapsiblePanel(String text, JComponent content, boolean allowExpandInitially) {
		setLayout(new BorderLayout());
		setOpaque(false);

		setBorder(border = BorderFactory.createTitledBorder(title));
		setTitle(text);
		updateBorderTitle();

		contentHolder.setOpaque(false);
		contentHolder.add(content);
		super.addImpl(contentHolder, null, -1);
		toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault.get() && allowExpandInitially);

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				toggleVisibility();
			}
		});
	}

	public void setTitle(String title) {
		firePropertyChange("title", this.title, this.title = title);
	}

	@Override protected void addImpl(Component comp, Object constraints, int index) {
		contentHolder.add(comp, constraints, index);
	}

	@Override public void remove(int index) {
		contentHolder.remove(index);
	}

	@Override public void remove(Component comp) {
		contentHolder.remove(comp);
	}

	@Override public void removeAll() {
		contentHolder.removeAll();
	}

	public void toggleVisibility() {
		toggleVisibility(!contentHolder.isVisible());
	}

	public void toggleVisibility(boolean visible) {
		contentHolder.setVisible(visible);
		updateBorderTitle();
		setPreferredSize(contentHolder.isVisible() ? null : new Dimension(getPreferredSize().width, 24));
	}

	protected void updateBorderTitle() {
		if (contentHolder.getComponentCount() > 0) {
			String arrow = !contentHolder.isVisible() ?
					"[" + L10N.t("components.collapsible_panel.expand") + "]" :
					"[" + L10N.t("components.collapsible_panel.collapse") + "]";
			border.setTitle("<html>" + title + " <b>" + arrow);
		} else {
			border.setTitle("<html>" + title);
		}
		repaint();
	}
}
