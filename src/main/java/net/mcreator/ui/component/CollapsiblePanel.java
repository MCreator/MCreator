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
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CollapsiblePanel extends JPanel {

	private String title = "";

	private TitledBorder border;
	private boolean smallArrows = false;

	protected final JPanel contentHolder = new JPanel(new BorderLayout());

	public CollapsiblePanel(String text, JComponent content) {
		this(text, content, true);
	}

	public CollapsiblePanel(String text, JComponent content, boolean allowExpandInitially) {
		setLayout(new BorderLayout());
		setOpaque(false);

		this.setBorder(BorderFactory.createMatteBorder(4, 1, 1, 1, Theme.current().getAltBackgroundColor()));

		setTitle(text);
		updateBorderTitle();

		contentHolder.setOpaque(false);
		contentHolder.add(content);
		super.addImpl(contentHolder, null, -1);
		toggleVisibility(PreferencesManager.PREFERENCES.ui.expandSectionsByDefault.get() && allowExpandInitially);

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				FontMetrics fm = getFontMetrics(border.getTitleFont() != null ? border.getTitleFont() : getFont());

				// Title height + some offset
				int titleHeight = fm.getHeight() + 4;

				int y = e.getY();
				Insets insets = getInsets();
				if (y >= insets.top - titleHeight && y <= insets.top) {
					toggleVisibility();
				}
			}
		});
	}

	@Override public void setBorder(Border border) {
		super.setBorder(this.border = BorderFactory.createTitledBorder(border, title, TitledBorder.LEADING,
				TitledBorder.DEFAULT_POSITION, null, Theme.current().getAltForegroundColor()));
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

	public void setSmallArrows(boolean smallArrows) {
		this.smallArrows = smallArrows;
		updateBorderTitle();
	}

	protected void updateBorderTitle() {
		if (contentHolder.getComponentCount() > 0) {
			if (smallArrows) {
				String arrow = !contentHolder.isVisible() ? "▶ " : "▼ ";
				border.setTitle(arrow + title);
			} else {
				String arrow = !contentHolder.isVisible() ?
						"[" + L10N.t("components.collapsible_panel.expand") + "]" :
						"[" + L10N.t("components.collapsible_panel.collapse") + "]";
				border.setTitle("<html>" + title + " <b>" + arrow);
			}
		} else {
			border.setTitle(title);
		}
		repaint();
	}

}
