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

import net.mcreator.element.parts.TextureHolder;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.JEmptyBox;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.validators.TextureSelectionButtonValidator;
import net.mcreator.ui.workspace.resources.TextureType;

import javax.swing.*;
import java.awt.*;

public class BlockTexturesSelector extends JPanel implements IValidable {

	private final TextureSelectionButton texture;
	private final TextureSelectionButton textureTop;
	private final TextureSelectionButton textureLeft;
	private final TextureSelectionButton textureFront;
	private final TextureSelectionButton textureRight;
	private final TextureSelectionButton textureBack;

	public BlockTexturesSelector(MCreator mcreator) {
		super(new GridLayout(3, 4));
		setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK)).setFlipUV(
				true);
		textureTop = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK)).setFlipUV(
				true);

		textureLeft = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureFront = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureRight = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));
		textureBack = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.BLOCK));

		texture.setOpaque(false);
		textureTop.setOpaque(false);
		textureLeft.setOpaque(false);
		textureFront.setOpaque(false);
		textureRight.setOpaque(false);
		textureBack.setOpaque(false);

		add(new JEmptyBox());
		add(ComponentUtils.squareAndBorder(textureTop, L10N.t("elementgui.block.texture_place_top")));
		add(new JEmptyBox());
		add(new JEmptyBox());

		add(ComponentUtils.squareAndBorder(textureLeft, new Color(126, 196, 255),
				L10N.t("elementgui.block.texture_place_left_overlay")));
		add(ComponentUtils.squareAndBorder(textureFront, L10N.t("elementgui.block.texture_place_front_side")));
		add(ComponentUtils.squareAndBorder(textureRight, L10N.t("elementgui.block.texture_place_right")));
		add(ComponentUtils.squareAndBorder(textureBack, L10N.t("elementgui.block.texture_place_back")));

		add(new JEmptyBox());
		add(ComponentUtils.squareAndBorder(texture, new Color(125, 255, 174),
				L10N.t("elementgui.block.texture_place_bottom_main")));
		add(new JEmptyBox());
		add(new JEmptyBox());

		textureLeft.addTextureSelectedListener(event -> {
			if (!(texture.hasTexture() || textureTop.hasTexture() || textureBack.hasTexture()
					|| textureFront.hasTexture() || textureRight.hasTexture())) {
				texture.setTexture(textureLeft.getTextureHolder());
				textureTop.setTexture(textureLeft.getTextureHolder());
				textureBack.setTexture(textureLeft.getTextureHolder());
				textureFront.setTexture(textureLeft.getTextureHolder());
				textureRight.setTexture(textureLeft.getTextureHolder());
			}
		});

		texture.setValidator(new TextureSelectionButtonValidator(texture));
	}

	public void setTextures(TextureHolder texture, TextureHolder textureTop, TextureHolder textureLeft,
			TextureHolder textureFront, TextureHolder textureRight, TextureHolder textureBack) {
		this.texture.setTexture(texture);
		this.textureTop.setTexture(textureTop);
		this.textureLeft.setTexture(textureLeft);
		this.textureFront.setTexture(textureFront);
		this.textureRight.setTexture(textureRight);
		this.textureBack.setTexture(textureBack);
	}

	public void setTextureFormat(TextureFormat format) {
		texture.setFlipUV(false);
		textureTop.setFlipUV(false);
		textureTop.setVisible(false);
		textureLeft.setVisible(false);
		textureFront.setVisible(false);
		textureRight.setVisible(false);
		textureBack.setVisible(false);

		switch (format) {
		case ALL:
			texture.setFlipUV(true);
			textureTop.setFlipUV(true);
			textureTop.setVisible(true);
			textureLeft.setVisible(true);
			textureFront.setVisible(true);
			textureRight.setVisible(true);
			textureBack.setVisible(true);
			break;
		case GRASS:
			textureTop.setVisible(true);
			textureLeft.setVisible(true);
			textureFront.setVisible(true);
			break;
		case TOP_BOTTOM:
			textureTop.setVisible(true);
			break;
		case TOP_BOTTOM_SIDES:
			textureTop.setVisible(true);
			textureFront.setVisible(true);
			break;
		default:
			// Bottom is always visible
			break;
		}
	}

	public TextureHolder getTexture() {
		return texture.getTextureHolder();
	}

	public TextureHolder getTextureTop() {
		return textureTop.getTextureHolder();
	}

	public TextureHolder getTextureLeft() {
		return textureLeft.getTextureHolder();
	}

	public TextureHolder getTextureFront() {
		return textureFront.getTextureHolder();
	}

	public TextureHolder getTextureRight() {
		return textureRight.getTextureHolder();
	}

	public TextureHolder getTextureBack() {
		return textureBack.getTextureHolder();
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return texture.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return texture.getValidator();
	}

	public enum TextureFormat {
		ALL, SINGLE_TEXTURE, TOP_BOTTOM, TOP_BOTTOM_SIDES, GRASS
	}

}
