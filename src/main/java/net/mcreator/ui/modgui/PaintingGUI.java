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

package net.mcreator.ui.modgui;

import net.mcreator.element.types.Painting;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.dialogs.TypedTextureSelectorDialog;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.minecraft.TextureHolder;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.ValidationGroup;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.ui.validation.validators.TileHolderValidator;
import net.mcreator.ui.workspace.resources.TextureType;
import net.mcreator.workspace.elements.ModElement;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;

public class PaintingGUI extends ModElementGUI<Painting> {

	private final JSpinner width = new JSpinner(new SpinnerNumberModel(16, 16, 64000, 16));
	private final JSpinner height = new JSpinner(new SpinnerNumberModel(16, 16, 64000, 16));
	private final VTextField title = new VTextField(28);
	private final VTextField author = new VTextField(28);

	private TextureHolder texture;

	private final ValidationGroup page1group = new ValidationGroup();

	public PaintingGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		texture = new TextureHolder(new TypedTextureSelectorDialog(mcreator, TextureType.OTHER));
		texture.setOpaque(false);

		JComponent textureComponent = PanelUtils.centerInPanel(ComponentUtils.squareAndBorder(
				HelpUtils.wrapWithHelpButton(this.withEntry("painting/texture"), texture),
				L10N.t("elementgui.common.texture")));

		JPanel pane3 = new JPanel(new BorderLayout());
		pane3.setOpaque(false);

		ComponentUtils.deriveFont(title, 16);
		ComponentUtils.deriveFont(author, 16);

		JPanel selp = new JPanel(new GridLayout(4, 2, 50, 2));
		selp.setOpaque(false);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("painting/title"),
				L10N.label("elementgui.painting.painting_title")));
		selp.add(title);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("paintings/author"),
				L10N.label("elementgui.painting.painting_author")));
		selp.add(author);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("painting/width"),
				L10N.label("elementgui.painting.painting_width")));
		selp.add(width);

		selp.add(HelpUtils.wrapWithHelpButton(this.withEntry("painting/height"),
				L10N.label("elementgui.painting.painting_height")));
		selp.add(height);

		pane3.add("Center",
				PanelUtils.totalCenterInPanel(PanelUtils.northAndCenterElement(textureComponent, selp, 25, 25)));

		texture.setValidator(new TileHolderValidator(texture));

		title.setValidator(
				new TextFieldValidator(title, L10N.t("elementgui.painting.painting_needs_title")));
		title.enableRealtimeValidation();

		author.setValidator(new TextFieldValidator(author, L10N.t("elementgui.painting.painting_needs_author")));
		author.enableRealtimeValidation();

		page1group.addValidationElement(texture);
		page1group.addValidationElement(title);
		page1group.addValidationElement(author);

		addPage(L10N.t("elementgui.common.page_properties"), pane3);

		if (!isEditingMode()) {
			String readableNameFromModElement = net.mcreator.util.StringUtils.machineToReadableName(
					modElement.getName());
			title.setText(readableNameFromModElement);
		}
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		if (page == 0)
			return new AggregatedValidationResult(page1group);
		return new AggregatedValidationResult.PASS();
	}

	@Override public void openInEditingMode(Painting painting) {
		title.setText(painting.title);
		author.setText(painting.author);
		width.setValue(painting.width);
		height.setValue(painting.height);
		texture.setTextureFromTextureName(
				StringUtils.removeEnd(painting.texture, ".png")); // legacy, old workspaces stored name with extension
	}

	@Override public Painting getElementFromGUI() {
		Painting painting = new Painting(modElement);
		painting.title = title.getText();
		painting.author = author.getText();
		painting.width = (int) width.getValue();
		painting.height = (int) height.getValue();
		painting.texture = texture.getID() + ".png"; // legacy, old workspaces stored name with extension
		return painting;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-painting");
	}

}
