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

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.LinkedHashMap;
import java.util.Map;

public class CollapsibleDockPanel extends JPanel {

	private final JToolBar buttonStrip;
	private final JPanel dockPanel;
	private final CardLayout cardLayout;
	private final JSplitPane splitPane;

	private final ButtonGroup buttonGroup;
	private final Map<AbstractButton, String> buttonToCard = new LinkedHashMap<>();
	private final Map<String, Integer> cardToLastHeight = new LinkedHashMap<>();

	private AbstractButton selectedButton;

	public CollapsibleDockPanel(JComponent mainContent) {
		super(new BorderLayout());
		setOpaque(false);

		cardLayout = new CardLayout();
		dockPanel = new JPanel(cardLayout);

		buttonStrip = new JToolBar();
		buttonStrip.setFloatable(false);
		buttonStrip.setOpaque(false);
		buttonStrip.setVisible(false);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainContent, dockPanel);
		splitPane.setOpaque(false);

		buttonGroup = new ButtonGroup();

		add(splitPane, BorderLayout.CENTER);
		add(buttonStrip, BorderLayout.SOUTH);

		clearSelection(null);

		addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent e) {
				String lastCard = buttonToCard.get(selectedButton);
				if (lastCard != null) {
					splitPane.setDividerLocation(splitPane.getHeight() - cardToLastHeight.get(lastCard));
				} else {
					splitPane.setDividerLocation(splitPane.getHeight());
				}
			}
		});
	}

	public void addDock(String id, int initialHeight, JComponent content) {
		addDock(id, initialHeight, null, null, content);
	}

	public void addDock(String id, int initialHeight, String text, JComponent content) {
		addDock(id, initialHeight, text, null, content);
	}

	public void addDock(String id, int initialHeight, Icon icon, JComponent content) {
		addDock(id, initialHeight, null, icon, content);
	}

	public void addDock(String id, int initialHeight, String text, Icon icon, JComponent content) {
		JToggleButton button = createToggleButton(text, icon);
		addDock(id, initialHeight, button, content);
	}

	public void addDock(String id, int initialHeight, AbstractButton toggleButton, JComponent content) {
		toggleButton.setFocusable(false);

		buttonGroup.add(toggleButton);
		buttonStrip.add(toggleButton);
		buttonStrip.setVisible(true);

		dockPanel.add(content, id);
		buttonToCard.put(toggleButton, id);
		cardToLastHeight.put(id, initialHeight);

		toggleButton.addActionListener(e -> handleToggle(toggleButton));
	}

	public void clearSelection(@Nullable String lastCard) {
		buttonGroup.clearSelection();
		selectedButton = null;

		if (lastCard != null)
			cardToLastHeight.put(lastCard, splitPane.getHeight() - splitPane.getDividerLocation());

		splitPane.setDividerLocation(splitPane.getHeight());
		splitPane.setDividerSize(0);
		revalidate();
	}

	private void handleToggle(AbstractButton button) {
		if (button == selectedButton) {
			clearSelection(buttonToCard.get(selectedButton));
			return;
		}

		selectedButton = button;
		String card = buttonToCard.get(button);

		cardLayout.show(dockPanel, card);

		splitPane.setDividerSize(UIManager.getInt("SplitPane.dividerSize"));
		splitPane.setDividerLocation(splitPane.getHeight() - cardToLastHeight.get(card));
		revalidate();
	}

	private JToggleButton createToggleButton(String text, Icon icon) {
		JToggleButton button = new JToggleButton(text, icon);
		button.setFocusable(false);
		button.setHorizontalTextPosition(SwingConstants.CENTER);
		button.setVerticalTextPosition(SwingConstants.BOTTOM);
		return button;
	}

}