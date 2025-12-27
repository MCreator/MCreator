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

package net.mcreator.ui.component;

import net.mcreator.ui.laf.OpaqueFlatSplitPaneUI;
import net.mcreator.ui.laf.themes.Theme;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollapsibleDockPanel extends JSplitPane {

	private final CardLayout cardLayout = new CardLayout();
	private final JPanel dockPanel = new JPanel(cardLayout);

	private final ButtonGroup buttonGroup;
	private final Map<AbstractButton, String> buttonToID = new LinkedHashMap<>();
	private final Map<String, AbstractButton> idToButton = new LinkedHashMap<>();
	private final Map<String, Integer> idToLastSize = new LinkedHashMap<>();
	private final Map<String, JComponent> idToContent = new LinkedHashMap<>();

	private final DockPosition dockPosition;

	@Nullable private String currentDockID = null;

	private final JToolBar dockStrip = new JToolBar(JToolBar.VERTICAL);

	public CollapsibleDockPanel(DockPosition dockPosition, JComponent mainContent) {
		super((dockPosition == DockPosition.UP || dockPosition == DockPosition.DOWN) ?
				JSplitPane.VERTICAL_SPLIT :
				JSplitPane.HORIZONTAL_SPLIT);
		this.dockPosition = dockPosition;

		mainContent.setMinimumSize(new Dimension(25, 25));

		dockStrip.setFloatable(false);
		dockStrip.setOpaque(false);
		dockStrip.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 1));
		dockStrip.putClientProperty("FlatLaf.style", "hoverButtonGroupBackground: #00000000");

		putClientProperty("FlatLaf.style", "gripDotCount: 0");

		if (dockPosition == DockPosition.UP || dockPosition == DockPosition.LEFT) {
			setLeftComponent(dockPanel);
			setRightComponent(mainContent);
		} else {
			setLeftComponent(mainContent);
			setRightComponent(dockPanel);
		}

		setOpaque(false);
		setResizeWeight(0);

		OpaqueFlatSplitPaneUI ui = new OpaqueFlatSplitPaneUI();
		ui.setDividerColor(Theme.current().getAltBackgroundColor());
		setUI(ui);

		buttonGroup = new ButtonGroup();

		if (dockPosition == DockPosition.DOWN || dockPosition == DockPosition.RIGHT) {
			addComponentListener(new ComponentAdapter() {
				@Override public void componentResized(ComponentEvent e) {
					if (currentDockID != null) {
						setDividerLocation(getTotalSize() - idToLastSize.get(currentDockID));
					} else {
						setDividerLocation(getTotalSize());
					}
				}
			});
		}

		addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, e -> {
			if (currentDockID != null) {
				idToLastSize.put(currentDockID, getCurrentDockSize());
			}
		});

		clearSelection();
	}

	public AbstractButton addDock(String id, int initialSize, String text, Icon icon, JComponent content) {
		JToggleButton button = new JToggleButton(icon);
		button.setToolTipText(text);
		return addDock(id, initialSize, button, content);
	}

	public AbstractButton addDock(String id, int initialSize, AbstractButton toggleButton, JComponent content) {
		buttonGroup.add(toggleButton);

		dockPanel.add(content, id);
		buttonToID.put(toggleButton, id);
		idToButton.put(id, toggleButton);
		idToLastSize.put(id, initialSize);
		idToContent.put(id, content);

		toggleButton.putClientProperty("FlatLaf.style", "arc: 4");
		toggleButton.setMargin(new Insets(4, 4, 4, 4));

		content.setMinimumSize(new Dimension(0, 0));

		toggleButton.addActionListener(e -> handleToggle(toggleButton));

		dockStrip.add(toggleButton);

		return toggleButton;
	}

	/**
	 * Handles showing of the dock and toggling the right button. Also considers initial width.
	 *
	 * @param id Dock to show
	 */
	public void setDockVisibility(String id, boolean visible) {
		if (idToButton.containsKey(id)) {
			AbstractButton button = idToButton.get(id);
			if (button.isEnabled()) {
				boolean currentState = button.isSelected();
				if (currentState != visible) {
					button.setSelected(visible);
					handleToggle(button);
				}
			}
		}
	}

	public void setToggleEnabled(String id, boolean enabled) {
		if (idToButton.containsKey(id)) {
			idToButton.get(id).setEnabled(enabled);

			if (idToButton.get(id).isSelected()) {
				setDockVisibility(id, enabled);
			}
		}
	}

	public void clearSelection() {
		buttonGroup.clearSelection();

		if (currentDockID != null) {
			idToLastSize.put(currentDockID, getCurrentDockSize());
			idToContent.get(currentDockID).setMinimumSize(new Dimension(0, 0));
		}

		currentDockID = null;

		setDividerLocation(getCollapsedLocation());
		setDividerSize(0);
		revalidate();
	}

	private void handleToggle(AbstractButton button) {
		String affectedDockID = buttonToID.get(button);

		if (currentDockID != null && button == idToButton.get(currentDockID)) {
			clearSelection();
			return;
		}

		if (button.isSelected()) {
			currentDockID = affectedDockID;

			cardLayout.show(dockPanel, affectedDockID);
			setDividerSize(2);
			setDividerLocation(getExpandedLocation(affectedDockID));

			if (dockPosition == DockPosition.UP || dockPosition == DockPosition.DOWN) {
				idToContent.get(affectedDockID).setMinimumSize(new Dimension(0, 25));
			} else {
				idToContent.get(affectedDockID).setMinimumSize(new Dimension(25, 0));
			}
		} else {
			idToLastSize.put(affectedDockID, getCurrentDockSize());
			idToContent.get(affectedDockID).setMinimumSize(new Dimension(0, 0));
		}

		revalidate();
	}

	private int getExpandedLocation(String id) {
		int requestedSize = idToLastSize.getOrDefault(id, 300);
		int totalAvailable = getTotalSize();

		if (dockPosition == DockPosition.UP || dockPosition == DockPosition.LEFT) {
			return requestedSize;
		}

		return Math.max(0, totalAvailable - requestedSize);
	}

	private int getCollapsedLocation() {
		if (dockPosition == DockPosition.UP || dockPosition == DockPosition.LEFT)
			return 0;

		return getTotalSize();
	}

	private int getCurrentDockSize() {
		if (dockPosition == DockPosition.UP || dockPosition == DockPosition.LEFT)
			return getDividerLocation();
		return getTotalSize() - getDividerLocation();
	}

	private int getTotalSize() {
		return getOrientation() == VERTICAL_SPLIT ? getHeight() : getWidth();
	}

	public JToolBar getDockStrip() {
		return dockStrip;
	}

	public enum DockPosition {
		UP, DOWN, LEFT, RIGHT
	}

	public record State(String expandedDock, Map<String, Integer> dockSizes) {

		public static State get(CollapsibleDockPanel collapsibleDockPanel) {
			return new State(collapsibleDockPanel.currentDockID, new HashMap<>(collapsibleDockPanel.idToLastSize));
		}

		public static void apply(@Nullable State state, CollapsibleDockPanel collapsibleDockPanel) {
			if (state == null)
				return;

			collapsibleDockPanel.idToLastSize.putAll(state.dockSizes);
			if (state.expandedDock != null) {
				collapsibleDockPanel.setDockVisibility(state.expandedDock, true);
			}
		}

	}

}