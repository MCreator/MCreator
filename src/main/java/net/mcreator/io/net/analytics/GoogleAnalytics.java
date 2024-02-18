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

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GoogleAnalytics {

	public static boolean ANALYTICS_ENABLED = true;

	private static final Logger LOG = LogManager.getLogger("GA4");

	private static final String DH = "app.mcreator.net";
	private static final String BASE_URL = "https://" + DH;

	private final String clientUUID;
	private final DeviceInfo deviceInfo;

	private final Random random;

	// Session info
	private final long sessionID;
	private boolean newSession = true;

	// Page info
	private String currentPageHash = "";
	private String currentPage = "";
	private String previousPage = "";

	private final ExecutorService requestExecutor = Executors.newSingleThreadExecutor();

	public GoogleAnalytics(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
		this.sessionID = System.currentTimeMillis();

		this.random = new Random();

		this.clientUUID = (random.nextInt() & Integer.MAX_VALUE) + "." + sessionID;
	}

	private String getGATrackURL(Map<String, Object> payload) {
		StringBuilder actionRequestURL = new StringBuilder("https://www.google-analytics.com/g/collect?v=2");

		// Thanks to https://www.thyngster.com/ga4-measurement-protocol-cheatsheet/
		payload.put("tid", "G-V6EPB4SPL8");
		payload.put("_z", "ccd.ABC");
		payload.put("_p", currentPageHash); // page hash
		payload.put("cid", clientUUID);
		payload.put("ul", L10N.getLocaleString().toLowerCase(Locale.ENGLISH).replace("_", "-"));
		payload.put("sr", deviceInfo.getScreenWidth() + "x" + deviceInfo.getScreenHeight());
		payload.put("uaw", deviceInfo.getScreenWidth());
		payload.put("dh", DH);
		payload.put("sid", sessionID);
		payload.put("_nsi", newSession ? 1 : 0); // new session ID
		payload.put("_ss", newSession ? 1 : 0); // session start
		payload.put("_s", 1); // hit counter
		payload.put("_et", 1); // engagement time, fixed at 1ms
		payload.put("dl", BASE_URL + currentPage); // document location
		payload.put("dr", BASE_URL + previousPage); // document referrer
		payload.put("uafvl", Launcher.version.getFullString()); // user agent full version list
		payload.put("uam", Launcher.version.getMajorString()); // user agent model
		payload.put("uap", System.getProperty("os.name")); // user agent platform
		payload.put("uaa", System.getProperty("os.arch")); // user agent architecture

		for (Map.Entry<String, Object> entry : payload.entrySet()) {
			if (entry.getValue() != null)
				actionRequestURL.append("&").append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
		}

		// Once session is started, it is not new anymore
		newSession = false;

		return actionRequestURL.toString();
	}

	public void trackPageSync(String page) {
		try {
			currentPageHash = String.valueOf(random.nextInt() & Integer.MAX_VALUE);
			previousPage = currentPage;
			currentPage = page;

			Map<String, Object> payload = new LinkedHashMap<>();
			payload.put("en", "page_view");

			processRequestURL(getGATrackURL(payload));

			LOG.info("Tracked page: " + page);
		} catch (Exception e) {
			LOG.warn("Failed to track page: " + page, e);
		}
	}

	private void trackEventSync(String name, String context) {
		try {
			Map<String, Object> payload = new LinkedHashMap<>();

			payload.put("en", name);
			payload.put("ep.ctx", context);
			processRequestURL(getGATrackURL(payload));

			LOG.info("Tracked event: " + name + ", context: " + context);
		} catch (Exception e) {
			LOG.warn("Failed to track event: " + name + ", context: " + context, e);
		}
	}

	public void trackPage(String page) {
		requestExecutor.submit(() -> trackPageSync(page));
	}

	public void trackEvent(String name, String context) {
		requestExecutor.submit(() -> trackEventSync(name, context));
	}

	private void processRequestURL(String requesturl) throws IOException {
		if (MCreatorApplication.isInternet && ANALYTICS_ENABLED && !Launcher.version.isDevelopment()) {
			HttpURLConnection conn = (HttpURLConnection) new URL(requesturl).openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setUseCaches(false);
			conn.setDefaultUseCaches(false);
			conn.setRequestProperty("User-Agent",
					"MCreator/" + Launcher.version.getFullString() + " (" + System.getProperty("os.name") + "; "
							+ System.getProperty("os.arch") + ")");

			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			OutputStream os = conn.getOutputStream();
			os.flush();
			os.close();

			conn.connect();
			if (conn.getResponseCode() != 200 && conn.getResponseCode() != 204) {
				throw new IOException(
						"GA track failed! Response code: " + conn.getResponseCode() + "/" + conn.getResponseMessage());
			}
		}
	}

}
