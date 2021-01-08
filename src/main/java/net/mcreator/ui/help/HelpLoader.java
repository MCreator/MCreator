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

import net.mcreator.io.FileIO;
import net.mcreator.plugin.PluginLoader;
import net.mcreator.ui.init.L10N;
import org.apache.commons.io.FilenameUtils;
import org.commonmark.Extension;
import org.commonmark.ext.autolink.AutolinkExtension;
import org.commonmark.ext.gfm.tables.TablesExtension;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class HelpLoader {

	private static final Map<String, String> DEFAULT_CACHE = new HashMap<>();
	private static final Map<String, String> LOCALIZED_CACHE = new HashMap<>();

	private static Parser parser;
	private static HtmlRenderer renderer;

	public static void preloadCache() {
		PluginLoader.INSTANCE.getResources("help.default", Pattern.compile("^[^$].*\\.md")).forEach(e -> DEFAULT_CACHE
				.put(FilenameUtils.removeExtension(e.replaceFirst("help/default/", "")),
						FileIO.readResourceToString(PluginLoader.INSTANCE, e, StandardCharsets.UTF_8)));

		PluginLoader.INSTANCE.getResources("help." + L10N.getLocaleString(), Pattern.compile("^[^$].*\\.md")).forEach(
				e -> LOCALIZED_CACHE
						.put(FilenameUtils.removeExtension(e.replaceFirst("help/" + L10N.getLocaleString() + "/", "")),
								FileIO.readResourceToString(PluginLoader.INSTANCE, e, StandardCharsets.UTF_8)));

		List<Extension> extensionList = Arrays.asList(TablesExtension.create(), AutolinkExtension.create());
		parser = Parser.builder().extensions(extensionList).build();
		renderer = HtmlRenderer.builder().extensions(extensionList).build();
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
				if (getFromCache(helpContext.getEntry()) != null) {
					helpString.append(renderer.render(parser.parse(getFromCache(helpContext.getEntry()))));
				} else {
					helpString.append(L10N.t("help.loader.no_help_entry")).append(helpContext.getEntry())
							.append("</tt> yet.<br>");
				}

				if (uri != null && helpContext.getContextName() != null) {
					helpString.append(L10N.t("help.loader.learn_about")).append(uri.toString()).append("\">")
							.append(helpContext.getContextName()).append(L10N.t("help.loader.wiki"));
				}

				return helpString.toString();
			} else if (uri != null && helpContext.getContextName() != null) {
				return L10N.t("help.loader.no_entry_learn") + uri.toString() + "\">"
						+ helpContext.getContextName() + L10N.t("help.loader.wiki");
			}
		}

		return "<html><body>No help was found for the given help context.";
	}

}
