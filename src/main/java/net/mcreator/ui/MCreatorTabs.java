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

import com.formdev.flatlaf.ui.FlatTabbedPaneUI;
import net.mcreator.plugin.MCREvent;
import net.mcreator.plugin.events.ui.TabEvent;
import net.mcreator.ui.views.ViewBase;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.IconUtils;
import net.mcreator.util.image.ImageUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.IntConsumer;

public class MCreatorTabs extends JTabbedPane {

	private final List<TabShownListener> tabShownListeners = new CopyOnWriteArrayList<>();

	private final List<Tab> tabs = new ArrayList<>();
	private Tab current;
	private Tab previous;

	MCreatorTabs() {
		putClientProperty("JTabbedPane.tabCloseCallback", (IntConsumer) tabIndex -> {
			Tab toClose = getTabForTabIndex(tabIndex);
			if (toClose != null) {
				closeTab(toClose);
			}
		});

		setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		setFocusable(true);

		addChangeListener(e -> {
			int selectedIndex = getSelectedIndex();
			if (selectedIndex >= 0) {
				Tab toNotifyOfShow = getTabForTabIndex(selectedIndex);
				if (toNotifyOfShow != null) {
					showTabOrGetExisting(toNotifyOfShow.identifier, true, false);
				}
			}

			SwingUtilities.invokeLater(this::requestFocusInWindow);
		});

		addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				int tabIndex = indexAtLocation(e.getX(), e.getY());
				if (tabIndex >= 0) {
					Tab clickedTab = getTabForTabIndex(tabIndex);
					if (clickedTab != null) {
						if (SwingUtilities.isMiddleMouseButton(e)) {
							closeTab(clickedTab);
						}

						clickedTab.mouseClicked(e);
					}
				}
			}
		});

		setUI(new FlatTabbedPaneUI() {

			// This is used to paint background behind tab bar
			@Override public void paint(Graphics g, JComponent c) {
				Rectangle tr = null;
				if (getTabLayoutPolicy() == JTabbedPane.SCROLL_TAB_LAYOUT) {
					tr = tabViewport.getBounds();
					for (Component child : tabPane.getComponents()) {
						if (child instanceof FlatTabAreaButton && child.isVisible())
							tr = tr.union(child.getBounds());
					}
				} else {
					for (Rectangle r : rects)
						tr = (tr != null) ? tr.union(r) : r;
				}

				if (tr != null) {
					g.setColor(tabPane.getBackground());
					g.fillRect(0, tr.y, getWidth(), tr.height);
				}

				super.paint(g, c);
			}

		});
	}

	private Tab getTabForTabIndex(int index) {
		for (Tab tab : tabs) {
			if (tab.getIndex() == index) {
				return tab;
			}
		}
		return null;
	}

	public void addTab(final Tab tab) {
		tabs.add(tab);
		tab.addImpl(this);

		MCREvent.event(new TabEvent.Added(tab));

		showTab(tab);
	}

	/**
	 * @param tab Tab to show
	 * @return true, if Tab with this identifier already exists
	 */
	public boolean showTab(Tab tab) {
		return this.showTabOrGetExisting(tab.identifier) != null;
	}

	public void showTabNoNotify(Tab tab) {
		this.showTabOrGetExisting(tab.identifier, false, true);
	}

	public Tab showTabOrGetExisting(Tab tab) {
		return this.showTabOrGetExisting(tab.identifier);
	}

	public Tab showTabOrGetExisting(Object identifier) {
		return this.showTabOrGetExisting(identifier, true, true);
	}

	private Tab showTabOrGetExisting(Object identifier, boolean notify, boolean performUIAction) {
		if (this.current != null && !this.current.equals(this.previous)) {
			this.previous = this.current;
			if (this.previous.tabHiddenListener != null)
				this.previous.tabHiddenListener.tabHidden(this.previous);
		}

		Tab existing = null;
		for (Tab tab : tabs) {
			if (tab.identifier.equals(identifier) || tab.identifier.toString().toLowerCase(Locale.ROOT)
					.equals(identifier.toString().toLowerCase(Locale.ROOT))) {
				SwingUtilities.invokeLater(() -> {
					if (performUIAction) {
						setSelectedIndex(tab.getIndex());
					}

					if (notify) {
						tabShownListeners.forEach(l -> l.tabShown(tab));
						if (tab.tabShownListener != null)
							tab.tabShownListener.tabShown(tab);
					}
				});

				this.current = tab;
				existing = tab;

				MCREvent.event(new TabEvent.Shown(tab));

				break;
			}
		}
		return existing;
	}

	public void closeTab(Tab tab) {
		if (!tab.closeable)
			return;

		if (tab.tabClosingListener == null || tab.tabClosingListener.tabClosing(tab)) {
			MCREvent.event(new TabEvent.Closed(tab));

			SwingUtilities.invokeLater(() -> removeTabAt(tab.getIndex()));
			this.tabs.remove(tab);

			if (tab.equals(this.current)) {
				if (!showTab(this.previous)) {
					showTab(tabs.getFirst());
				}
			}

			if (tab.tabClosedListener != null) {
				tab.tabClosedListener.tabClosed(tab);
			}
		}
	}

	public void closeAllTabs() {
		// make copy so we don't foreach the original which is changed during iterations
		List<Tab> toClose = new ArrayList<>(tabs);
		toClose.forEach(this::closeTab);
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	public Tab getCurrentTab() {
		return current;
	}

	public void addTabShownListener(TabShownListener tabShownListener) {
		this.tabShownListeners.add(tabShownListener);
	}

	public static class Tab {

		private static final int ICON_SIZE = 16;

		private MCreatorTabs container;

		private final boolean closeable;

		private final Object identifier;
		private final JComponent content;

		private TabClosedListener tabClosedListener;
		private TabClosingListener tabClosingListener;
		private TabShownListener tabShownListener;
		private TabHiddenListener tabHiddenListener;
		private MouseListener clickListener;

		private String text;
		@Nullable private ImageIcon icon;

		public Tab(ViewBase content) {
			this(content.getViewName(), content.getViewIcon(), content,
					content.getViewName() + System.currentTimeMillis(), true);
		}

		public Tab(ViewBase content, Object identifier) {
			this(content.getViewName(), content.getViewIcon(), content, identifier, true);
		}

		public Tab(String name, JPanel content, Object identifier, boolean closeable) {
			this(name, null, content, identifier, closeable);
		}

		public Tab(String text, @Nullable ImageIcon icon, JComponent content, Object identifier, boolean closeable) {
			this.content = content;
			this.identifier = identifier;
			this.closeable = closeable;

			this.text = text;
			this.icon = icon;

			if (closeable) {
				content.putClientProperty("JTabbedPane.tabClosable", true);
			}
		}

		private void addImpl(MCreatorTabs container) {
			if (this.container != null)
				return; // Tab already added to a container

			this.container = container;

			container.addTab(text, icon, content);

			setText(text);
			setIcon(icon);
		}

		private int getIndex() {
			return container.indexOfComponent(content);
		}

		private void mouseClicked(MouseEvent e) {
			if (clickListener != null)
				clickListener.mouseClicked(e);
		}

		public String getText() {
			return text;
		}

		public void setText(String name) {
			this.text = name;
			if (container != null)
				container.setTitleAt(getIndex(), name);
		}

		public void setIcon(@Nullable ImageIcon icon) {
			if (icon != null) {
				if (icon.getIconWidth() > ICON_SIZE || icon.getIconHeight() > ICON_SIZE)
					icon = IconUtils.resize(icon, ICON_SIZE, ICON_SIZE);
				else if (icon.getIconWidth() < ICON_SIZE || icon.getIconHeight() < ICON_SIZE) {
					icon = ImageUtils.drawOver(new EmptyIcon.ImageIcon(ICON_SIZE, ICON_SIZE), icon,
							(int) (ICON_SIZE / 2.0 - icon.getIconWidth() / 2.0),
							(int) (ICON_SIZE / 2.0 - icon.getIconHeight() / 2.0), icon.getIconWidth(),
							icon.getIconHeight());
				}
			}

			this.icon = icon;
			if (container != null)
				container.setIconAt(getIndex(), icon);
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

		public void setTabHiddenListener(TabHiddenListener tabHiddenListener) {
			this.tabHiddenListener = tabHiddenListener;
		}

		public JComponent getContent() {
			return content;
		}

		public TabClosedListener getTabClosedListener() {
			return tabClosedListener;
		}

		@Override public boolean equals(Object o) {
			if (o instanceof Tab)
				return ((Tab) o).identifier.equals(identifier);
			return false;
		}

		@Override public int hashCode() {
			return identifier.hashCode();
		}

		@Override public String toString() {
			return text + " (" + identifier + ")";
		}

	}

	public interface TabShownListener {
		void tabShown(Tab tab);
	}

	public interface TabHiddenListener {
		void tabHidden(Tab tab);
	}

	public interface TabClosedListener {
		void tabClosed(Tab tab);
	}

	public interface TabClosingListener {
		boolean tabClosing(Tab tab);
	}

}
