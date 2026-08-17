/*
 * MCreator (https://mcreator.net/)
 * Copyright (C) 2012-2020, Pylo
 * Copyright (C) 2020-2026, Pylo, opensource contributors
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

package net.mcreator.ui.mcp;

import net.mcreator.io.mcp.McpInstallHelper;
import net.mcreator.io.mcp.transport.HttpMcpTransport;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.init.L10N;
import net.mcreator.ui.laf.themes.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MCreatorMcpManager implements Closeable {

	private static final Logger LOG = LogManager.getLogger(MCreatorMcp.class);

	@Nullable private final MCreatorMcp mcreatorMcp;

	private final Map<MCreator, AbstractButton> mcpButtons = new HashMap<>();
	private final ButtonGroup mcpButtonGroup = new ButtonGroup();

	public MCreatorMcpManager() {
		if (PreferencesManager.PREFERENCES.integrations.mcpEnable.get()) {
			this.mcreatorMcp = new MCreatorMcp(
					new HttpMcpTransport(PreferencesManager.PREFERENCES.integrations.mcpPort.get()),
					this::getCurrentMCreator);

			try {
				if (PreferencesManager.PREFERENCES.integrations.mcpAutoInstall.get()) {
					McpInstallHelper.installToAll("MCreator",
							"http://127.0.0.1:" + PreferencesManager.PREFERENCES.integrations.mcpPort.get() + "/mcp");
				}

				this.mcreatorMcp.start();
				LOG.debug("MCP server started at port {}", PreferencesManager.PREFERENCES.integrations.mcpPort.get());
			} catch (IOException e) {
				LOG.warn("Failed to start MCP server", e);
			}
		} else {
			this.mcreatorMcp = null;
		}
	}

	public void registerMCreator(MCreator mcreator) {
		if (mcreatorMcp == null)
			return;

		JToggleButton mcpButton = new JToggleButton("MCP");
		mcpButton.setToolTipText(L10N.t("mcp.button.tooltip"));
		mcpButton.setMargin(new Insets(0, 3, 0, 3));
		mcpButton.addItemListener(_ -> mcpButton.setForeground(mcpButton.isSelected() ?
				Theme.current().getInterfaceAccentColor() :
				Theme.current().getAltBackgroundColor()));
		mcpButton.setContentAreaFilled(false);
		mcpButton.setForeground(Theme.current().getAltBackgroundColor());

		mcpButtonGroup.add(mcpButton);
		mcpButtons.put(mcreator, mcpButton);

		mcreator.getStatusBar().addLeftComponent(mcpButton);

		// select this MCreator as current if no other MCreator is selected yet
		if (getCurrentMCreator() == null) {
			mcpButton.setSelected(true);
		}
	}

	public void unregisterMCreator(MCreator mcreator) {
		if (mcreatorMcp == null)
			return;

		AbstractButton mcpButton = mcpButtons.remove(mcreator);
		if (mcpButton != null) {
			mcpButtonGroup.remove(mcpButton);
		}

		if (getCurrentMCreator() == null && !mcpButtons.isEmpty()) {
			// select the first MCreator as current if no other MCreator is selected yet
			mcpButtons.values().iterator().next().setSelected(true);
		}
	}

	@Nullable public MCreatorMcp getMCP() {
		return mcreatorMcp;
	}

	@Nullable private MCreator getCurrentMCreator() {
		if (mcreatorMcp == null)
			return null;

		for (Map.Entry<MCreator, AbstractButton> entry : mcpButtons.entrySet()) {
			if (entry.getValue().isSelected()) {
				return entry.getKey();
			}
		}
		return null;
	}

	@Override public void close() throws IOException {
		if (mcreatorMcp != null) {
			LOG.debug("Stopping MCP server...");
			mcreatorMcp.close();
		}
	}

}
