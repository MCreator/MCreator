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

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.types.KeyBinding;
import net.mcreator.minecraft.DataListEntry;
import net.mcreator.minecraft.DataListLoader;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.component.util.ComponentUtils;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.AggregatedValidationResult;
import net.mcreator.ui.validation.component.VComboBox;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.ui.validation.validators.RegistryNameValidator;
import net.mcreator.ui.validation.validators.TextFieldValidator;
import net.mcreator.util.StringUtils;
import net.mcreator.workspace.elements.ModElement;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class KeyBindGUI extends ModElementGUI<KeyBinding> {

	private ProcedureSelector onKeyPressed;
	private ProcedureSelector onKeyReleased;

	private final JComboBox<String> triggerKey = new JComboBox<>(
			DataListLoader.loadDataList("keybuttons").stream().map(DataListEntry::getName).toArray(String[]::new));

	private final VTextField keyBindingName = new VTextField(20);

	private final VComboBox<String> keyBindingCategoryKey = new VComboBox<>(
			new String[] { "misc", "movement", "multiplayer", "gameplay", "ui", "inventory", "creative" });

	public KeyBindGUI(MCreator mcreator, ModElement modElement, boolean editingMode) {
		super(mcreator, modElement, editingMode);
		this.initGUI();
		super.finalizeGUI();
	}

	@Override protected void initGUI() {
		onKeyPressed = new ProcedureSelector(this.withEntry("keybinding/when_key_pressed"), mcreator,
				L10N.t("elementgui.keybind.event_key_pressed"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity"));

		onKeyReleased = new ProcedureSelector(this.withEntry("keybinding/when_key_released"), mcreator,
				L10N.t("elementgui.keybind.event_key_released"),
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/pressedms:number"));

		JPanel pane5 = new JPanel(new BorderLayout(10, 10));

		triggerKey.setFont(triggerKey.getFont().deriveFont(16.0f));

		ComponentUtils.deriveFont(keyBindingName, 16);

		JPanel enderpanel = new JPanel(new GridLayout(3, 2, 10, 10));

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("keybinding/key"),
				L10N.label("elementgui.keybind.key_trigger_event")));
		enderpanel.add(triggerKey);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("keybinding/name"),
				L10N.label("elementgui.keybind.key_binding_name")));
		enderpanel.add(keyBindingName);

		enderpanel.add(HelpUtils.wrapWithHelpButton(this.withEntry("keybinding/category"),
				L10N.label("elementgui.keybind.key_binding_category")));
		enderpanel.add(PanelUtils.westAndCenterElement(new JLabel("key.categories."), keyBindingCategoryKey));

		keyBindingCategoryKey.setEditable(true);

		enderpanel.setOpaque(false);

		JPanel events = new JPanel(new GridLayout(1, 2, 5, 5));
		events.setOpaque(false);
		events.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(Theme.current().getForegroundColor(), 1),
				L10N.t("elementgui.keybind.key_procedure_triggers"), 0, 0, getFont().deriveFont(12.0f),
				Theme.current().getForegroundColor()));
		events.add(onKeyPressed);
		events.add(onKeyReleased);

		pane5.setOpaque(false);

		JPanel merge = new JPanel(new BorderLayout(20, 20));
		merge.setOpaque(false);
		merge.add("North", PanelUtils.centerInPanel(enderpanel));
		merge.add("South", events);

		pane5.add("Center", PanelUtils.totalCenterInPanel(PanelUtils.centerInPanel(merge)));

		keyBindingName.setValidator(
				new TextFieldValidator(keyBindingName, L10N.t("elementgui.keybind.error_key_needs_name")));
		keyBindingName.enableRealtimeValidation();

		keyBindingCategoryKey.setValidator(new RegistryNameValidator(keyBindingCategoryKey,
				L10N.t("elementgui.keybind.error_key_category_needs_name")));
		keyBindingCategoryKey.enableRealtimeValidation();

		addPage(pane5);

		if (!isEditingMode()) {
			String readableNameFromModElement = StringUtils.machineToReadableName(modElement.getName());
			keyBindingName.setText(readableNameFromModElement);
		}
	}

	@Override public void reloadDataLists() {
		super.reloadDataLists();
		onKeyPressed.refreshListKeepSelected();
		onKeyReleased.refreshListKeepSelected();
	}

	@Override protected AggregatedValidationResult validatePage(int page) {
		return new AggregatedValidationResult(keyBindingName, keyBindingCategoryKey);
	}

	@Override public void openInEditingMode(KeyBinding keyBinding) {
		triggerKey.setSelectedItem(keyBinding.triggerKey);
		keyBindingName.setText(keyBinding.keyBindingName);
		onKeyPressed.setSelectedProcedure(keyBinding.onKeyPressed);
		onKeyReleased.setSelectedProcedure(keyBinding.onKeyReleased);
		keyBindingCategoryKey.getEditor().setItem(keyBinding.keyBindingCategoryKey);
	}

	@Override public KeyBinding getElementFromGUI() {
		KeyBinding keyBinding = new KeyBinding(modElement);
		keyBinding.triggerKey = (String) Objects.requireNonNull(triggerKey.getSelectedItem());
		keyBinding.keyBindingName = keyBindingName.getText();
		keyBinding.onKeyPressed = onKeyPressed.getSelectedProcedure();
		keyBinding.onKeyReleased = onKeyReleased.getSelectedProcedure();
		keyBinding.keyBindingCategoryKey = keyBindingCategoryKey.getEditor().getItem().toString();
		return keyBinding;
	}

	@Override public @Nullable URI contextURL() throws URISyntaxException {
		return new URI(MCreatorApplication.SERVER_DOMAIN + "/wiki/how-make-key-binding");
	}

}
