/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2024, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft;

import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.SearchableComboBox;
import net.mcreator.ui.component.util.ComboBoxUtil;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.TextureImportDialogs;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.ListUtils;
import net.mcreator.util.image.EmptyIcon;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.resources.Texture;
import org.apache.commons.io.FilenameUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class TextureComboBox extends JPanel implements IValidable {

	private final Texture empty;

	private final MCreator mcreator;

	private final TextureType textureType;
	private final boolean showEmpty;
	private final String defaultTextureName;
	private boolean addPNGExtension = true;

	private final SearchableComboBox<Texture> comboBox = new SearchableComboBox<>();

	public TextureComboBox(MCreator mcreator, TextureType textureType) {
		this(mcreator, textureType, true);
	}

	public TextureComboBox(MCreator mcreator, TextureType textureType, boolean showEmpty) {
		this(mcreator, textureType, showEmpty, "");
	}

	public TextureComboBox(MCreator mcreator, TextureType textureType, boolean showEmpty, String defaultTextureName) {
		super(new BorderLayout(0, 0));

		this.mcreator = mcreator;
		this.textureType = textureType;
		this.showEmpty = showEmpty;
		this.defaultTextureName = defaultTextureName;

		comboBox.setRenderer(new Renderer());
		comboBox.setPrototypeDisplayValue(new Texture.Dummy(textureType, "XXXXXXXXXXXXXXXXXXXXXXXXX"));
		ComponentUtils.deriveFont(comboBox, 16);

		this.empty = new Texture.Dummy(textureType, defaultTextureName);

		add("Center", comboBox);

		JButton importTexture = new JButton(UIRES.get("18px.add"));
		importTexture.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 1, 1, UIManager.getColor("Component.borderColor")),
				BorderFactory.createEmptyBorder(0, 8, 0, 8)));
		importTexture.setOpaque(false);
		importTexture.addActionListener(e -> {
			TextureImportDialogs.importMultipleTextures(mcreator, TextureType.ENTITY);
			reload();
		});
		add("East", importTexture);

		reload();
	}

	public TextureComboBox requireValue(String errorTranslationKey) {
		comboBox.setValidator(() -> {
			if (comboBox.getSelectedItem() == null || comboBox.getSelectedItem().equals(empty))
				return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
						L10N.t(errorTranslationKey));
			return Validator.ValidationResult.PASSED;
		});
		return this;
	}

	public void reload() {
		List<File> customTextureFiles;
		if (textureType == TextureType.ARMOR) {
			customTextureFiles = new ArrayList<>();
			List<File> armors = mcreator.getFolderManager().getTexturesList(TextureType.ARMOR);
			for (File texture : armors)
				if (texture.getName().endsWith("_layer_1.png"))
					customTextureFiles.add(texture);
		} else {
			customTextureFiles = mcreator.getFolderManager().getTexturesList(textureType);
		}

		if (showEmpty) {
			ComboBoxUtil.updateComboBoxContents(comboBox, ListUtils.merge(Collections.singleton(empty),
					customTextureFiles.stream().map(e -> new Texture.Custom(textureType, e))
							.collect(Collectors.toList())), empty);
		} else {
			ComboBoxUtil.updateComboBoxContents(comboBox,
					customTextureFiles.stream().map(e -> new Texture.Custom(textureType, e))
							.collect(Collectors.toList()));
		}
	}

	public void setTextureFromTextureName(@Nullable String textureName) {
		if (textureName != null && !textureName.isBlank()) {
			textureName = FilenameUtils.removeExtension(textureName);
			comboBox.setSelectedItem(Texture.fromName(mcreator.getWorkspace(), textureType, textureName));
		}
	}

	public String getTextureName() {
		Texture selected = comboBox.getSelectedItem();
		if (selected == null || selected.equals(empty))
			return defaultTextureName;
		return selected.getTextureName() + (addPNGExtension ? ".png" : "");
	}

	public boolean hasTexture() {
		return getTexture() != null && !getTexture().equals(empty);
	}

	public Texture getTexture() {
		return comboBox.getSelectedItem();
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return comboBox.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
		comboBox.setValidator(validator);
	}

	@Override public Validator getValidator() {
		return comboBox.getValidator();
	}

	public SearchableComboBox<Texture> getComboBox() {
		return comboBox;
	}

	public void setAddPNGExtension(boolean addPNGExtension) {
		this.addPNGExtension = addPNGExtension;
	}

	private class Renderer extends JLabel implements ListCellRenderer<Texture> {

		public Renderer() {
			setOpaque(true);
			setHorizontalAlignment(CENTER);
			setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList<? extends Texture> list, Texture value, int index,
				boolean isSelected, boolean cellHasFocus) {

			if (isSelected) {
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
			} else {
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setText(value.getTextureName());

			ImageIcon imageIcon = value.getTextureIcon(mcreator.getWorkspace());
			if (imageIcon != null) {
				setIcon(new ImageIcon(ImageUtils.resize(imageIcon.getImage(), 30)));
			} else {
				setIcon(new EmptyIcon(30, 30));
			}

			setHorizontalTextPosition(SwingConstants.RIGHT);
			setHorizontalAlignment(SwingConstants.LEFT);

			return this;
		}

	}

}
