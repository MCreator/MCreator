/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.workspace;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMainWorkspacePanel extends JPanel {

	protected final MCreator mcreator;

	protected final JTabbedPane subTabs;
	protected final Map<String, AbstractWorkspacePanel> sectionTabs = new HashMap<>();
	@Nullable protected AbstractWorkspacePanel currentTabPanel = null;

	public AbstractMainWorkspacePanel(MCreator mcreator, BorderLayout layout) {
		super(layout);
		this.mcreator = mcreator;

		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);

		subTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT) {
			@Override protected void paintComponent(Graphics g) {
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(Theme.current().getAltBackgroundColor());
				g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
				g2d.fillRect(0, 0, getWidth(), getHeight());
				g2d.dispose();
				super.paintComponent(g);
			}
		};
		subTabs.setModel(new DefaultSingleSelectionModel() {
			@Override public void setSelectedIndex(int index) {
				if (subTabs.getComponentAt(index) instanceof AbstractWorkspacePanel tabComponent) {
					if (tabComponent.canSwitchToSection()) {
						currentTabPanel = tabComponent;
					} else { // No permission to view the newly selected tab
						return;
					}
				}

				super.setSelectedIndex(index);
				reloadElementsInCurrentTab();
				afterVerticalTabChanged();
			}
		});

		subTabs.setOpaque(false);
		subTabs.putClientProperty(FlatClientProperties.TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, true);
		subTabs.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ROTATION,
				FlatClientProperties.TABBED_PANE_TAB_ROTATION_AUTO);

		add("Center", subTabs);
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	/**
	 * Adds a new section to this workspace as well as a vertical tab button on the left that switches
	 * to the section panel when clicked.
	 *
	 * @param id      The unique identifier of the section used for reloading/filtering contained elements.
	 * @param name    The name of the section shown in the workspace.
	 * @param section The panel representing contents of the vertical tab being added.
	 */
	public void addVerticalTab(String id, String name, AbstractWorkspacePanel section) {
		if (getVerticalTab(id) != null)
			return;

		sectionTabs.put(id, section);

		if (section.isSupportedInWorkspace()) {
			subTabs.addTab(name, section);
		}
	}

	public AbstractWorkspacePanel getVerticalTab(String id) {
		return sectionTabs.get(id);
	}

	public void switchToVerticalTab(AbstractWorkspacePanel panel) {
		if (panel != null && panel.canSwitchToSection()) {
			// Find the tab to switch to
			for (int i = 0; i < subTabs.getTabCount(); i++) {
				if (subTabs.getComponentAt(i) == panel) {
					subTabs.setSelectedIndex(i);
					break;
				}
			}
		}
	}

	public void switchToVerticalTab(String id) {
		switchToVerticalTab(sectionTabs.get(id));
	}

	protected void afterVerticalTabChanged() {
	}

	public synchronized void reloadElementsInCurrentTab() {
		if (currentTabPanel != null)
			currentTabPanel.reloadElements();
	}

}
