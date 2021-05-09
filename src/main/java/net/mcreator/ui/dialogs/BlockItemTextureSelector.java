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
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.imageeditor.NewImageDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.validators.TagsNameValidator;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.WorkspaceFolderManager;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockItemTextureSelector extends MCreatorDialog {

	private static BlockItemTextureSelector instance;
	private final JButton select = L10N.button("dialog.textures_selector.select");
	private final FilterModel model = new FilterModel();
	private final TextureType type;
	private final CardLayout layout = new CardLayout();
	private final JPanel center = new JPanel(layout);

	private final JTextField filterField = new JTextField(20);

	private final MCreator mcreator;
	public JList<File> list = new JList<>(model);

	public BlockItemTextureSelector(MCreator mcreator, TextureType type) {
		super(mcreator);
		this.type = type;
		this.mcreator = mcreator;
		instance = this;

		setModal(true);
		setTitle(L10N.t("dialog.textures_selector.title", type));
		setSize(842, 480);

		Dimension dim = getToolkit().getScreenSize();
		Rectangle abounds = getBounds();
		setLocation((dim.width - abounds.width) / 2, (dim.height - abounds.height) / 2);
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

		select.setFont(select.getFont().deriveFont(16.0f));
		JButton cancel = new JButton(UIManager.getString("OptionPane.cancelButtonText"));
		cancel.setFont(cancel.getFont().deriveFont(16.0f));

		buttons.add(select);
		buttons.add(cancel);

		cancel.addActionListener(event -> setVisible(false));

		ComponentUtils.deriveFont(filterField, 15);
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

		JPanel pno = new JPanel();

		JButton createTx2 = L10N.button("dialog.textures_selector.create_from_scratch");
		createTx2.setFont(select.getFont());
		createTx2.setIcon(UIRES.get("18px.add"));
		createTx2.addActionListener(event -> {
			setVisible(false);
			NewImageDialog newImageDialog = new NewImageDialog(mcreator);
			newImageDialog.setVisible(true);
		});
		pno.add(createTx2);

		JButton importTx = L10N.button("dialog.textures_selector.import", type.name().toLowerCase(Locale.ENGLISH));
		importTx.setFont(select.getFont());
		importTx.setIcon(UIRES.get("18px.add"));
		importTx.addActionListener(event -> {

			TextureImportDialogs.importTexturesBlockOrItem(mcreator, type);
			List<File> block1;
			if (type == TextureType.BLOCK) {
				block1 = mcreator.getFolderManager().getBlockTexturesList();
			} else {
				block1 = mcreator.getFolderManager().getItemTexturesList();
			}
			model.removeAllElements();
			for (File element : block1) {
				if (element.getName().endsWith(".png"))
					model.addElement(element);
			}
			if (model.getSize() > 0) {
				layout.show(center, "list");
			}
		});
		pno.add(importTx);

		JButton importMc = L10N.button("dialog.textures_selector.import_mc", type.name().toLowerCase(Locale.ENGLISH));
		importMc.setFont(select.getFont());
		importMc.setIcon(UIRES.get("18px.add"));
		VComboBox<String> ID = new VComboBox<>();

		ID.setValidator(new TagsNameValidator<>(ID, true));

		ID.addItem("");
		ID.addItem("minecraft:block/cobblestone");
		ID.addItem("minecraft:item/diamond");

		ID.setEditable(true);
		ID.setOpaque(false);
		ID.setForeground(Color.white);
		ComponentUtils.deriveFont(ID, 16);

		ID.enableRealtimeValidation();
		importMc.addActionListener(event -> {

			int result = JOptionPane.showConfirmDialog(this, PanelUtils.northAndCenterElement(
					L10N.label("dialog.textures_selector.enter_id", type.name().toLowerCase(Locale.ENGLISH)), ID),
					L10N.t("dialog.textures_selector.use_id", type.name().toLowerCase(Locale.ENGLISH)),
					JOptionPane.OK_CANCEL_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				if (ID.getValidationStatus().getValidationResultType() != Validator.ValidationResultType.ERROR) {
					String selectedID = ID.getSelectedItem();
					if (selectedID != null) {
						File fileID = new File(selectedID);
						model.addElement(fileID);
						list.setSelectedValue(fileID, true);
						if (model.getSize() > 0) {
							layout.show(center, "list");
						}
					}
				} else {
					JOptionPane.showMessageDialog(this, L10N.t("dialog.textures_selector.error_invalid_id_message"),
							L10N.t("dialog.textures_selector.error_invalid_id_title"), JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		pno.add(importMc);

		pn.add("North", PanelUtils.westAndEastElement(pno, PanelUtils.totalCenterInPanel(pno2)));
		pn.add("South", buttons);

		add(pn);
	}

	public static String fixName(String name, WorkspaceFolderManager workspace) {
		if (name.contains("textures\\blocks\\") || name.contains("textures/blocks/")) {
			return textureNameReplace(name.replace(Objects.requireNonNull(workspace.getBlocksTexturesDir()).getPath(), ""));
		} else if (name.contains("textures\\items\\") || name.contains("textures/items/")) {
			return textureNameReplace(name.replace(Objects.requireNonNull(workspace.getItemsTexturesDir()).getPath(), ""));
		} else if (name.contains("textures\\others\\") || name.contains("textures/others/")) {
			return textureNameReplace(name.replace(Objects.requireNonNull(workspace.getOtherTexturesDir()).getPath(), ""));
		} else {
			return name.replace("\\", "/");
		}
	}

	public static String textureNameReplace(String string) {
		if (string.contains("\\"))
			string = string.replace("\\", "");
		else if (string.contains("//"))
			string = string.replace("//", "");

		return string;
	}

	public TextureType getTextureType() {
		return type;
	}

	@Override public void setVisible(boolean b) {
		List<File> block;
		if (type == TextureType.BLOCK) {
			block = mcreator.getFolderManager().getBlockTexturesList();
		} else {
			block = mcreator.getFolderManager().getItemTexturesList();
		}
		model.removeAllElements();
		for (File element : block) {
			if (element.getName().endsWith(".png"))
				model.addElement(element);
		}
		list.setSelectedIndex(0);
		if (block.size() == 0) {
			layout.show(center, "help");
		} else {
			layout.show(center, "list");
		}

		super.setVisible(b);
	}

	public JButton getConfirmButton() {
		return select;
	}

	public MCreator getMCreator() {
		return mcreator;
	}

	public enum TextureType {
		BLOCK, ITEM
	}

	static class Render extends JLabel implements ListCellRenderer<File> {
		@Override
		public Component getListCellRendererComponent(JList<? extends File> list, File ma, int index,
				boolean isSelected, boolean cellHasFocus) {
			setOpaque(false);
			if (isSelected) {
				setBorder(BorderFactory.createLineBorder(Color.red, 1));
			} else {
				setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
			}
			if (ma != null) {
				setToolTipText(FilenameUtils
						.removeExtension(fixName(ma.toString(), instance.getMCreator().getFolderManager())));
				if (ma.toString().endsWith(".png")) {
					ImageIcon icon = new ImageIcon(ma.toString());
					if (icon.getImage() != null)
						setIcon(new ImageIcon(ImageUtils.resize(icon.getImage(), 32)));
					else
						setIcon(new ImageIcon(ImageUtils.resize(UIRES.get("tag").getImage(), 32)));
				} else {
					setIcon(new ImageIcon(ImageUtils.resize(UIRES.get("tag").getImage(), 32)));
				}
			}
			return this;
		}

	}

	private class FilterModel extends DefaultListModel<File> {
		ArrayList<File> items;
		ArrayList<File> filterItems;

		FilterModel() {
			super();
			items = new ArrayList<>();
			filterItems = new ArrayList<>();
		}

		@Override public File getElementAt(int index) {
			if (index < filterItems.size())
				return filterItems.get(index);
			else
				return null;
		}

		@Override public int getSize() {
			return filterItems.size();
		}

		@Override public void addElement(File o) {
			items.add(o);
			refilter();
		}

		@Override public void removeAllElements() {
			super.removeAllElements();
			items.clear();
			filterItems.clear();
		}

		@Override public boolean removeElement(Object a) {
			if (a instanceof File) {
				items.remove(a);
				filterItems.remove(a);
			}
			return super.removeElement(a);
		}

		private void refilter() {
			filterItems.clear();
			String term = filterField.getText();
			filterItems.addAll(items.stream().filter(item -> item.getName().toLowerCase(Locale.ENGLISH)
					.contains(term.toLowerCase(Locale.ENGLISH))).collect(Collectors.toList()));
			fireContentsChanged(this, 0, getSize());
		}
	}
}
