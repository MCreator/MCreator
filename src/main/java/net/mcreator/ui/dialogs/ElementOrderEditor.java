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

package net.mcreator.ui.dialogs;

import com.formdev.flatlaf.FlatClientProperties;
import net.mcreator.element.GeneratableElement;
import net.mcreator.element.parts.TabEntry;
import net.mcreator.element.types.interfaces.ITabContainedElement;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.MCItem;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.ReordarableListTransferHandler;
import net.mcreator.ui.init.BlockItemIcons;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.renderer.elementlist.SmallIconModListRender;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ElementOrderEditor {

	public static void openElementOrderEditor(MCreator mcreator) {
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

		JButton moveLeft = L10N.button("dialog.element_order.move_tab.left");
		moveLeft.setIcon(UIRES.get("previous"));
		JButton moveRight = L10N.button("dialog.element_order.move_tab.right");
		moveRight.setIcon(UIRES.get("next"));
		JPanel tabMovers = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tabMovers.add(moveLeft);
		tabMovers.add(moveRight);

		JPanel top = new JPanel(new BorderLayout());

		top.add("West", L10N.label("dialog.element_order.instructions"));
		top.add("South", tabMovers);

		mainPanel.add("North", top);

		LinkedHashMap<String, DefaultListModel<ModElement>> tabEditors = new LinkedHashMap<>();
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(BorderFactory.createEmptyBorder());
		tabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		tabs.putClientProperty(FlatClientProperties.TABBED_PANE_TABS_POPUP_POLICY,
				FlatClientProperties.TABBED_PANE_POLICY_NEVER);

		AtomicBoolean tabsMoved = new AtomicBoolean();
		moveLeft.addActionListener(e -> {
			int index = tabs.getSelectedIndex();
			Component tc = tabs.getTabComponentAt(index);
			tabs.insertTab(tabs.getTitleAt(index), tabs.getIconAt(index), tabs.getComponentAt(index),
					tabs.getToolTipTextAt(index), index - 1);
			tabs.setTabComponentAt(index - 1, tc);
			tabs.setSelectedIndex(index - 1);
			tabsMoved.set(true);
		});
		moveRight.addActionListener(e -> {
			int index = tabs.getSelectedIndex();
			Component tc = tabs.getTabComponentAt(index);
			tabs.insertTab(tabs.getTitleAt(index), tabs.getIconAt(index), tabs.getComponentAt(index),
					tabs.getToolTipTextAt(index), index + 2);
			tabs.setTabComponentAt(index + 1, tc);
			tabs.setSelectedIndex(index + 1);
			tabsMoved.set(true);
		});

		// Custom tabs are appended after all the other ones
		AtomicInteger customStart = new AtomicInteger();
		for (ModElement modElement : mcreator.getWorkspace().getModElements()) {
			GeneratableElement generatableElement = modElement.getGeneratableElement();
			if (generatableElement instanceof ITabContainedElement element) {
				if (element.getCreativeTabItems().isEmpty())
					continue;
				for (TabEntry tab : element.getCreativeTabs()) {
					if (tabEditors.get(tab.getUnmappedValue()) == null) {
						DefaultListModel<ModElement> model = new DefaultListModel<>() {
							@Override public void add(int idx, ModElement element) {
								super.add(idx, element);
								element.reinit(mcreator.getWorkspace());
							}
						};
						JList<ModElement> list = new JList<>(model);
						list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
						list.setVisibleRowCount(-1);
						list.setTransferHandler(new ReordarableListTransferHandler());
						list.setDropMode(DropMode.INSERT);
						list.setDragEnabled(true);
						list.setBackground(Theme.current().getAltBackgroundColor());

						list.setCellRenderer(new SmallIconModListRender(false));

						// Pick the right index depending on tab type (custom or vanilla)
						int nextIndex = tab.getUnmappedValue().startsWith("CUSTOM:") ?
								tabs.getTabCount() :
								customStart.getAndIncrement();
						tabs.insertTab(tab.getUnmappedValue(), null, new JScrollPane(list), null, nextIndex);

						Optional<DataListEntry> tabEntry = tab.getDataListEntry();
						if (tabEntry.isPresent()) {
							tabs.setTabComponentAt(nextIndex, new JLabel(tabEntry.get().getReadableName(),
									new ImageIcon(ImageUtils.resizeAA(
											BlockItemIcons.getIconForItem(tabEntry.get().getTexture()).getImage(), 24)),
									JLabel.LEADING));
						} else {
							Icon tabIcon = null;
							if (tab.getUnmappedValue().startsWith("CUSTOM:"))
								tabIcon = new ImageIcon(ImageUtils.resizeAA(
										MCItem.getBlockIconBasedOnName(mcreator.getWorkspace(), tab.getUnmappedValue())
												.getImage(), 24));
							tabs.setTabComponentAt(nextIndex,
									new JLabel(tab.getUnmappedValue(), tabIcon, JLabel.LEADING));
						}

						tabEditors.put(tab.getUnmappedValue(), model);
					}

					// Add ME items here only if the tab items are in does not have order overridden
					if (mcreator.getWorkspace().getCreativeTabsOrder().get(tab.getUnmappedValue()) == null)
						tabEditors.get(tab.getUnmappedValue()).addElement(modElement);
				}
			}
		}

		// Add ME items of tabs with overridden elements order
		int shiftedCustom = customStart.get();
		for (Map.Entry<String, ArrayList<String>> tab : mcreator.getWorkspace().getCreativeTabsOrder().entrySet()) {
			if (!tabEditors.containsKey(tab.getKey()))
				continue;

			for (String element : tab.getValue()) {
				ModElement me = mcreator.getWorkspace().getModElementByName(element);
				if (me != null && me.getGeneratableElement() instanceof ITabContainedElement)
					tabEditors.get(tab.getKey()).addElement(me);
			}

			// Move custom tabs with modified ordering before other custom ones to reflect order of tabs themselves
			int index = tabs.indexOfTab(tab.getKey());
			if (index >= shiftedCustom) {
				Component tc = tabs.getTabComponentAt(index);
				tabs.insertTab(tabs.getTitleAt(index), tabs.getIconAt(index), tabs.getComponentAt(index), null,
						shiftedCustom);
				tabs.setTabComponentAt(shiftedCustom++, tc);
			}
		}

		Map<String, ModElement[]> originalOrder = new HashMap<>();
		for (Map.Entry<String, DefaultListModel<ModElement>> entry : tabEditors.entrySet()) {
			originalOrder.put(entry.getKey(), Collections.list(entry.getValue().elements()).toArray(new ModElement[0]));
		}

		moveLeft.setEnabled(false);
		moveRight.setEnabled(customStart.get() == 0); // If custom tabs start later, initial tab is vanilla/external
		tabs.addChangeListener(e -> {
			int index = tabs.getSelectedIndex();
			moveLeft.setEnabled(index > customStart.get());
			moveRight.setEnabled(index >= customStart.get() && index < tabs.getTabCount() - 1);
		});
		if (tabs.getTabCount() > 0)
			tabs.setSelectedIndex(0);

		mainPanel.add("Center", tabs);
		mainPanel.setPreferredSize(new Dimension(720, 320));

		int resultval = JOptionPane.showOptionDialog(mcreator, mainPanel, L10N.t("dialog.element_order.editor_title"),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[] { "Save layout", UIManager.getString("OptionPane.cancelButtonText") }, "");
		if (resultval == 0) {
			for (Map.Entry<String, DefaultListModel<ModElement>> entry : tabEditors.entrySet()) {
				ModElement[] newOrder = Collections.list(entry.getValue().elements()).toArray(new ModElement[0]);
				if (!Arrays.equals(newOrder, originalOrder.get(entry.getKey()))) {
					mcreator.getWorkspace().getCreativeTabsOrder()
							.setElementOrderInTab(entry.getKey(), Collections.list(entry.getValue().elements()));
				}
			}

			// If order of tabs themselves was changed, re-add them in the same order as in dialog
			if (tabsMoved.get()) {
				for (int i = customStart.get(); i < tabs.getTabCount(); i++) {
					String title = tabs.getTitleAt(i);
					List<ModElement> newOrder = Collections.list(tabEditors.get(title).elements());
					mcreator.getWorkspace().getCreativeTabsOrder().remove(title);
					mcreator.getWorkspace().getCreativeTabsOrder().setElementOrderInTab(title, newOrder);
				}
			}

			mcreator.getWorkspace().markDirty();
		}
	}

}
