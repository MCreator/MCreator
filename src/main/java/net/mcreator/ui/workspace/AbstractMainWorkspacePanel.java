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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.search.ITextFieldSearchable;
import net.mcreator.util.ColorUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractMainWorkspacePanel extends JPanel implements ITextFieldSearchable {

	protected final MCreator mcreator;

	protected final JTextField search;

	protected final JTabbedPane subTabs;
	protected final Map<Class<? extends AbstractWorkspacePanel>, AbstractWorkspacePanel> sectionTabs = new HashMap<>();
	@Nullable protected AbstractWorkspacePanel currentTabPanel = null;

	public AbstractMainWorkspacePanel(MCreator mcreator, BorderLayout layout) {
		super(layout);
		this.mcreator = mcreator;

		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);

		search = new JTextField(34) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				if (getText().isEmpty()) {
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(new Color(120, 120, 120));
					g.drawString(getSearchPlaceholderText(), 8, 19);
				}
			}
		};
		search.addFocusListener(new FocusAdapter() {
			@Override public void focusGained(FocusEvent e) {
				super.focusGained(e);
				if (e.getCause() == FocusEvent.Cause.MOUSE_EVENT) {
					search.setText(null);
				}
			}
		});
		search.getDocument().addDocumentListener(new DocumentListener() {

			@Override public void removeUpdate(DocumentEvent arg0) {
				refilterWorkspaceTab();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				refilterWorkspaceTab();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				refilterWorkspaceTab();
			}

		});

		search.setToolTipText(L10N.t("workspace.elements.list.search.tooltip"));

		ComponentUtils.deriveFont(search, 14);
		search.setOpaque(false);
		search.setBackground(ColorUtils.applyAlpha(search.getBackground(), 150));
		search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

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
				reloadWorkspaceTab();
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

	public String getSearchTerm() {
		return search.getText();
	}

	public void setSearchTerm(String term) {
		search.setText(term);
	}

	protected String getSearchPlaceholderText() {
		return L10N.t("workspace.elements.list.search_list");
	}

	/**
	 * Adds a new section to this workspace as well as a vertical tab button on the left that switches
	 * to the section panel when clicked.
	 *
	 * @param name    The name of the section shown in the workspace.
	 * @param section The panel representing contents of the vertical tab being added.
	 */
	public void addVerticalTab(String name, AbstractWorkspacePanel section) {
		Class<? extends AbstractWorkspacePanel> id = section.getClass();

		if (getVerticalTab(id) != null)
			return;

		sectionTabs.put(id, section);

		if (section.isSupportedInWorkspace()) {
			subTabs.addTab(name, section);
		}
	}

	public <T extends AbstractWorkspacePanel> T getVerticalTab(Class<T> id) {
		//noinspection unchecked
		return (T) sectionTabs.get(id);
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

	public synchronized void reloadWorkspaceTab() {
		if (currentTabPanel != null)
			currentTabPanel.reloadElements();
	}

	public synchronized void refilterWorkspaceTab() {
		sectionTabs.values().forEach(IReloadableFilterable::refilterElements);
	}

	@Override public JTextComponent getSearchTextField() {
		return search;
	}

}
