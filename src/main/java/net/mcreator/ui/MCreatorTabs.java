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

package net.mcreator.ui;

import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.util.ListUtils;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MCreatorTabs {

	private final CardLayout cardLayout;
	private final JPanel container;

	private final JPanel tabsStrip;
	private final JLabel moreTabs;
	private JScrollablePopupMenu moreTabsMenu;

	private final List<Tab> tabs;

	private final List<TabShownListener> tabShownListeners = new ArrayList<>();

	private Tab current;
	private Tab previous;

	private final JPanel filler;

	MCreatorTabs() {
		tabs = new ArrayList<>();
		cardLayout = new CardLayout();

		container = new JPanel(cardLayout);
		container.setOpaque(false);

		tabsStrip = new JPanel();
		tabsStrip.setOpaque(false);
		tabsStrip.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tabsStrip.addComponentListener(new ComponentAdapter() {
			@Override public void componentResized(ComponentEvent componentEvent) {
				super.componentResized(componentEvent);
				reloadTabStrip();
			}
		});

		moreTabs = new JLabel(UIRES.get("more"));
		moreTabs.setPreferredSize(new Dimension(40, 39));
		moreTabs.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(0, 0, 0, 1, (Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")),
				BorderFactory.createCompoundBorder(
						BorderFactory.createMatteBorder(0, 0, 5, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")),
						BorderFactory.createEmptyBorder(0, 10, 0, 10))));
		moreTabs.setCursor(new Cursor(Cursor.HAND_CURSOR));
		moreTabs.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				moreTabsMenu.show(e.getComponent(), 0, 39);
			}
		});

		filler = new JPanel();
		filler.setOpaque(false);
		filler.setBorder(
				BorderFactory.createMatteBorder(0, 0, 5, 0, (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")));
	}

	void reloadTabStrip() {
		tabsStrip.removeAll();

		if (moreTabsMenu != null) {
			moreTabsMenu.setVisible(false);
			moreTabsMenu.removeAll();
		}

		moreTabsMenu = new JScrollablePopupMenu();
		moreTabsMenu
				.setBorder(BorderFactory.createMatteBorder(0, 5, 0, 0, (Color) UIManager.get("MCreatorLAF.MAIN_TINT")));
		moreTabsMenu.setMaximumVisibleRows(11);

		int maxWidth = tabsStrip.getWidth();
		int currWidth = 0;
		int tabsStripWidth = 0;

		List<Tab> overflown = new ArrayList<>();

		boolean first = true;

		for (Tab tab : tabs) {
			if (!tab.ghost) {
				currWidth += tab.getPreferredSize().width;
				if (currWidth < maxWidth - 50) {
					tabsStripWidth += tab.getPreferredSize().width;
					tabsStrip.add(tab);
				} else {
					if (first) { // if the first one is already overflown, we add at least this one
						tabsStrip.add(tab);
						tabsStripWidth += tab.getPreferredSize().width;
					} else
						overflown.add(tab);
				}
				first = false;
			}
		}

		if (!overflown.isEmpty()) {
			tabsStrip.add(moreTabs);
			for (Tab tab : overflown) {
				// if the current tab is overflown, we move this tab up on the list
				if (tab.equals(current)) {
					showOverflownTab(tab);
					return;
				}
				tab.setBorder(BorderFactory.createEmptyBorder());
				moreTabsMenu.add(tab);
			}
			tabsStripWidth += moreTabs.getPreferredSize().width;
		}

		filler.setPreferredSize(new Dimension(Math.max(0, maxWidth - tabsStripWidth), 39));
		tabsStrip.add(filler);

		tabsStrip.revalidate();
		tabsStrip.repaint();
	}

	private void showOverflownTab(Tab tab) {
		ListUtils.rearrange(tabs, tab);
		reloadTabStrip();
	}

	public void addTab(final Tab tab) {
		tab.container = this;
		tabs.add(tab);

		tab.addMouseListener(new MouseAdapter() {
			@Override public void mousePressed(MouseEvent mouseEvent) {
				if (mouseEvent.getButton() == MouseEvent.BUTTON2 && !tab.ghost && tab.closeable) {
					closeTab(tab);
				} else {
					showTab(tab);
				}
			}
		});

		container.add(tab.content, tab.identifier.toString());
		showTab(tab);

		reloadTabStrip();
	}

	/**
	 * @param tab Tab to show
	 * @return true, if Tab with this identifier already exists
	 */
	public boolean showTab(Tab tab) {
		return this.showTabOrGetExisting(tab.identifier) != null;
	}

	public void showTabNoNotify(Tab tab) {
		this.showTabOrGetExisting(tab.identifier, false);
	}

	public Tab showTabOrGetExisting(Tab tab) {
		return this.showTabOrGetExisting(tab.identifier);
	}

	public Tab showTabOrGetExisting(Object identifier) {
		return this.showTabOrGetExisting(identifier, true);
	}

	public Tab showTabOrGetExisting(Object identifier, boolean notify) {
		Tab existing = null;
		if (this.current != null && !this.current.equals(this.previous))
			this.previous = this.current;
		for (Tab tab : tabs) {
			if (tab.identifier.equals(identifier)) {
				cardLayout.show(container, identifier.toString());
				tab.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
				tab.selected = true;
				this.current = tab;
				if (notify) {
					tabShownListeners.forEach(l -> l.tabShown(tab));
					if (tab.tabShownListener != null)
						tab.tabShownListener.tabShown(tab);
				}
				existing = tab;
			} else {
				tab.setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
				tab.selected = false;
			}
			tab.updateBorder();
		}
		if (existing != null)
			reloadTabStrip();

		return existing;
	}

	public void closeTab(Tab tab) {
		if (!tab.closeable)
			return;

		if (tab.tabClosingListener == null || tab.tabClosingListener.tabClosing(tab)) {
			this.tabs.remove(tab);

			reloadTabStrip();

			if (tab.equals(this.current))
				if (!showTab(this.previous)) {
					showTab(tabs.get(0));
				}

			if (tab.tabClosedListener != null)
				tab.tabClosedListener.tabClosed(tab);
		}
	}

	public void closeAllTabs() {
		List<Tab> toClose = new ArrayList<>(
				tabs); // make copy so we don't foreach original which is changed during iterations
		toClose.forEach(this::closeTab);
	}

	public Tab getCurrentTab() {
		return current;
	}

	public void addTabShownListener(TabShownListener tabShownListener) {
		this.tabShownListeners.add(tabShownListener);
	}

	JComponent getContainer() {
		return container;
	}

	JComponent getTabsStrip() {
		return tabsStrip;
	}

	public static class Tab extends JPanel {

		private final boolean ghost;
		private final boolean closeable;
		private final boolean uppercase;
		private MCreatorTabs container;

		private final Object identifier;
		private final ImageIcon icon;
		private final JPanel content;

		private TabClosedListener tabClosedListener;
		private TabClosingListener tabClosingListener;
		private TabShownListener tabShownListener;

		private Color inactiveColor = (Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT");
		private Color activeColor = (Color) UIManager.get("MCreatorLAF.MAIN_TINT");

		private boolean selected = false;

		private final JLabel iconLabel = new JLabel();
		private final JLabel blo = new JLabel();
		private final FontRenderContext frc;

		private boolean hasRightBorder = true;

		private String text;

		public Tab(ViewBase content) {
			this(content.getViewName(), content.getViewIcon(), content,
					content.getViewName() + System.currentTimeMillis(), false, true, true);
		}

		public Tab(ViewBase content, Object identifier) {
			this(content.getViewName(), content.getViewIcon(), content, identifier, false, true, true);
		}

		public Tab(ViewBase content, Object identifier, boolean uppercase) {
			this(content.getViewName(), content.getViewIcon(), content, identifier, false, true, uppercase);
		}

		Tab(String name, JPanel content, Object identifier, boolean ghost, boolean closeable) {
			this(name, null, content, identifier, ghost, closeable, true);
		}

		private Tab(String name, ImageIcon icon, JPanel content, Object identifier, boolean ghost, boolean closeable,
				boolean uppercase) {
			super(new BorderLayout(0, 0));
			this.icon = icon;

			this.text = name;

			this.content = content;
			this.identifier = identifier;
			this.ghost = ghost;
			this.closeable = closeable;
			this.uppercase = uppercase;

			blo.setHorizontalAlignment(SwingConstants.CENTER);
			blo.setVerticalAlignment(SwingConstants.CENTER);
			blo.setCursor(new Cursor(Cursor.HAND_CURSOR));
			blo.setBackground(new Color(80, 80, 80));
			blo.setForeground((Color) UIManager.get("MCreatorLAF.BRIGHT_COLOR"));

			setText(name);

			AffineTransform affinetransform = new AffineTransform();
			frc = new FontRenderContext(affinetransform, true, true);

			JLabel close = new JLabel(UIRES.get("close_small"));
			close.setCursor(new Cursor(Cursor.HAND_CURSOR));
			close.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
			close.addMouseListener(new MouseAdapter() {
				@Override public void mouseClicked(MouseEvent mouseEvent) {
					container.closeTab(Tab.this);
				}
			});

			if (closeable && this.icon == null) {
				add(close, "East");
				add(PanelUtils
						.centerAndEastElement(blo, PanelUtils.centerAndEastElement(close, new JEmptyBox(10, 10), 0, 0),
								0, 0), "Center");
			} else if (closeable) {
				setIcon(this.icon);
				iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
				iconLabel.setBorder(BorderFactory.createEmptyBorder(1, 9, 0, 0));
				add(iconLabel, "West");
				add(close, "East");
				add(PanelUtils
						.centerAndEastElement(blo, PanelUtils.centerAndEastElement(close, new JEmptyBox(10, 10), 0, 0),
								0, 0), "Center");
			} else {
				add(blo, "Center");
			}
			updateSize();
			updateBorder();
			setBackground((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"));
		}

		public void updateSize() {
			if (closeable && icon == null) {
				setPreferredSize(
						new Dimension((int) (blo.getFont().getStringBounds(blo.getText(), frc).getWidth() + 40), 39));
			} else if (closeable) {
				setPreferredSize(
						new Dimension((int) (blo.getFont().getStringBounds(blo.getText(), frc).getWidth() + 70), 39));
			} else {
				setPreferredSize(
						new Dimension((int) (blo.getFont().getStringBounds(blo.getText(), frc).getWidth() + 30), 39));
			}
		}

		public void setText(String name) {
			if (uppercase)
				name = name.toUpperCase(Locale.ENGLISH);
			blo.setText(name);
			if (name.length() < 24) {
				ComponentUtils.deriveFont(blo, 15);
			} else {
				ComponentUtils.deriveFont(blo, 12);
			}
			blo.setText(StringUtils.abbreviateString(name, uppercase ? 35 : 42));
		}

		public String getText() {
			return text;
		}

		public void setIcon(ImageIcon icon) {
			if (icon.getIconWidth() > 24 || icon.getIconHeight() > 24)
				icon = new ImageIcon(ImageUtils.resizeAA(icon.getImage(), 24, 24));
			else if (icon.getIconWidth() < 24 || icon.getIconHeight() < 24) {
				icon = ImageUtils.drawOver(new EmptyIcon.ImageIcon(24, 24), icon, 12 - icon.getIconWidth() / 2,
						12 - icon.getIconHeight() / 2, icon.getIconWidth(), icon.getIconHeight());
			}
			iconLabel.setIcon(icon);
		}

		void updateBorder() {
			if (selected)
				setBorder(BorderFactory.createCompoundBorder(BorderFactory
								.createMatteBorder(0, 0, 0, hasRightBorder ? 1 : 0,
										(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")),
						BorderFactory.createMatteBorder(0, 0, 5, 0, activeColor)));
			else
				setBorder(BorderFactory.createCompoundBorder(BorderFactory
								.createMatteBorder(0, 0, 0, hasRightBorder ? 1 : 0,
										(Color) UIManager.get("MCreatorLAF.BLACK_ACCENT")),
						BorderFactory.createMatteBorder(0, 0, 5, 0, inactiveColor)));
		}

		public void setHasRightBorder(boolean hasRightBorder) {
			this.hasRightBorder = hasRightBorder;
		}

		public void setInactiveColor(Color inactiveColor) {
			this.inactiveColor = inactiveColor;
			updateBorder();
		}

		public void setActiveColor(Color activeColor) {
			this.activeColor = activeColor;
			updateBorder();
		}

		public void setTabClosedListener(TabClosedListener tabClosedListener) {
			this.tabClosedListener = tabClosedListener;
		}

		public void setTabClosingListener(TabClosingListener tabClosingListener) {
			this.tabClosingListener = tabClosingListener;
		}

		public void setTabShownListener(TabShownListener tabShownListener) {
			this.tabShownListener = tabShownListener;
		}

		public JPanel getContent() {
			return content;
		}

		@Override public boolean equals(Object o) {
			if (o instanceof Tab)
				return ((Tab) o).identifier.equals(identifier);
			return false;
		}

		@Override public int hashCode() {
			return identifier.hashCode();
		}
	}

	public interface TabShownListener {
		void tabShown(Tab tab);
	}

	public interface TabClosedListener {
		void tabClosed(Tab tab);
	}

	public interface TabClosingListener {
		boolean tabClosing(Tab tab);
	}

}
