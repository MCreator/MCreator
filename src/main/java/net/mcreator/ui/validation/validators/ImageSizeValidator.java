/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2023, Pylo, opensource contributors
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

package net.mcreator.ui.validation.validators;

import net.mcreator.ui.init.L10N;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.Workspace;
import org.gradle.internal.FileUtils;

import javax.swing.*;

public class ImageSizeValidator implements Validator {

	private String firstImage, secondImage;
	private final TextureType type;
	private final Workspace workspace;
	private final boolean passIfSecondEmpty;

	/**
	 * This validator takes 2 images from a same {@link TextureType} and check if they have the same width and height.
	 *
	 * @param firstImage        This is the first image of the check. This image can never be null or empty.
	 * @param secondImage This
	 * @param type
	 * @param workspace
	 * @param passIfSecondEmpty
	 */
	public ImageSizeValidator(String firstImage, String secondImage, TextureType type, Workspace workspace,
			boolean passIfSecondEmpty) {
		this.firstImage = firstImage;
		this.secondImage = secondImage;
		this.type = type;
		this.workspace = workspace;
		this.passIfSecondEmpty = passIfSecondEmpty;
	}

	public void setFirstImage(String firstImage) {
		this.firstImage = firstImage;
	}

	public void setSecondImage(String secondImage) {
		this.secondImage = secondImage;
	}

	@Override public ValidationResult validate() {
		// The first image can never be null as this is the reference
		if (firstImage != null && !firstImage.isEmpty())
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validator.image_size.empty"));

		// Then we check if the second image can be empty or null, so we can pass the validation if needed
		if (passIfSecondEmpty && (secondImage == null || secondImage.isEmpty()))
			return Validator.ValidationResult.PASSED;
		else if (!passIfSecondEmpty && (secondImage == null || secondImage.isEmpty()))
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR,
					L10N.t("validator.image_size.empty"));

		// Finally, we can check if both images have the same height and width
		ImageIcon image1 = workspace.getFolderManager()
				.getTextureImageIcon(FileUtils.removeExtension(firstImage), type);
		ImageIcon image2 = workspace.getFolderManager()
				.getTextureImageIcon(FileUtils.removeExtension(secondImage), type);

		if (image1.getIconHeight() == image2.getIconHeight() && image1.getIconWidth() == image2.getIconWidth())
			return Validator.ValidationResult.PASSED;
		else
			return new Validator.ValidationResult(Validator.ValidationResultType.ERROR, L10N.t("validator.image_size"));
	}
}
