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

import net.mcreator.ui.MCreatorApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

class GoogleAnalytics {

	private static final Logger LOG = LogManager.getLogger("GA");

	private String userAgent;
	private String clientUUID;

	void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	void setClientUUID(String clientUUID) {
		this.clientUUID = clientUUID;
	}

	private String getGATrackURL(String hitType, Map<String, Object> payload) {
		StringBuilder actionRequestURL = new StringBuilder("https://www.google-analytics.com/collect?v=1");

		payload.put("aip", 1);
		payload.put("cid", clientUUID);
		payload.put("tid", "UA-27875746-8");
		payload.put("dh", "app.mcreator.net");
		payload.put("ua", userAgent);
		payload.put("t", hitType);

		for (Map.Entry<String, Object> entry : payload.entrySet()) {
			try {
				if (entry.getValue() != null)
					actionRequestURL.append("&").append(entry.getKey()).append("=")
							.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
			} catch (UnsupportedEncodingException ignored) {
			}
		}

		return actionRequestURL.toString();
	}

	private void processRequestURL(String requesturl) {
		if (MCreatorApplication.isInternet) {
			try {
				HttpURLConnection conn = (HttpURLConnection) new URL(requesturl).openConnection();
				conn.setInstanceFollowRedirects(true);
				conn.setRequestMethod("GET");
				conn.setUseCaches(false);
				conn.setDefaultUseCaches(false);
				conn.setRequestProperty("User-Agent", userAgent);
				conn.connect();
				if (conn.getResponseCode() != 200) {
					LOG.warn("GA track failed! Error" + conn.getResponseCode());
				}
			} catch (Exception e) {
				LOG.warn("GA error: " + e.getMessage());
			}
		}
	}

	void trackPageview(String page, Map<String, Object> payload) {
		LOG.info("Tracking page: " + page);

		payload.put("dp", "/" + page);
		processRequestURL(getGATrackURL("pageview", payload));
	}

	void trackEvent(String category, String action, String label, String value, Map<String, Object> payload) {
		if (category == null || action == null)
			return;

		LOG.info("Tracking event: " + category + " - " + action);

		payload.put("ec", category);
		payload.put("ea", action);
		payload.put("el", label);
		payload.put("ev", value);
		processRequestURL(getGATrackURL("event", payload));
	}
}
