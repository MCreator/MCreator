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

package net.mcreator.ui.help;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import net.mcreator.generator.template.base.DefaultFreemarkerConfiguration;
import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.FilenameUtilsPatched;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class HelpLoader {

	private static final Map<String, String> DEFAULT_CACHE = new HashMap<>();
	private static final Map<String, String> LOCALIZED_CACHE = new HashMap<>();

	private static Parser parser;
	private static HtmlRenderer renderer;

	private static final DefaultFreemarkerConfiguration configuration = new DefaultFreemarkerConfiguration();

	public static void preloadCache() {
		PluginLoader.INSTANCE.getResources("help.default", Pattern.compile("^[^$].*\\.md")).forEach(
				e -> DEFAULT_CACHE.put(FilenameUtilsPatched.removeExtension(e.replaceFirst("help/default/", "")),
						FileIO.readResourceToString(PluginLoader.INSTANCE, e, StandardCharsets.UTF_8)));

		PluginLoader.INSTANCE.getResources("help." + L10N.getLocaleString(), Pattern.compile("^[^$].*\\.md")).forEach(
				e -> LOCALIZED_CACHE.put(FilenameUtilsPatched.removeExtension(
								e.replaceFirst("help/" + L10N.getLocaleString() + "/", "")),
						FileIO.readResourceToString(PluginLoader.INSTANCE, e, StandardCharsets.UTF_8)));

		List<Extension> extensionList = Arrays.asList(TablesExtension.create(), AutolinkExtension.create());
		parser = Parser.builder().extensions(extensionList).build();
		renderer = HtmlRenderer.builder().extensions(extensionList).build();
	}

	public static int getCoverageForLocale(Locale locale) {
		int langcount = PluginLoader.INSTANCE.getResources("help." + locale.toString(), Pattern.compile("^[^$].*\\.md"))
				.size();

		if (langcount == 0)
			return 0;

		return Math.min(100, (int) Math.ceil(langcount * 100d / (double) DEFAULT_CACHE.size()));
	}

	@Nullable private static String getFromCache(String key) {
		if (LOCALIZED_CACHE.containsKey(key))
			return LOCALIZED_CACHE.get(key);

		return DEFAULT_CACHE.get(key);
	}

	public static boolean hasFullHelp(IHelpContext helpContext) {
		return helpContext != null && helpContext.getEntry() != null && getFromCache(helpContext.getEntry()) != null;
	}

	public static String loadHelpFor(IHelpContext helpContext) {
		if (helpContext != null) {
			URI uri = null;
			try {
				uri = helpContext.getContextURL();
			} catch (URISyntaxException ignored) {
			}

			StringBuilder helpString = new StringBuilder(
					"<html><head><style>table{ border-collapse: collapse; border-spacing: 0; } "
							+ "th { border: 1px solid #a0a0a0; } td { border: 1px solid #a0a0a0; } </style></head><body>");

			if (helpContext.getEntry() != null) {
				String helpText = getFromCache(helpContext.getEntry());
				if (helpText != null) {
					if ((helpText.contains("${") || helpText.contains("<#"))
							&& helpContext instanceof ModElementHelpContext meHelpContext) {
						try {
							Map<String, Object> dataModel = new HashMap<>();
							dataModel.put("data", meHelpContext.getModElementFromGUI());
							dataModel.put("registryname",
									meHelpContext.getModElementFromGUI().getModElement().getRegistryName());
							dataModel.put("name", meHelpContext.getModElementFromGUI().getModElement().getName());
							dataModel.put("elementtype",
									meHelpContext.getModElementFromGUI().getModElement().getType().getReadableName());
							dataModel.put("l10n", new L10N());

							if (meHelpContext.getModElementFromGUI().getModElement().getGenerator() != null)
								dataModel.putAll(meHelpContext.getModElementFromGUI().getModElement().getGenerator()
										.getBaseDataModelProvider().provide());

							Template freemarkerTemplate = new Template(helpContext.getEntry(),
									new StringReader(helpText), configuration);
							StringWriter stringWriter = new StringWriter();
							freemarkerTemplate.process(dataModel, stringWriter, configuration.getBeansWrapper());

							helpString.append(renderer.render(parser.parse(stringWriter.getBuffer().toString())));
						} catch (TemplateException | IOException e) {
							helpString.append(renderer.render(parser.parse(helpText)));
						}
					} else {
						helpString.append(renderer.render(parser.parse(helpText)));
					}
				} else {
					helpString.append(L10N.t("help_loader.no_help_entry", helpContext.getEntry()));
				}

				if (uri != null && helpContext.getContextName() != null) {
					helpString.append(L10N.t("help_loader.learn_about", uri.toString(), helpContext.getContextName()));
				}

				return helpString.toString();
			} else if (uri != null && helpContext.getContextName() != null) {
				return L10N.t("help_loader.no_entry_learn_more", uri.toString(), helpContext.getContextName());
			}
		}

		return L10N.t("help_loader.no_help_found");
	}

}
