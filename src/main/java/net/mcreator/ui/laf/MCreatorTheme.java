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

package net.mcreator.ui.laf;

import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.themes.Theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MCreatorTheme extends OceanTheme {

	private final Theme theme;

	public MCreatorTheme(Theme theme) {
		this.theme = theme;
	}

	@Override public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);

		Set<Object> keySet = table.keySet();
		for (Object key : keySet) {
			if (key == null)
				continue;
			if (key.toString().toLowerCase(Locale.ENGLISH).contains("font")) {
				table.put(key, theme.getSecondaryFont().deriveFont((float) theme.getFontSize()));
			} else if (key.toString().toLowerCase(Locale.ENGLISH).contains("bordercolor")) {
				table.put(key, theme.getInterfaceAccentColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".background")) {
				table.put(key, theme.getBackgroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".foreground")) {
				table.put(key, theme.getForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".inactiveforeground")) {
				table.put(key, theme.getAltForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledbackground")) {
				table.put(key, theme.getBackgroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledforeground")) {
				table.put(key, theme.getAltForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".caretforeground")) {
				table.put(key, theme.getForegroundColor());
			}
		}

		table.put("TabbedPane.contentOpaque", false);

		table.put("Tree.rendererFillBackground", false);

		table.put("TitledBorder.titleColor", theme.getForegroundColor());

		table.put("SplitPane.dividerFocusColor", theme.getAltBackgroundColor());
		table.put("SplitPane.darkShadow", theme.getAltBackgroundColor());
		table.put("SplitPane.shadow", theme.getAltBackgroundColor());
		table.put("SplitPaneDivider.draggingColor", theme.getInterfaceAccentColor());

		table.put("OptionPane.messageForeground", theme.getForegroundColor());

		table.put("Label.foreground", theme.getForegroundColor());
		table.put("Label.disabledForeground", theme.getForegroundColor());
		table.put("Label.inactiveforeground", theme.getForegroundColor());
		table.put("Label.textForeground", theme.getForegroundColor());

		table.put("Button.toolBarBorderBackground", theme.getForegroundColor());
		table.put("Button.disabledToolBarBorderBackground", theme.getAltBackgroundColor());
		table.put("ToolBar.rolloverBorder",
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(theme.getBackgroundColor(), 1),
						BorderFactory.createCompoundBorder(
								BorderFactory.createLineBorder(theme.getAltBackgroundColor(), 1),
								BorderFactory.createLineBorder(theme.getBackgroundColor(), 3))));

		table.put("ScrollBarUI", SlickDarkScrollBarUI.class.getName());
		table.put("SpinnerUI", DarkSpinnerUI.class.getName());
		table.put("SplitPaneUI", DarkSplitPaneUI.class.getName());
		table.put("SliderUI", DarkSliderUI.class.getName());
		table.put("ComboBoxUI", DarkComboBoxUI.class.getName());

		table.put("Menu.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));
		table.put("MenuItem.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));

		table.put("PopupMenu.border", BorderFactory.createLineBorder(theme.getAltBackgroundColor()));

		table.put("Separator.foreground", theme.getAltBackgroundColor());
		table.put("Separator.background", theme.getBackgroundColor());

		table.put("Menu.foreground", theme.getForegroundColor());
		table.put("MenuItem.foreground", theme.getForegroundColor());

		table.put("ComboBox.foreground", theme.getForegroundColor());
		table.put("ComboBox.background", theme.getAltBackgroundColor());
		table.put("ComboBox.disabledForeground", theme.getAltForegroundColor());

		table.put("Spinner.foreground", theme.getForegroundColor());
		table.put("Spinner.background", theme.getAltBackgroundColor());

		table.put("FormattedTextField.foreground", theme.getForegroundColor());
		table.put("FormattedTextField.inactiveForeground", theme.getAltForegroundColor());
		table.put("FormattedTextField.background", theme.getAltBackgroundColor());
		table.put("FormattedTextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("TextField.foreground", theme.getForegroundColor());
		table.put("TextField.inactiveForeground", theme.getAltForegroundColor());
		table.put("TextField.background", theme.getAltBackgroundColor());
		table.put("TextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("PasswordField.foreground", theme.getForegroundColor());
		table.put("PasswordField.inactiveForeground", theme.getAltForegroundColor());
		table.put("PasswordField.background", theme.getAltBackgroundColor());
		table.put("PasswordField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("ComboBox.border", null);

		List<?> buttonGradient = Arrays.asList(0f, 0f, new ColorUIResource(theme.getForegroundColor()),
				new ColorUIResource(theme.getForegroundColor()), new ColorUIResource(theme.getForegroundColor()));

		table.put("Button.gradient", buttonGradient);
		table.put("Button.rollover", true);

		table.put("CheckBox.gradient", buttonGradient);
		table.put("CheckBox.rollover", true);

		table.put("RadioButton.gradient", buttonGradient);
		table.put("RadioButtonMenuItem.gradient", buttonGradient);
		table.put("RadioButton.rollover", true);
		table.put("RadioButtonMenuItem.rollover", true);
		table.put("RadioButtonMenuItem.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));

		table.put("ToggleButton.gradient", buttonGradient);
		table.put("ToggleButton.rollover", true);

		List<?> sliderGradient = Arrays.asList(0f, 0f, new ColorUIResource(theme.getBackgroundColor()),
				new ColorUIResource(theme.getBackgroundColor()), new ColorUIResource(theme.getBackgroundColor()));

		table.put("Slider.altTrackColor", new ColorUIResource(theme.getBackgroundColor()));
		table.put("Slider.gradient", sliderGradient);
		table.put("Slider.focusGradient", sliderGradient);

		table.put("Spinner.border", BorderFactory.createEmptyBorder());

		table.put("List.focusCellHighlightBorder", null);

		table.put("List.border", null);
		table.put("ScrollPane.border", null);
		table.put("Tree.border", null);

		table.put("Button.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("ToggleButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("CheckBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("TabbedPane.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("RadioButton.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("RadioButtonMenuItem.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("Slider.focus", new ColorUIResource(new Color(0, 0, 0, 0)));
		table.put("ComboBox.focus", new ColorUIResource(new Color(0, 0, 0, 0)));

		table.put("CheckBox.icon", new CheckBoxIcon());
		table.put("RadioButton.icon", new RadioButtonIcon());
		table.put("RadioButtonMenuItem.icon", new RadioButtonIcon());

		table.put("TabbedPane.contentAreaColor", theme.getBackgroundColor());
		table.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3));
		table.put("TabbedPane.selected", theme.getBackgroundColor());
		table.put("TabbedPane.tabAreaBackground", theme.getAltBackgroundColor());
		table.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6));
		table.put("TabbedPane.unselectedBackground", theme.getBackgroundColor());

		table.put("ToolTip.border", BorderFactory.createLineBorder(theme.getForegroundColor()));
		table.put("ToolTip.foreground", theme.getForegroundColor());
		table.put("ToolTip.background", theme.getBackgroundColor());

		table.put("ScrollBar.width", 7);

		table.put("SplitPane.border", BorderFactory.createEmptyBorder());

		table.put("FileChooser.homeFolderIcon", UIRES.get("laf.homeFolder.gif"));
		table.put("FileChooser.newFolderIcon", UIRES.get("laf.newFolder.gif"));
		table.put("FileChooser.upFolderIcon", UIRES.get("laf.upFolder.gif"));
		table.put("FileChooser.computerIcon", UIRES.get("laf.computer.gif"));
		table.put("FileChooser.hardDriveIcon", UIRES.get("laf.hardDrive.gif"));
		table.put("FileChooser.floppyDriveIcon", UIRES.get("laf.floppy.gif"));
		table.put("FileChooser.closedIcon", UIRES.get("laf.newFolder.gif"));

		table.put("Tree.closedIcon", UIRES.get("laf.newFolder.gif"));
		table.put("Tree.openIcon", UIRES.get("laf.upFolder.gif"));
		table.put("Tree.leafIcon", UIRES.get("laf.file.gif"));

		table.put("FileView.directoryIcon", UIRES.get("laf.directory.gif"));
		table.put("FileView.fileIcon", UIRES.get("laf.file.gif"));

		table.put("OptionPane.warningIcon", UIRES.get("laf.warning"));
		table.put("OptionPane.errorIcon", UIRES.get("laf.error"));
		table.put("OptionPane.questionIcon", UIRES.get("laf.question"));
		table.put("OptionPane.informationIcon", UIRES.get("laf.info"));

		table.put("MenuItem.acceleratorForeground", theme.getAltForegroundColor());
	}

	@Override public String getName() {
		return "MCreator";
	}

	@Override protected ColorUIResource getPrimary1() {
		return new ColorUIResource(theme.getBackgroundColor());
	}

	@Override protected ColorUIResource getPrimary2() {
		return new ColorUIResource(theme.getInterfaceAccentColor());
	}

	@Override protected ColorUIResource getPrimary3() {
		return new ColorUIResource(theme.getInterfaceAccentColor());
	}

	@Override protected ColorUIResource getSecondary1() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override protected ColorUIResource getSecondary2() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override protected ColorUIResource getSecondary3() {
		return new ColorUIResource(0xeeeeee);
	}

	@Override public ColorUIResource getControl() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override public ColorUIResource getControlHighlight() {
		return new ColorUIResource(theme.getAltBackgroundColor());
	}

	@Override public ColorUIResource getPrimaryControlHighlight() {
		return new ColorUIResource(theme.getAltForegroundColor());
	}

	@Override public FontUIResource getControlTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getSystemTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getUserTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getMenuTextFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getWindowTitleFont() {
		return new FontUIResource(theme.getFont());
	}

	@Override public FontUIResource getSubTextFont() {
		return new FontUIResource(theme.getFont());
	}

}
