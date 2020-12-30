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

import net.mcreator.io.Transliteration;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JScrollablePopupMenu;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.optionpane.OptionPaneValidatior;
import net.mcreator.ui.validation.optionpane.VOptionPane;
import net.mcreator.ui.validation.validators.JavaMemeberNameValidator;
import net.mcreator.workspace.resources.TexturedModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class TextureMappingDialog {

	private Map<String, TexturedModel.TextureMapping> currentState;

	public TextureMappingDialog(Map<String, TexturedModel.TextureMapping> currentState) {
		this.currentState = currentState;
	}

	public Map<String, TexturedModel.TextureMapping> openMappingDialog(MCreator mcreator, Set<String> texturesList,
			boolean supportMultiple) {
		if (texturesList == null && currentState == null)
			return null;

		if (currentState == null) {
			currentState = new HashMap<>();
			Map<String, String> textureMap = new HashMap<>();
			for (String texture : texturesList)
				textureMap.put(texture, "");
			currentState.put("default", new TexturedModel.TextureMapping("default", textureMap));
		} else if (texturesList == null) {
			TexturedModel.TextureMapping textureMapping = currentState.get("default");
			texturesList = textureMapping.getTextureMap().keySet();
		} else {
			return null;
		}

		MCreatorDialog d = new MCreatorDialog(mcreator, L10N.t("dialog.textures_mapping.title_for_model"), true);

		JTabbedPane pane = new JTabbedPane();
		pane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		pane.setForeground(Color.white);

		if (supportMultiple) {
			pane.addTab(L10N.t("dialog.textures_mapping.add_new"), null);
			pane.setTabComponentAt(0, new JLabel(UIRES.get("16px.add.gif")));
			Set<String> finalTexturesList = texturesList;
			pane.addChangeListener(e -> {
				if (pane.getSelectedIndex() == 0) { // new texture mapping
					pane.setSelectedIndex(1);
					String mapping = VOptionPane
							.showInputDialog(mcreator, L10N.t("dialog.textures_mapping.enter_name_message"),
									L10N.t("dialog.textures_mapping.enter_name_title"), null,
									new OptionPaneValidatior() {
										@Override public Validator.ValidationResult validate(JComponent component) {
											return new JavaMemeberNameValidator((VTextField) component, false)
													.validate();
										}
									});
					if (mapping != null) {
						mapping = Transliteration.transliterateString(mapping.toLowerCase(Locale.ENGLISH));
						Map<String, String> textureMap = new HashMap<>();
						for (String texture : finalTexturesList)
							textureMap.put(texture, "");
						currentState.put(mapping, new TexturedModel.TextureMapping(mapping, textureMap));
						addMappingPanel(mcreator, mapping, pane);
					}
				}
			});
		}

		d.add("Center", pane);

		if (currentState.keySet().size() > 5) {
			JButton jumpto = L10N.button("dialog.textures_mapping.jump_to");
			jumpto.setMargin(new Insets(2, 5, 2, 5));
			jumpto.addActionListener(e -> {
				JScrollablePopupMenu popupMenu = new JScrollablePopupMenu();
				for (int i = 0; i < pane.getTabCount(); i++) {
					JMenuItem menuItem = new JMenuItem(pane.getTitleAt(i));
					int finalI = i;
					menuItem.addActionListener(e1 -> pane.setSelectedIndex(finalI));
					popupMenu.add(menuItem);
				}
				popupMenu.show(jumpto, 0, jumpto.getHeight());
			});
			d.add("North", PanelUtils.join(FlowLayout.LEFT, jumpto));
		}

		for (Map.Entry<String, TexturedModel.TextureMapping> entry : currentState.entrySet())
			addMappingPanel(mcreator, entry.getKey(), pane);

		if (supportMultiple)
			pane.setSelectedIndex(1);

		JButton ok = L10N.button("dialog.textures_mapping.save");
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));

		d.add("South", PanelUtils.join(FlowLayout.CENTER, ok, cancel));

		ok.addActionListener(e -> d.setVisible(false));
		cancel.addActionListener(e -> {
			currentState = null;
			d.setVisible(false);
		});
		d.addWindowListener(new WindowAdapter() {
			@Override public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				currentState = null;
				d.setVisible(false);
			}
		});

		d.setSize(500, 500);
		d.setLocationRelativeTo(mcreator);
		d.setVisible(true);

		return currentState;
	}

	private void addMappingPanel(MCreator mcreator, String currentMappingName, JTabbedPane addTo) {
		TexturedModel.TextureMapping textureMapping = currentState.get(currentMappingName);

		Set<Map.Entry<String, String>> entries = textureMapping.getTextureMap().entrySet();

		JPanel panel = new JPanel(new GridLayout(entries.size(), 2, 100, 10));

		TextureHolder[] tx = new TextureHolder[entries.size()];
		int idx = 0;
		for (Map.Entry<String, String> s : entries) {
			panel.add(L10N.label("dialog.textures_mapping.model_texture_part", s.getKey()));
			tx[idx] = new TextureHolder(
					new BlockItemTextureSelector(mcreator, BlockItemTextureSelector.TextureType.BLOCK));
			if (s.getValue() != null && !s.getValue().equals(""))
				tx[idx].setTextureFromTextureName(s.getValue());
			panel.add(PanelUtils.join(tx[idx]));
			int finalIdx = idx;
			tx[idx].setActionListener(
					e -> currentState.get(currentMappingName).getTextureMap().put(s.getKey(), tx[finalIdx].getID()));
			idx++;
		}

		JPanel tab = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
		tab.setOpaque(false);
		JButton button = new JButton(UIRES.get("16px.delete.gif"));
		button.setContentAreaFilled(false);
		button.setBorder(BorderFactory.createEmptyBorder());
		button.setMargin(new Insets(0, 0, 0, 0));
		button.addActionListener(e -> {
			Object[] options = { "Yes", "No" };
			int n = JOptionPane.showOptionDialog(mcreator, L10N.t("dialog.textures_mapping.confirm_deletion_message"),
					L10N.t("dialog.textures_mapping.confirm_deletion_title"), JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			if (n == 0) {
				for (int i = 0; i < addTo.getTabCount(); i++) {
					String title = addTo.getTitleAt(i);
					if (title.equals(currentMappingName)) {
						addTo.remove(i);
						currentState.remove(currentMappingName);
						break;
					}
				}
			}
		});
		button.setEnabled(!currentMappingName.equals("default"));

		JLabel label = new JLabel(currentMappingName);
		ComponentUtils.deriveFont(label, 12);
		label.setBorder(BorderFactory.createEmptyBorder());
		tab.add(label);
		tab.add(button);
		int id = addTo.getTabCount();
		addTo.addTab(currentMappingName, new JScrollPane(PanelUtils.centerInPanel(panel)));
		addTo.setTabComponentAt(id, tab);

	}

}
