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
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.search.ITextFieldSearchable;
import net.mcreator.ui.workspace.localhistory.HistoryPopup;
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

	protected final TransparentToolBar workspaceRightBar = new TransparentToolBar();

	private final CardLayout rightBarCardLayout = new CardLayout();
	private final JPanel rightBarCards = new JPanel(rightBarCardLayout);

	private final Map<AbstractWorkspacePanel, JToolBar> sectionToolbars = new HashMap<>();

	protected final JLabel elementsCount = new JLabel();

	// TODO: correct icon
	private JButton history = new JButton(UIRES.get("16px.json"));

	public AbstractMainWorkspacePanel(MCreator mcreator, BorderLayout layout) {
		super(layout);
		this.mcreator = mcreator;

		setBorder(BorderFactory.createEmptyBorder());
		setOpaque(false);

		search = new JTextField(34) {
			@Override public void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getText().isEmpty()) {
					Graphics2D g2 = (Graphics2D) g;
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setFont(g.getFont().deriveFont(11f));
					g.setColor(Theme.current().getAltForegroundColor());
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

		JPanel se = new JPanel(new BorderLayout());

		JPanel leftPan = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		leftPan.setOpaque(false);
		leftPan.add(search);

		workspaceRightBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 3));

		elementsCount.setHorizontalTextPosition(SwingConstants.LEFT);

		rightBarCards.setOpaque(false);
		rightBarCards.add(new JEmptyBox(), "none");
		workspaceRightBar.add(rightBarCards);

		workspaceRightBar.add(new JEmptyBox(7, 1));
		workspaceRightBar.add(ComponentUtils.deriveFont(elementsCount, 12));
		workspaceRightBar.add(new JEmptyBox(5, 1));
		workspaceRightBar.add(history);
		workspaceRightBar.add(new JEmptyBox(5, 1));

		history.addActionListener(_ -> HistoryPopup.showHistoryPopup(mcreator, history, 16 + 5, history.getHeight() + 5));

		se.setOpaque(false);

		se.add("West", leftPan);
		se.add("East", workspaceRightBar);

		add("North", se);

		search.setToolTipText(L10N.t("workspace.elements.list.search.tooltip"));

		ComponentUtils.deriveFont(search, 14);
		search.setOpaque(false);
		search.setBackground(ColorUtils.applyAlpha(search.getBackground(), 150));
		search.putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true);

		subTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT) {
			@Override protected void paintComponent(Graphics g) {
				if (mcreator.hasBackgroundImage()) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setColor(Theme.current().getAltBackgroundColor());
					g2d.setComposite(AlphaComposite.SrcOver.derive(0.45f));
					g2d.fillRect(0, 0, getWidth(), getHeight());
					g2d.dispose();
				}
				super.paintComponent(g);
			}
		};
		subTabs.putClientProperty(FlatClientProperties.TABBED_PANE_HIDE_TAB_AREA_WITH_ONE_TAB, true);
		subTabs.putClientProperty(FlatClientProperties.TABBED_PANE_TAB_ROTATION,
				FlatClientProperties.TABBED_PANE_TAB_ROTATION_AUTO);

		if (mcreator.hasBackgroundImage()) {
			subTabs.setOpaque(false);
			subTabs.setBackground(ColorUtils.applyAlpha(subTabs.getBackground(), 0));
		} else {
			subTabs.setOpaque(true);
		}

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

		add("Center", subTabs);
	}

	private void reloadToolBarComponents() {
		rightBarCardLayout.show(rightBarCards,
				(currentTabPanel != null && sectionToolbars.containsKey(currentTabPanel) ?
						currentTabPanel.getClass().getName() :
						"none"));
		for (Component comp : rightBarCards.getComponents()) {
			if (comp.isVisible()) {
				rightBarCards.setPreferredSize(comp.getPreferredSize());
			}
		}
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

		JToolBar toolbar = section.getToolBarComponent();
		if (toolbar != null) {
			toolbar.setOpaque(false);
			toolbar.setFloatable(false);
			toolbar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

			rightBarCards.add(toolbar, id.getName());
			sectionToolbars.put(section, toolbar);
		}

		if (section.isSupportedInWorkspace()) {
			subTabs.addTab(name, section);
		}
	}

	@SuppressWarnings("unchecked") public <T extends AbstractWorkspacePanel> T getVerticalTab(Class<T> id) {
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

	public <T extends AbstractWorkspacePanel> void switchToVerticalTab(Class<T> id) {
		switchToVerticalTab(sectionTabs.get(id));
	}

	protected void afterVerticalTabChanged() {
	}

	public synchronized final void reloadWorkspaceTab() {
		reloadToolBarComponents();

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
