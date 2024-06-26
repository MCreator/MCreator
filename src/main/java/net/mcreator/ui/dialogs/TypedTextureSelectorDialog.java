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

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.FilenameUtilsPatched;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.resources.CustomTexture;
import net.mcreator.workspace.resources.Texture;
import net.mcreator.workspace.resources.VanillaTexture;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Locale;

public class TypedTextureSelectorDialog extends MCreatorDialog {

	private final TextureType type;

	private final JButton select = L10N.button("dialog.textures_selector.select");

	private final FilterModel model = new FilterModel();
	public final JList<Texture> list = new JList<>(model);

	private final CardLayout layout = new CardLayout();
	private final JPanel center = new JPanel(layout);

	private final JTextField filterField = new JTextField(20);

	private final MCreator mcreator;

	private boolean loadVanillaTextures = false;

	public TypedTextureSelectorDialog(MCreator mcreator, TextureType type) {
		super(mcreator);
		this.type = type;
		this.mcreator = mcreator;

		if (type == TextureType.BLOCK || type == TextureType.ITEM) {
			loadVanillaTextures(true);
		}

		setModal(true);
		setTitle(L10N.t("dialog.textures_selector.title", type));
		setSize(842, 480);
		setLocationRelativeTo(mcreator);

		JPanel pn = new JPanel(new BorderLayout());

		list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		list.setVisibleRowCount(0);
		list.setCellRenderer(new Render());

		list.addMouseListener(new MouseAdapter() {
			@Override public void mouseClicked(MouseEvent mouseEvent) {
				super.mouseClicked(mouseEvent);
				if (mouseEvent.getClickCount() == 2)
					select.doClick();
			}
		});

		pn.add("Center", center);

		JLabel aa = L10N.label("dialog.textures_selector.no_texture");

		center.add(PanelUtils.centerInPanel(aa), "help");
		center.add(new JScrollPane(list), "list");

		JPanel buttons = new JPanel();

		JButton cancelButton = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancelButton.addActionListener(event -> setVisible(false));

		buttons.add(select);
		buttons.add(cancelButton);

		filterField.getDocument().addDocumentListener(new DocumentListener() {
			@Override public void removeUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void insertUpdate(DocumentEvent arg0) {
				model.refilter();
			}

			@Override public void changedUpdate(DocumentEvent arg0) {
				model.refilter();
			}
		});

		JPanel pno2 = new JPanel();
		pno2.add(L10N.label("dialog.textures_selector.search"));
		pno2.add(filterField);

		JPanel pno = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 4));

		JButton createTx2 = L10N.button("dialog.textures_selector.create_from_scratch");
		createTx2.setIcon(UIRES.get("18px.add"));
		createTx2.addActionListener(event -> {
			NewImageDialog newImageDialog = new NewImageDialog(mcreator);
			newImageDialog.setVisible(true);
			setVisible(false);
		});
		pno.add(createTx2);

		JButton importTx = L10N.button("dialog.textures_selector.import", type.name().toLowerCase(Locale.ENGLISH));
		importTx.setIcon(UIRES.get("18px.add"));
		importTx.addActionListener(event -> {
			TextureImportDialogs.importMultipleTextures(mcreator, type);
			reloadList();
		});
		pno.add(importTx);

		pn.add("North", PanelUtils.westAndEastElement(pno, PanelUtils.totalCenterInPanel(pno2)));
		pn.add("South", buttons);

		add(pn);
	}

	public TypedTextureSelectorDialog loadVanillaTextures(boolean shouldLoad) {
		this.loadVanillaTextures = shouldLoad;
		return this;
	}

	public TextureType getTextureType() {
		return type;
	}

	@Override public void setVisible(boolean visible) {
		if (visible) {
			reloadList();
		}

		super.setVisible(visible);
	}

	private void reloadList() {
		model.removeAllElements();

		// Load custom textures
		CustomTexture.getTexturesOfType(mcreator.getWorkspace(), type).forEach(model::addElement);

		if (loadVanillaTextures) {
			VanillaTexture.getTexturesOfType(mcreator.getWorkspace(), type).forEach(model::addElement);
		}

		list.setSelectedIndex(0);

		if (model.getSize() == 0) {
			layout.show(center, "help");
		} else {
			layout.show(center, "list");
		}

		list.requestFocus();
	}

	public JButton getConfirmButton() {
		return select;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	private class Render extends JLabel implements ListCellRenderer<Texture> {

		Render() {
			setOpaque(false);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Texture> list, Texture ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			if (isSelected) {
				setBorder(BorderFactory.createLineBorder(Theme.current().getInterfaceAccentColor(), 1));
			} else {
				setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			}

			if (ma != null) {
				setToolTipText(FilenameUtilsPatched.removeExtension(ma.getTextureName()));
				ImageIcon icon = ma.getTextureIcon(mcreator.getWorkspace());
				if (icon.getImage() != null)
					setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
			}

			return this;
		}

	}

	private class FilterModel extends DefaultListModel<Texture> {
		final ArrayList<Texture> items;
		final ArrayList<Texture> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public Texture getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(Texture o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof Texture) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		private void refilter() {
			filterItems.clear();
			String term = filterField.getText();
			filterItems.addAll(items.stream().filter(item -> item.getTextureName().toLowerCase(Locale.ENGLISH)
					.contains(term.toLowerCase(Locale.ENGLISH))).toList());
			fireContentsChanged(this, 0, getSize());
		}

	}

}
