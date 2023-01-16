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

package net.mcreator.io.net.analytics;

import net.mcreator.Launcher;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.init.L10N;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class GoogleAnalytics {

	private static final Logger LOG = LogManager.getLogger("GA");

	private final String clientUUID;
	private final DeviceInfo deviceInfo;

	// Session info
	private final long sessionID;
	private boolean newSession = true;

	// Page info
	// TODO: add page info

	public GoogleAnalytics(DeviceInfo deviceInfo, String clientUUID) {
		this.clientUUID = clientUUID;
		this.deviceInfo = deviceInfo;
		this.sessionID = System.currentTimeMillis();
	}

	private String getGATrackURL(Map<String, Object> payload) {
		StringBuilder actionRequestURL = new StringBuilder("https://www.google-analytics.com/g/collect?v=2");

		payload.put("tid", "G-V6EPB4SPL8");
		payload.put("cid", clientUUID);
		payload.put("dh", "app.mcreator.net");
		payload.put("sr", deviceInfo.getScreenWidth() + "x" + deviceInfo.getScreenHeight());
		payload.put("ul", L10N.getLocaleString().toLowerCase(Locale.ENGLISH).replace("_", "-"));
		payload.put("sid", sessionID);
		payload.put("_nsi", newSession ? "1" : "0"); // new session ID
		payload.put("_ss", newSession ? "1" : "0"); // session start

		// https://www.thyngster.com/ga4-measurement-protocol-cheatsheet/

		// dl - http://localhost/test2.html - document location
		// dt - Title - document title
		// _p - random page load hash
		// dr - document referrer - previous page

		// uafvl - full version of MCreator
		// uam - user agent model - version of mcreator
		// uap - user agent platform: windows, macos, linux

		// up.* - user parameter string
		// upn.* - user parameter number

		for (Map.Entry<String, Object> entry : payload.entrySet()) {
			if (entry.getValue() != null)
				actionRequestURL.append("&").append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}

		// Once session is started, it is not new anymore
		newSession = false;

		return actionRequestURL.toString();
	}

	private void processRequestURL(String requesturl) {
		// TODO: remove me
		System.err.println("DEMO: " + requesturl);

		if (MCreatorApplication.isInternet) {
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(requesturl).openConnection();
				conn.setInstanceFollowRedirects(true);
				conn.setRequestMethod("GET");
				conn.setUseCaches(false);
				conn.setDefaultUseCaches(false);
				conn.setRequestProperty("User-Agent", "MCreator " + Launcher.version.getFullString());
				conn.connect();
				if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
					LOG.warn("GA track failed! Response code: " + conn.getResponseCode() + "/"
							+ conn.getResponseMessage());
				}
			} catch (Exception e) {
				LOG.warn("GA error: ", e);
			}
		}
	}

	void trackPage(String page) {
		LOG.info("Tracking page: " + page);

		// TODO: on page tracking, set also (and keep for events on that page):
		// dl - http://localhost/test2.html - document location
		// dt - Title - document title
		// _p - random page load hash

		processRequestURL(getGATrackURL(new HashMap<>()));
	}

	void trackEvent(String category, String action, String label, String value) {
		if (category == null || action == null)
			return;

		LOG.info("Tracking event: " + category + " - " + action);

		Map<String, Object> payload = new HashMap<>();

		// Events:
		// en - event name
		// ep.* - event parameter string
		// epn.* - event parameter number

		// TODO: fix those
		payload.put("ec", category);
		payload.put("ea", action);
		payload.put("el", label);
		payload.put("ev", value);
		processRequestURL(getGATrackURL(payload));
	}
}
