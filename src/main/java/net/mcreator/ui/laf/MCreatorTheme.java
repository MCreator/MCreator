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

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.themes.ColorTheme;
import net.mcreator.themes.ThemeLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.metal.OceanTheme;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MCreatorTheme extends OceanTheme {

	private static final Logger LOG = LogManager.getLogger("Theme");

	private static final List<String> NON_ROBOTO_LANGUAGES = Arrays.asList("zh", "ja", "ko", "th", "hi", "he", "iw");

	public static final Color MAIN_TINT_DEFAULT = new Color(0x93c54b);
	private Color MAIN_TINT = MAIN_TINT_DEFAULT;
	private final ColorTheme theme;

	public static Font light_font;
	public static Font console_font;

	private static Font default_font;

	public MCreatorTheme(ColorTheme theme) {
		this.theme = theme;
		try {
			default_font = new Font("Sans-Serif", Font.PLAIN, 13);

			String lang = L10N.getLocale().getLanguage();
			if (NON_ROBOTO_LANGUAGES.contains(lang))
				light_font = default_font;
			else
				light_font = Font
						.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/roboto_light.ttf"));

			console_font = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/fonts/notomono.ttf"));
			MAIN_TINT = PreferencesManager.PREFERENCES.ui.interfaceAccentColor;
		} catch (FontFormatException | IOException e2) {
			LOG.info("Failed to init MCreator Theme! Error " + e2.getMessage());
		}
	}

	public Color getMainTint() {
		return MAIN_TINT;
	}

	private @NotNull Color getBlackAccent() {
		return theme.getBlackAccent().getRGB();
	}

	private @NotNull Color getDarkAccent() {
		return theme.getDarkAccent().getRGB();
	}

	private @NotNull Color getLightAccent() {
		return theme.getLightAccent().getRGB();
	}

	private @NotNull Color getGrayColor() {
		return theme.getGrayColor().getRGB();
	}

	protected @NotNull Color getBrightColor() {
		return theme.getBrightColor().getRGB();
	}

	private @NotNull String getBlocklyCSSName() {
		return "blockly_" + theme.getBlocklyCSSFile() + ".css";
	}

	private @NotNull String getCodeEditorXML() {
		return "codeeditor_" + theme.getCodeEditorFile() + ".xml";
	}

	protected void initMCreatorThemeColors(UIDefaults table) {
		table.put("MCreatorLAF.BLACK_ACCENT", getBlackAccent());
		table.put("MCreatorLAF.DARK_ACCENT", getDarkAccent());
		table.put("MCreatorLAF.LIGHT_ACCENT", getLightAccent());
		table.put("MCreatorLAF.GRAY_COLOR", getGrayColor());
		table.put("MCreatorLAF.BRIGHT_COLOR", getBrightColor());
		table.put("MCreatorLAF.MAIN_TINT", MAIN_TINT);

		String path = "themes/" + theme.getTheme().getID() + "/colors/";
		table.put("MCreatorLAF.BLOCKLY_CSS", PluginLoader.INSTANCE.getResource(path + getBlocklyCSSName()));
		table.put("MCreatorLAF.CODE_EDITOR_XML", PluginLoader.INSTANCE.getResource(path + getCodeEditorXML()));
	}

	@Override public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);

		initMCreatorThemeColors(table);

		Set<Object> keySet = table.keySet();
		for (Object key : keySet) {
			if (key == null)
				continue;
			if (key.toString().toLowerCase(Locale.ENGLISH).contains("font")) {
				table.put(key, light_font.deriveFont(12.0f));
			} else if (key.toString().toLowerCase(Locale.ENGLISH).contains("bordercolor")) {
				table.put(key, MAIN_TINT);
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".background")) {
				table.put(key, getDarkAccent());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".foreground")) {
				table.put(key, getBrightColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".inactiveforeground")) {
				table.put(key, getGrayColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledbackground")) {
				table.put(key, getDarkAccent());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledforeground")) {
				table.put(key, getGrayColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".caretforeground")) {
				table.put(key, getBrightColor());
			}
		}

		table.put("TabbedPane.contentOpaque", false);

		table.put("Tree.rendererFillBackground", false);

		table.put("TitledBorder.titleColor", getBrightColor());

		table.put("SplitPane.dividerFocusColor", getLightAccent());
		table.put("SplitPane.darkShadow", getLightAccent());
		table.put("SplitPane.shadow", getLightAccent());
		table.put("SplitPaneDivider.draggingColor", MAIN_TINT);

		table.put("OptionPane.messageForeground", getBrightColor());

		table.put("Label.foreground", getBrightColor());
		table.put("Label.disabledForeground", getBrightColor());
		table.put("Label.inactiveforeground", getBrightColor());
		table.put("Label.textForeground", getBrightColor());

		table.put("Button.toolBarBorderBackground", getBrightColor());
		table.put("Button.disabledToolBarBorderBackground", getLightAccent());
		table.put("ToolBar.rolloverBorder", BorderFactory
				.createCompoundBorder(BorderFactory.createLineBorder(getDarkAccent(), 1), BorderFactory
						.createCompoundBorder(BorderFactory.createLineBorder(getLightAccent(), 1),
								BorderFactory.createLineBorder(getDarkAccent(), 3))));

		table.put("ScrollBarUI", SlickDarkScrollBarUI.class.getName());
		table.put("SpinnerUI", DarkSpinnerUI.class.getName());
		table.put("SplitPaneUI", DarkSplitPaneUI.class.getName());
		table.put("SliderUI", DarkSliderUI.class.getName());
		table.put("ComboBoxUI", DarkComboBoxUI.class.getName());

		table.put("Menu.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));
		table.put("MenuItem.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));

		table.put("PopupMenu.border", BorderFactory.createLineBorder(getLightAccent()));

		table.put("Separator.foreground", getLightAccent());
		table.put("Separator.background", getDarkAccent());

		table.put("Menu.foreground", getBrightColor());
		table.put("MenuItem.foreground", getBrightColor());

		table.put("ComboBox.foreground", getBrightColor());
		table.put("ComboBox.background", getLightAccent());
		table.put("ComboBox.disabledForeground", getGrayColor());

		table.put("Spinner.foreground", getBrightColor());
		table.put("Spinner.background", getLightAccent());

		table.put("FormattedTextField.foreground", getBrightColor());
		table.put("FormattedTextField.inactiveForeground", getGrayColor());
		table.put("FormattedTextField.background", getLightAccent());
		table.put("FormattedTextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("TextField.foreground", getBrightColor());
		table.put("TextField.inactiveForeground", getGrayColor());
		table.put("TextField.background", getLightAccent());
		table.put("TextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("PasswordField.foreground", getBrightColor());
		table.put("PasswordField.inactiveForeground", getGrayColor());
		table.put("PasswordField.background", getLightAccent());
		table.put("PasswordField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("ComboBox.border", null);

		List<?> buttonGradient = Arrays
				.asList(0f, 0f, new ColorUIResource(getBrightColor()), new ColorUIResource(getBrightColor()),
						new ColorUIResource(getBrightColor()));

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

		List<?> sliderGradient = Arrays
				.asList(0f, 0f, new ColorUIResource(getDarkAccent()), new ColorUIResource(getDarkAccent()),
						new ColorUIResource(getDarkAccent()));

		table.put("Slider.altTrackColor", new ColorUIResource(getDarkAccent()));
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

		table.put("TabbedPane.contentAreaColor", getDarkAccent());
		table.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3));
		table.put("TabbedPane.selected", getLightAccent());
		table.put("TabbedPane.tabAreaBackground", getLightAccent());
		table.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6));
		table.put("TabbedPane.unselectedBackground", getDarkAccent());

		table.put("ToolTip.border", BorderFactory.createLineBorder(getBrightColor()));
		table.put("ToolTip.foreground", getBrightColor());
		table.put("ToolTip.background", getDarkAccent());

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

		table.put("MenuItem.acceleratorForeground", getGrayColor());
	}

	@Override public String getName() {
		return "MCreator";
	}

	@Override protected ColorUIResource getPrimary1() {
		return new ColorUIResource(getDarkAccent());
	}

	@Override protected ColorUIResource getPrimary2() {
		return new ColorUIResource(MAIN_TINT);
	}

	@Override protected ColorUIResource getPrimary3() {
		return new ColorUIResource(MAIN_TINT);
	}

	@Override protected ColorUIResource getSecondary1() {
		return new ColorUIResource(getLightAccent());
	}

	@Override protected ColorUIResource getSecondary2() {
		return new ColorUIResource(getLightAccent());
	}

	@Override protected ColorUIResource getSecondary3() {
		return new ColorUIResource(0xeeeeee);
	}

	@Override public ColorUIResource getControl() {
		return new ColorUIResource(getLightAccent());
	}

	@Override public ColorUIResource getControlHighlight() {
		return new ColorUIResource(getLightAccent());
	}

	@Override public ColorUIResource getPrimaryControlHighlight() {
		return new ColorUIResource(getGrayColor());
	}

	@Override public FontUIResource getControlTextFont() {
		return new FontUIResource(default_font);
	}

	@Override public FontUIResource getSystemTextFont() {
		return new FontUIResource(default_font);
	}

	@Override public FontUIResource getUserTextFont() {
		return new FontUIResource(default_font);
	}

	@Override public FontUIResource getMenuTextFont() {
		return new FontUIResource(default_font);
	}

	@Override public FontUIResource getWindowTitleFont() {
		return new FontUIResource(default_font);
	}

	@Override public FontUIResource getSubTextFont() {
		return new FontUIResource(default_font);
	}

}
