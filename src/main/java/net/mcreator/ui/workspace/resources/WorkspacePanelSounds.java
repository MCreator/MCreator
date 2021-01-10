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

package net.mcreator.ui.workspace.resources;

import net.mcreator.ui.component.JSelectableList;
import net.mcreator.ui.component.TransparentToolBar;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.SoundElementDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.AbstractMCreatorTheme;
import net.mcreator.ui.laf.SlickDarkScrollBarUI;
import net.mcreator.ui.workspace.IReloadableFilterable;
import net.mcreator.ui.workspace.WorkspacePanel;
import net.mcreator.workspace.elements.SoundElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class WorkspacePanelSounds extends JPanel implements IReloadableFilterable {

	private final WorkspacePanel workspacePanel;

	private final FilterModel listmodel = new FilterModel();

	WorkspacePanelSounds(WorkspacePanel workspacePanel) {
		super(new BorderLayout());
		setOpaque(false);

		this.workspacePanel = workspacePanel;

		JSelectableList<SoundElement> soundElementList = new JSelectableList<>(listmodel);
		soundElementList.setOpaque(false);
		soundElementList.setCellRenderer(new Render());
		soundElementList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		soundElementList.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					editSelectedSound(soundElementList.getSelectedValue());
			}
		});

		JScrollPane sp = new JScrollPane(soundElementList);
		sp.setOpaque(false);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getViewport().setOpaque(false);
		sp.getVerticalScrollBar().setUnitIncrement(11);
		sp.getVerticalScrollBar().setUI(new SlickDarkScrollBarUI((Color) UIManager.get("MCreatorLAF.DARK_ACCENT"),
				(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"), sp.getVerticalScrollBar()));
		sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
		sp.setBorder(null);

		add("Center", sp);

		TransparentToolBar bar = new TransparentToolBar();
		bar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 0));

		JButton importsound = L10N.button("action.workspace.resources.import_sound");
		importsound.setIcon(UIRES.get("16px.open.gif"));
		importsound.setContentAreaFilled(false);
		importsound.setOpaque(false);
		ComponentUtils.deriveFont(importsound, 12);
		importsound.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(importsound);

		JButton edit = L10N.button("workspace.sounds.edit_selected");
		edit.setIcon(UIRES.get("16px.edit.gif"));
		edit.setContentAreaFilled(false);
		edit.setOpaque(false);
		ComponentUtils.deriveFont(edit, 12);
		edit.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(edit);

		JButton del = L10N.button("workspace.sounds.delete_selected");
		del.setIcon(UIRES.get("16px.delete.gif"));
		del.setOpaque(false);
		del.setContentAreaFilled(false);
		del.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
		bar.add(del);

		del.addActionListener(actionEvent -> {
			List<SoundElement> file = soundElementList.getSelectedValuesList();
			if (file.size() > 0) {
				int n = JOptionPane.showConfirmDialog(workspacePanel.getMcreator(),
						L10N.t("workspace.sounds.confirm_deletion_message"),
						L10N.t("workspace.sounds.confirm_deletion_title"),
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if (n == 0) {
					file.forEach(workspacePanel.getMcreator().getWorkspace()::removeSoundElement);
					reloadElements();
				}
			}
		});

		edit.addActionListener(e -> editSelectedSound(soundElementList.getSelectedValue()));
		importsound.addActionListener(e -> workspacePanel.getMcreator().actionRegistry.importSound.doAction());
		add("North", bar);

	}

	private void editSelectedSound(SoundElement selectedValue) {
		if (selectedValue != null) {
			SoundElement newElement = SoundElementDialog.soundDialog(workspacePanel.getMcreator(), selectedValue, null);
			workspacePanel.getMcreator().getWorkspace().updateSoundElement(selectedValue, newElement);
			reloadElements();
		}
	}

	@Override public void reloadElements() {
		listmodel.removeAllElements();
		workspacePanel.getMcreator().getWorkspace().getSoundElements().forEach(listmodel::addElement);
		refilterElements();
	}

	@Override public void refilterElements() {
		listmodel.refilter();
	}

	private class FilterModel extends DefaultListModel<SoundElement> {
		List<SoundElement> items;
		List<SoundElement> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public SoundElement getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(SoundElement o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof SoundElement) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		void refilter() {
			filterItems.clear();
			String term = workspacePanel.search.getText();
			filterItems.addAll(items.stream().filter(Objects::nonNull)
					.filter(item -> (item.getName().toLowerCase(Locale.ENGLISH)
							.contains(term.toLowerCase(Locale.ENGLISH)))).collect(Collectors.toList()));

			if (workspacePanel.sortName.isSelected()) {
				filterItems.sort(Comparator.comparing(SoundElement::getName));
			}

			if (workspacePanel.desc.isSelected())
				Collections.reverse(filterItems);

			fireContentsChanged(this, 0, getSize());
		}
	}

	static class Render extends JPanel implements ListCellRenderer<SoundElement> {

		Render() {
			setLayout(new GridLayout());
			setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
		}

		@Override
		public JPanel getListCellRendererComponent(JList<? extends SoundElement> list, SoundElement ma, int index,
				boolean isSelected, boolean cellHasFocus) {

			removeAll();

			JPanel cont = new JPanel(new BorderLayout());
			cont.setBackground(isSelected ?
					((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT")).brighter() :
					(Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
			cont.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

			JPanel namepan = new JPanel(new BorderLayout());
			namepan.setBorder(BorderFactory.createEmptyBorder(0, 8, 5, 0));
			namepan.setOpaque(false);

			JLabel name = new JLabel(ma.getName());
			name.setFont(AbstractMCreatorTheme.light_font.deriveFont(20.0f));
			namepan.add("North", name);

			JLabel name2 = L10N.label("workspace.sounds.files", String.join(", ", ma.getFiles()));
			ComponentUtils.deriveFont(name2, 11);
			namepan.add("South", name2);

			JPanel iconpn = new JPanel(new BorderLayout());
			iconpn.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
			iconpn.setOpaque(false);
			iconpn.add("West", new JLabel(UIRES.get("note")));
			iconpn.add("Center", namepan);

			String rightText;

			if (ma.getSubtitle() != null && !ma.getSubtitle().isEmpty()) {
				rightText = L10N.t("workspace.sounds.subtitle_and_category", ma.getSubtitle(), ma.getCategory());
			} else {
				rightText = L10N.t("workspace.sounds.category", ma.getCategory());
			}

			JLabel rightTextLabel = new JLabel(rightText);
			ComponentUtils.deriveFont(rightTextLabel, 17);
			cont.add("East", rightTextLabel);

			cont.add("West", iconpn);

			add(cont);
			setOpaque(false);

			return this;
		}

	}

}
