/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2021, Pylo, opensource contributors
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

package net.mcreator.ui.minecraft.states.item;

import net.mcreator.blockly.data.Dependency;
import net.mcreator.element.parts.procedure.Procedure;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.component.util.PanelUtils;
import net.mcreator.ui.help.HelpUtils;
import net.mcreator.ui.help.IHelpContext;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.procedure.ProcedureSelector;
import net.mcreator.ui.validation.IValidable;
import net.mcreator.ui.validation.Validator;
import net.mcreator.ui.validation.component.VTextField;
import net.mcreator.workspace.elements.VariableTypeLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class JItemPropertiesListEntry extends JPanel implements IValidable {

	private final VTextField name;
	private String cachedName;
	private final JButton rename = new JButton(UIRES.get("16px.edit.gif"));

	private final ProcedureSelector value;

	private final JButton remove;

	public JItemPropertiesListEntry(MCreator mcreator, IHelpContext gui, JPanel parent,
			List<JItemPropertiesListEntry> entryList, int propertyId) {
		super(new FlowLayout(FlowLayout.LEFT));
		name = new VTextField(20) {
			@Override public void setEditable(boolean b) {
				super.setEditable(b);
				rename.setEnabled(!b);
			}
		};
		name.setOpaque(true);
		name.setText(cachedName = "property" + propertyId);
		name.setToolTipText(L10N.t("elementgui.item.custom_property.name_renaming"));
		name.setEditable(false);
		name.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		name.addFocusListener(new FocusAdapter() {
			@Override public void focusLost(FocusEvent e) {
				name.setEditable(false);
				name.setText(cachedName);
				name.getValidationStatus();
			}
		});

		rename.setOpaque(false);
		rename.setMargin(new Insets(0, 0, 0, 0));
		rename.setBorder(BorderFactory.createEmptyBorder());
		rename.setContentAreaFilled(false);
		rename.setToolTipText(L10N.t("elementgui.item.custom_property.rename"));
		rename.addActionListener(e -> {
			name.setEditable(true);
			name.requestFocus();
		});

		value = new ProcedureSelector(gui.withEntry("item/custom_property_value"), mcreator,
				L10N.t("elementgui.item.custom_property.value"),
				L10N.t("elementgui.item.custom_property.value") + propertyId, ProcedureSelector.Side.CLIENT, true,
				VariableTypeLoader.BuiltInTypes.NUMBER,
				Dependency.fromString("x:number/y:number/z:number/world:world/entity:entity/itemstack:itemstack"));
		value.setDefaultName(L10N.t("elementgui.item.custom_property.value.default"));
		reloadDataLists(); // we make sure that selector can be properly shown

		JPanel butPan = new JPanel();
		butPan.setBackground((Color) UIManager.get("MCreatorLAF.LIGHT_ACCENT"));
		butPan.add(rename);
		JPanel namePane = new JPanel(new FlowLayout(FlowLayout.CENTER, 7, 7));
		namePane.setBackground((Color) UIManager.get("MCreatorLAF.BLACK_ACCENT"));
		namePane.add(name);
		namePane.add(butPan);
		add(HelpUtils.stackHelpTextAndComponent(gui.withEntry("item/custom_property_name"),
				L10N.t("elementgui.item.custom_property.name"), namePane, 3));
		add(value);

		JComponent container = PanelUtils.expandHorizontally(this);
		parent.add(container);
		entryList.add(this);

		remove = new JButton(UIRES.get("16px.clear"));
		remove.setText(L10N.t("elementgui.item.custom_property.remove"));
		remove.addActionListener(e -> {
			entryList.remove(this);
			parent.remove(container);
			parent.revalidate();
			parent.repaint();
		});
		add(remove);

		parent.revalidate();
		parent.repaint();
	}

	@Override public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		remove.setEnabled(enabled);

		name.setEnabled(enabled);
		rename.setEnabled(enabled);
		value.setEnabled(enabled);
	}

	public VTextField getNameField() {
		return name;
	}

	public String getCachedName() {
		return cachedName;
	}

	public void renameTo(String newName) {
		name.setText(cachedName = newName);
	}

	public void finishRenaming() {
		rename.requestFocus();
	}

	public void reloadDataLists() {
		value.refreshListKeepSelected();
	}

	public Procedure getEntry() {
		return value.getSelectedProcedure();
	}

	public void setEntry(String name, Procedure value) {
		this.name.setText(cachedName = name);
		this.value.setSelectedProcedure(value);
	}

	@Override public Validator.ValidationResult getValidationStatus() {
		return name.getValidationStatus();
	}

	@Override public void setValidator(Validator validator) {
	}

	@Override public Validator getValidator() {
		return name.getValidator();
	}
}