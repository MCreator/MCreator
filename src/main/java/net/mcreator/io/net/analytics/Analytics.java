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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class Analytics {

	private final GoogleAnalytics ga;
	private final DeviceInfo deviceInfo;

	public Analytics(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
		ga = new GoogleAnalytics();
		ga.setUserAgent(
				"MCreator " + Launcher.version.getFullString() + " / " + deviceInfo.getOsName() + " / " + deviceInfo
						.getJvmVersion());
		ga.setClientUUID(UUID.randomUUID().toString());
	}

	public Analytics trackMCreatorLaunch() {
		trackPageViewImpl(AnalyticsConstants.PAGE_LAUNCH, new HashMap<String, Object>() {{
			put("sc", "start");
		}});
		return this;
	}

	public Analytics trackMCreatorClose() {
		trackPageViewImpl(AnalyticsConstants.PAGE_CLOSE, new HashMap<String, Object>() {{
			put("sc", "end");
		}});
		return this;
	}

	public Analytics trackEvent(String category, String action, String label, String value) {
		Map<String, Object> payload = new HashMap<>();
		addDeviceInfoPayload(payload);
		ga.trackEvent(category, action, label, value, payload);
		return this;
	}

	public void async(Supplier<Analytics> run) {
		new Thread(run::get).start();
	}

	private void trackPageViewImpl(String page, Map<String, Object> payload) {
		addDeviceInfoPayload(payload);
		ga.trackPageview("/" + Launcher.version.major + "/" + page, payload);
	}

	private void addDeviceInfoPayload(Map<String, Object> payload) {
		payload.put("sr", deviceInfo.getScreenWidth() + "x" + deviceInfo.getScreenHeight());
		payload.put("cm1", deviceInfo.getSystemBits());
		payload.put("cm2", deviceInfo.getRamAmountMB());
		payload.put("cd1", deviceInfo.getOsName());
		payload.put("cd2", deviceInfo.getJvmVersion());
		payload.put("cd3", Launcher.version.getFullString());
	}

}