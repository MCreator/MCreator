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
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureSelectionButton;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.util.StringUtils;
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
		shieldTexture = new TextureSelectionButton(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		shieldTexture.setOpaque(false);

		texturesPanel.add(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/texture"), texture),
				L10N.t("elementgui.banner_pattern.texture")));
		texturesPanel.add(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/shield_texture"), shieldTexture),
				L10N.t("elementgui.banner_pattern.shield_texture")));

		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/name"),
				L10N.label("elementgui.banner_pattern.name")));
		properties.add(name);
		properties.add(HelpUtils.wrapWithHelpButton(this.withEntry("banner_pattern/require_item"),
				L10N.label("elementgui.banner_pattern.require_item")));
		properties.add(requireItem);

		name.setPreferredSize(new Dimension(0, 32));

		requireItem.setOpaque(false);

		texture.setValidator(new TileHolderValidator(texture));
		shieldTexture.setValidator(new TileHolderValidator(shieldTexture));
		name.setValidator(new TextFieldValidator(name, L10N.t("elementgui.banner_pattern.pattern_needs_name")));
		name.enableRealtimeValidation();

		page1group.addValidationElement(texture);
		page1group.addValidationElement(shieldTexture);
		page1group.addValidationElement(name);

		addPage(PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(texturesPanel, properties, 25, 25)));

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			name.setText(readableNameFromModElement);
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override protected void openInEditingMode(BannerPattern bannerPattern) {
		texture.setTexture(bannerPattern.texture);
		shieldTexture.setTexture(bannerPattern.shieldTexture);
		name.setText(bannerPattern.name);
		requireItem.setSelected(bannerPattern.requireItem);
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
