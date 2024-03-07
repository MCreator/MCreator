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

package net.mcreator.ui.laf.themes;

import net.mcreator.plugin.PluginLoader;
import net.mcreator.preferences.data.PreferencesData;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.init.UIRES;
import net.mcreator.ui.laf.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * <p>A Theme can change images MCreator will use and redefine the colors and the style
 * of {@link net.mcreator.ui.blockly.BlocklyPanel} and {@link net.mcreator.ui.ide.RSyntaxTextAreaStyler} by creating a new {@link ColorScheme}</p>.
 */
@SuppressWarnings("unused") public class Theme {

	private static final Logger LOG = LogManager.getLogger(Theme.class);

	public static Theme current() {
		return ThemeManager.CURRENT_THEME;
	}

	protected String id;
	private String name;

	@Nullable private String description;
	@Nullable private String version;
	@Nullable private String credits;
	@Nullable private String defaultFont;
	private boolean useDefaultFontForSecondary;
	private int fontSize;

	@Nullable private ColorScheme colorScheme;

	private transient ImageIcon icon;

	private transient Font defaultThemeFont;
	private transient Font secondaryFont;
	private transient Font consoleFont;

	protected Theme init() {
		if (colorScheme != null)
			colorScheme.init();

		try {
			defaultThemeFont = new Font(defaultFont != null ? defaultFont : "Sans-Serif", Font.PLAIN,
					this.getFontSize());
			secondaryFont = defaultThemeFont;

			String lang = L10N.getLocale().getLanguage();
			if (!L10N.SYSTEM_FONT_LANGUAGES.contains(lang) && !useDefaultFontForSecondary) {
				InputStream secondaryFontStream = PluginLoader.INSTANCE.getResourceAsStream(
						"themes/" + id + "/fonts/secondary_font.ttf");
				if (secondaryFontStream != null) { // Font loaded from a file in the theme
					secondaryFont = Font.createFont(Font.TRUETYPE_FONT, secondaryFontStream);
				} else { // Default secondary front (from the default_dark theme)
					secondaryFont = Font.createFont(Font.TRUETYPE_FONT,
							PluginLoader.INSTANCE.getResourceAsStream("themes/default_dark/fonts/secondary_font.ttf"));
					LOG.info("Main font from default_dark will be used.");
				}
			}

			InputStream consoleFontStream = PluginLoader.INSTANCE.getResourceAsStream(
					"themes/" + id + "/fonts/console_font.ttf");
			if (consoleFontStream != null) {
				consoleFont = Font.createFont(Font.TRUETYPE_FONT, consoleFontStream);
			} else {
				// Default main front (from the default_dark theme)
				consoleFont = Font.createFont(Font.TRUETYPE_FONT,
						PluginLoader.INSTANCE.getResourceAsStream("themes/default_dark/fonts/console_font.ttf"));
				LOG.info("Console font from default_dark will be used.");
			}
		} catch (NullPointerException | FontFormatException | IOException e2) {
			LOG.info("Failed to init MCreator Theme! Error " + e2.getMessage());
		}

		return this;
	}

	public void applyUIDefaultsOverrides(UIDefaults table) {
		Set<Object> keySet = table.keySet();
		for (Object key : keySet) {
			if (key == null)
				continue;
			if (key.toString().toLowerCase(Locale.ENGLISH).contains("font")) {
				table.put(key, getSecondaryFont().deriveFont((float) getFontSize()));
			} else if (key.toString().toLowerCase(Locale.ENGLISH).contains("bordercolor")) {
				table.put(key, getInterfaceAccentColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".background")) {
				table.put(key, getBackgroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".foreground")) {
				table.put(key, getForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".inactiveforeground")) {
				table.put(key, getAltForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledbackground")) {
				table.put(key, getBackgroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".disabledforeground")) {
				table.put(key, getAltForegroundColor());
			} else if (key.toString().toLowerCase(Locale.ENGLISH).endsWith(".caretforeground")) {
				table.put(key, getForegroundColor());
			}
		}

		table.put("TabbedPane.contentOpaque", false);

		table.put("Tree.rendererFillBackground", false);

		table.put("TitledBorder.titleColor", getForegroundColor());

		table.put("SplitPane.dividerFocusColor", getAltBackgroundColor());
		table.put("SplitPane.darkShadow", getAltBackgroundColor());
		table.put("SplitPane.shadow", getAltBackgroundColor());
		table.put("SplitPaneDivider.draggingColor", getInterfaceAccentColor());

		table.put("OptionPane.messageForeground", getForegroundColor());

		table.put("Label.foreground", getForegroundColor());
		table.put("Label.disabledForeground", getForegroundColor());
		table.put("Label.inactiveforeground", getForegroundColor());
		table.put("Label.textForeground", getForegroundColor());

		table.put("Button.toolBarBorderBackground", getForegroundColor());
		table.put("Button.disabledToolBarBorderBackground", getAltBackgroundColor());
		table.put("ToolBar.rolloverBorder",
				BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(getBackgroundColor(), 1),
						BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(getAltBackgroundColor(), 1),
								BorderFactory.createLineBorder(getBackgroundColor(), 3))));

		table.put("ScrollBarUI", SlickDarkScrollBarUI.class.getName());
		table.put("SpinnerUI", DarkSpinnerUI.class.getName());
		table.put("SplitPaneUI", DarkSplitPaneUI.class.getName());
		table.put("SliderUI", DarkSliderUI.class.getName());
		table.put("ComboBoxUI", DarkComboBoxUI.class.getName());

		table.put("Menu.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));
		table.put("MenuItem.border", BorderFactory.createEmptyBorder(3, 4, 3, 4));

		table.put("PopupMenu.border", BorderFactory.createLineBorder(getAltBackgroundColor()));

		table.put("Separator.foreground", getAltBackgroundColor());
		table.put("Separator.background", getBackgroundColor());

		table.put("Menu.foreground", getForegroundColor());
		table.put("MenuItem.foreground", getForegroundColor());

		table.put("ComboBox.foreground", getForegroundColor());
		table.put("ComboBox.background", getAltBackgroundColor());
		table.put("ComboBox.disabledForeground", getAltForegroundColor());

		table.put("Spinner.foreground", getForegroundColor());
		table.put("Spinner.background", getAltBackgroundColor());

		table.put("FormattedTextField.foreground", getForegroundColor());
		table.put("FormattedTextField.inactiveForeground", getAltForegroundColor());
		table.put("FormattedTextField.background", getAltBackgroundColor());
		table.put("FormattedTextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("TextField.foreground", getForegroundColor());
		table.put("TextField.inactiveForeground", getAltForegroundColor());
		table.put("TextField.background", getAltBackgroundColor());
		table.put("TextField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("PasswordField.foreground", getForegroundColor());
		table.put("PasswordField.inactiveForeground", getAltForegroundColor());
		table.put("PasswordField.background", getAltBackgroundColor());
		table.put("PasswordField.border", BorderFactory.createEmptyBorder(2, 5, 2, 5));

		table.put("ComboBox.border", null);

		java.util.List<?> buttonGradient = Arrays.asList(0f, 0f, new ColorUIResource(getForegroundColor()),
				new ColorUIResource(getForegroundColor()), new ColorUIResource(getForegroundColor()));

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

		List<?> sliderGradient = Arrays.asList(0f, 0f, new ColorUIResource(getBackgroundColor()),
				new ColorUIResource(getBackgroundColor()), new ColorUIResource(getBackgroundColor()));

		table.put("Slider.altTrackColor", new ColorUIResource(getBackgroundColor()));
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

		table.put("TabbedPane.contentAreaColor", getBackgroundColor());
		table.put("TabbedPane.contentBorderInsets", new Insets(4, 2, 3, 3));
		table.put("TabbedPane.selected", getBackgroundColor());
		table.put("TabbedPane.tabAreaBackground", getAltBackgroundColor());
		table.put("TabbedPane.tabAreaInsets", new Insets(2, 2, 0, 6));
		table.put("TabbedPane.unselectedBackground", getBackgroundColor());

		table.put("ToolTip.border", BorderFactory.createLineBorder(getForegroundColor()));
		table.put("ToolTip.foreground", getForegroundColor());
		table.put("ToolTip.background", getBackgroundColor());

		table.put("ScrollBar.width", 7);

		table.put("SplitPane.border", BorderFactory.createEmptyBorder());

		table.put("FileChooser.homeFolderIcon", UIRES.get("laf.homeFolder"));
		table.put("FileChooser.newFolderIcon", UIRES.get("laf.newFolder"));
		table.put("FileChooser.upFolderIcon", UIRES.get("laf.upFolder"));
		table.put("FileChooser.computerIcon", UIRES.get("laf.computer"));
		table.put("FileChooser.hardDriveIcon", UIRES.get("laf.hardDrive"));
		table.put("FileChooser.floppyDriveIcon", UIRES.get("laf.floppy"));
		table.put("FileChooser.closedIcon", UIRES.get("laf.newFolder"));

		table.put("Tree.closedIcon", UIRES.get("laf.newFolder"));
		table.put("Tree.openIcon", UIRES.get("laf.upFolder"));
		table.put("Tree.leafIcon", UIRES.get("laf.file"));

		table.put("FileView.directoryIcon", UIRES.get("laf.directory"));
		table.put("FileView.fileIcon", UIRES.get("laf.file"));

		table.put("OptionPane.warningIcon", UIRES.get("laf.warning"));
		table.put("OptionPane.errorIcon", UIRES.get("laf.error"));
		table.put("OptionPane.questionIcon", UIRES.get("laf.question"));
		table.put("OptionPane.informationIcon", UIRES.get("laf.info"));

		table.put("MenuItem.acceleratorForeground", getAltForegroundColor());
	}

	/**
	 * The ID is the theme's registry name. It is used to differentiate each theme in the code.
	 * This ID is also the main folder's name of the theme.
	 *
	 * @return <p>The theme's ID</p>
	 */
	public String getID() {
		return id;
	}

	/**
	 * @return <p>Its displayed name</p>
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return <p>A description displayed in the {@link net.mcreator.ui.dialogs.preferences.ThemesPanel} if provided.</p>
	 */
	public String getDescription() {
		// Description inside the JSON file
		if (description != null)
			return description;
			// Localized description
		else if (!L10N.t("theme." + id + ".description").equals("theme." + id + ".description"))
			return L10N.t("theme." + id + ".description");
			// No description
		else
			return "";
	}

	/**
	 * @return <p>A String with optional credits to give to someone.</p>
	 */
	@Nullable public String getCredits() {
		return credits;
	}

	/**
	 * @return <p>The theme's version if provided</p>
	 */
	@Nullable public String getVersion() {
		return version;
	}

	/**
	 * <p>The main font size changes the size of the text for the main font. Usually, this parameter should not be changed except if the font is too big or too small with the default value.</p>
	 *
	 * @return <p>The main font size</p>
	 */
	public int getFontSize() {
		if (fontSize != 0)
			return fontSize;
		else
			return 12;
	}

	public Font getFont() {
		return defaultThemeFont;
	}

	public Font getSecondaryFont() {
		return secondaryFont;
	}

	public Font getConsoleFont() {
		return consoleFont;
	}

	/**
	 * <p>This methods gets the {@link ColorScheme} to use with the theme</p>
	 *
	 * @return Returns the {@link ColorScheme} of the Theme if one is defined. If the Theme does not create a new {@link ColorScheme}, the Dark's theme {@link ColorScheme} will be used.
	 */
	public ColorScheme getColorScheme() {
		if (colorScheme != null)
			return colorScheme;
		else
			return ThemeManager.getTheme("default_dark").getColorScheme();
	}

	/**
	 * This icon is only with {@link net.mcreator.ui.dialogs.preferences.ThemesPanel}.
	 *
	 * @return <p>An {@link ImageIcon} representing the plugin.</p>
	 */
	public ImageIcon getIcon() {
		return icon;
	}

	/**
	 * <p>To be detected, the name of the image file needs to be "icon.png" located into the main folder.</p>
	 *
	 * @param icon <p>An {@link ImageIcon} to display in {@link net.mcreator.ui.dialogs.preferences.ThemesPanel}</p>
	 */
	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	@Override public String toString() {
		return getID() + ": " + getName();
	}

	// Color scheme getters below (to keep code shorter)

	/**
	 * @return Background of UI panels
	 */
	public Color getBackgroundColor() {
		return getColorScheme().getBackgroundColor();
	}

	/**
	 * @return Background of components (e.g. text fields, checkboxes and sound selectors)
	 */
	public Color getAltBackgroundColor() {
		return getColorScheme().getAltBackgroundColor();
	}

	/**
	 * @return Second background color used (e.g. workspace background)
	 */
	public Color getSecondAltBackgroundColor() {
		return getColorScheme().getSecondAltBackgroundColor();
	}

	/**
	 * @return <p>Secondary text color </p>
	 */
	public Color getAltForegroundColor() {
		return getColorScheme().getAltForegroundColor();
	}

	/**
	 * @return <p>Color used for most of texts </p>
	 */
	public Color getForegroundColor() {
		return getColorScheme().getForegroundColor();
	}

	/**
	 * @return <p>Returns the interfaceAccentColor if defined by theme, otherwise the one defined by the user in {@link PreferencesData}</p>
	 */
	public Color getInterfaceAccentColor() {
		return getColorScheme().getInterfaceAccentColor();
	}

}
