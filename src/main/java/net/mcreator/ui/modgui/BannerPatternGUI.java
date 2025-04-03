/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2025, Pylo, opensource contributors
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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.BannerPattern;
import net.mcreator.minecraft.MinecraftImageGenerator;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
import net.mcreator.util.image.ImageUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class BannerPatternGUI extends ModElementGUI<BannerPattern> {

	private TextureSelectionButton texture;
	private TextureSelectionButton shieldTexture;
	private final VTextField name = new VTextField(28);
	private final JCheckBox requireItem = L10N.checkbox("elementgui.common.enable");

	// Banner pattern previews
	private static final int PREVIEW_SCALE = 5;
	private final JLabel bannerPreview = new JLabel();
	private final JLabel shieldPreview = new JLabel();

	private final ValidationGroup page1group = new ValidationGroup();

	public BannerPatternGUI(MCreator mcreator, @Nonnull ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		JPanel properties = new JPanel(new GridLayout(2, 2, 5, 2));
		properties.setOpaque(false);

		JPanel texturesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		texturesPanel.setOpaque(false);

		texture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		texture.setOpaque(false);
		texture.addTextureSelectedListener(e -> updatePatternPreviews());
		shieldTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		shieldTexture.setOpaque(false);
		shieldTexture.addTextureSelectedListener(e -> updatePatternPreviews());

		texturesPanel.add(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/texture"), texture),
				L10N.t("elementgui.banner_pattern.texture")));
		texturesPanel.add(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/shield_texture"), shieldTexture),
				L10N.t("elementgui.banner_pattern.shield_texture")));

		// Pattern previews
		bannerPreview.setPreferredSize(new Dimension(42 * PREVIEW_SCALE, 42 * PREVIEW_SCALE));
		shieldPreview.setPreferredSize(new Dimension(26 * PREVIEW_SCALE, 24 * PREVIEW_SCALE));

		JPanel previewsPanel = new JPanel();
		previewsPanel.setOpaque(false);
		previewsPanel.add(bannerPreview);
		previewsPanel.add(shieldPreview);

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/name"),
				L10N.label("elementgui.banner_pattern.name")));
		properties.add(name);
		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/require_item"),
				L10N.label("elementgui.banner_pattern.require_item")));
		properties.add(requireItem);

		name.setPreferredSize(new Dimension(0, 32));

		requireItem.setOpaque(false);

		JPanel texturesWithProperties = new JPanel(new BorderLayout(35, 35));
		texturesWithProperties.add("Center", texturesPanel);
		texturesWithProperties.add("South", properties);
		texturesWithProperties.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));
		shieldTexture.setValidator(new TileHolderValidator(shieldTexture));
		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.banner_pattern.pattern_needs_name")));
		name.enableRealtimeValidation();

		page1group.addValidationElement(texture);
		page1group.addValidationElement(shieldTexture);
		page1group.addValidationElement(name);

		addPage(PanelUtils.totalCenterInPanel(
				PanelUtils.northAndCenterElement(texturesWithProperties, previewsPanel, 25, 25))).validate(page1group);

		updatePatternPreviews();

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	private void updatePatternPreviews() {
		// Try to generate banner preview
		if (!texture.getTextureHolder().isEmpty()) {
			var resizedImage = toPreviewIcon(texture.getTextureHolder().getImage(TextureType.OTHER), false);
			ImageIcon front1 = new ImageIcon(MinecraftImageGenerator.Preview.generateBannerPatternPreview());
			bannerPreview.setIcon(ImageUtils.drawOver(UIRES.get("mod_preview_bases.banner_pattern_preview_base"),
					ImageUtils.drawOver(resizedImage, front1)));
		} else {
			bannerPreview.setIcon(ImageUtils.drawOver(UIRES.get("mod_preview_bases.banner_pattern_preview_base"),
					new ImageIcon(MinecraftImageGenerator.Preview.generateBannerPatternPreview())));
		}
		// Try to generate shield preview
		if (!shieldTexture.getTextureHolder().isEmpty()) {
			var resizedImage = toPreviewIcon(shieldTexture.getTextureHolder().getImage(TextureType.OTHER), true);
			ImageIcon front1 = new ImageIcon(MinecraftImageGenerator.Preview.generateShieldPatternPreview());
			shieldPreview.setIcon(ImageUtils.drawOver(UIRES.get("mod_preview_bases.shield_pattern_preview_base"),
					ImageUtils.drawOver(resizedImage, front1)));
		} else {
			shieldPreview.setIcon(ImageUtils.drawOver(UIRES.get("mod_preview_bases.shield_pattern_preview_base"),
					new ImageIcon(MinecraftImageGenerator.Preview.generateShieldPatternPreview())));
		}
	}

	private ImageIcon toPreviewIcon(Image texture, boolean isShield) {
		int cropX = isShield ? 26 : 42, cropY = isShield ? 24 : 42;
		int patternX = isShield ? 12 : 20, patternY = isShield ? 22 : 40;
		var resizedImage = ImageUtils.resize(texture, 64 * PREVIEW_SCALE);
		var firstCrop = ImageUtils.crop(ImageUtils.toBufferedImage(resizedImage),
				new Rectangle(cropX * PREVIEW_SCALE, cropY * PREVIEW_SCALE));
		var onlyBottom = ImageUtils.crop(ImageUtils.deepCopy(firstCrop), new Rectangle((patternX + 1) * PREVIEW_SCALE,
				0, patternX * PREVIEW_SCALE, PREVIEW_SCALE));
		var withoutBottom = ImageUtils.eraseRect(firstCrop, (patternX + 1) * PREVIEW_SCALE, 0,
				patternX * PREVIEW_SCALE, PREVIEW_SCALE);
		return ImageUtils.drawOver(new ImageIcon(withoutBottom), new ImageIcon(onlyBottom),
				PREVIEW_SCALE, (1 + patternY) * PREVIEW_SCALE, patternX * PREVIEW_SCALE, PREVIEW_SCALE);
	}

	@Override protected void openInEditingMode(BannerPattern bannerPattern) {
		texture.setTexture(bannerPattern.texture);
		shieldTexture.setTexture(bannerPattern.shieldTexture);
		name.setText(bannerPattern.name);
		requireItem.setSelected(bannerPattern.requireItem);
		updatePatternPreviews();
	}

	@Override public BannerPattern getElementFromGUI() {
		BannerPattern bannerPattern = new BannerPattern(modElement);
		bannerPattern.texture = texture.getTextureHolder();
		bannerPattern.shieldTexture = shieldTexture.getTextureHolder();
		bannerPattern.name = name.getText();
		bannerPattern.requireItem = requireItem.isSelected();
		return bannerPattern;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-banner-pattern");
	}

}
