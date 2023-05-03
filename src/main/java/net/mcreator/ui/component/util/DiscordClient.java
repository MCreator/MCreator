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

package net.mcreator.ui.component.util;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.mcreator.preferences.PreferencesManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.Closeable;
import java.util.Timer;
import java.util.TimerTask;

public class DiscordClient implements Closeable {

	private static final Logger LOG = LogManager.getLogger("Discord");

	private DiscordRPC discordRpc;
	private long startTime;

	private final Timer timer = new Timer();

	public DiscordClient() {
		if (!PreferencesManager.PREFERENCES.ui.discordRichPresenceEnable)
			return;

		try {
			discordRpc = DiscordRPC.INSTANCE;
			startTime = System.currentTimeMillis() / 1000L;

			DiscordEventHandlers handlers = new DiscordEventHandlers();
			handlers.ready = (user) -> LOG.debug("Connected with " + user.username);
			handlers.disconnected = (errorCode, message) -> LOG.debug("Disconnected: " + errorCode + " - " + message);
			handlers.errored = (errorCode, message) -> LOG.warn(errorCode + " - " + message);
			discordRpc.Discord_Initialize("712264497787568171", handlers, true, null);
			timer.schedule(new TimerTask() {
				@Override public void run() {
					discordRpc.Discord_RunCallbacks();
				}
			}, 0, 5000);
		} catch (Exception e) {
			LOG.warn("Failed to init", e);
		}
	}

	public void updatePresence(String state, String details) {
		this.updatePresence(state, details, "");
	}

	public void updatePresence(String state, String details, String smallImage) {
		if (!PreferencesManager.PREFERENCES.ui.discordRichPresenceEnable)
			return;

		new Thread(() -> {
			try {
				DiscordRichPresence discordRichPresence = new DiscordRichPresence();
				discordRichPresence.state = state;
				discordRichPresence.details = details;
				discordRichPresence.startTimestamp = startTime;
				discordRichPresence.smallImageKey = smallImage;
				discordRichPresence.largeImageKey = "discord";
				discordRpc.Discord_UpdatePresence(discordRichPresence);
			} catch (Exception e) {
				LOG.warn("Failed to update presence", e);
			}
		}).start();
	}

	@Override public void close() {
		if (!PreferencesManager.PREFERENCES.ui.discordRichPresenceEnable)
			return;

		try {
			timer.cancel();
			discordRpc.Discord_Shutdown();
		} catch (Exception e) {
			LOG.warn("Failed to close properly", e);
		}
	}
}
