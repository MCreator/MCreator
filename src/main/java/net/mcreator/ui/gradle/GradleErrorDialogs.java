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

package net.mcreator.ui.gradle;

import net.mcreator.gradle.GradleErrorCodes;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.ui.MCreator;
import net.mcreator.ui.MCreatorApplication;
import net.mcreator.ui.action.impl.gradle.ClearAllGradleCachesAction;
import net.mcreator.ui.dialogs.preferences.PreferencesDialog;
import net.mcreator.ui.init.L10N;
import net.mcreator.util.DesktopUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;

public class GradleErrorDialogs {

	private static final Logger LOG = LogManager.getLogger("Gradle Error Dialogs");

	public static int showErrorDialog(int errorCode, MCreator whereToShow) {
		if (errorCode == GradleErrorCodes.JAVA_JVM_CRASH_ERROR)
			showJVMCrashErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_XMX_INVALID_VALUE)
			showXMXInvalidErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_XMS_INVALID_VALUE)
			showXMSInvalidErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.JAVA_JVM_HEAP_SPACE)
			showJVMHeapSpaceErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_NO_INTERNET)
			showNoInternetErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_INTERNET_INTERRUPTED)
			showInternetInterruptedErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_BUILD_FAILED)
			showGradleBuildFailedErrorDialog(whereToShow);
		else if (errorCode == GradleErrorCodes.GRADLE_REOBF_FAILED)
			showGradleReobfFailedErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_CACHEDATA_ERROR)
			showGradleCacheDataErrorDialog(whereToShow, errorCode);
		else if (errorCode == GradleErrorCodes.GRADLE_CACHEDATA_OUTDATED)
			showGradleCacheOutdatedDialogOfflineMode(whereToShow, errorCode);
		else
			LOG.warn("Error with code " + errorCode + " was reported, but no response is registered.");

		return errorCode;
	}

	private static String applyAppendx(String msg, int errorCode) {
		String appendx = "<small><br><br><font color=gray>" + L10N.t("gradle.errors.error") + GradleErrorCodes.toString(
				errorCode) + " [" + errorCode + "]";
		return msg + appendx;
	}

	private static void showGradleCacheDataErrorDialog(MCreator whereToShow, int errorCode) {
		if (PreferencesManager.PREFERENCES.gradle.offline) {
			showGradleCacheOutdatedDialogOfflineMode(whereToShow, errorCode);
		} else {
			String msg = L10N.t("gradle.errors.cache_corrupted");

			String[] options = { L10N.t("gradle.errors.clear_caches"), L10N.t("gradle.errors.do_nothing") };
			int option = JOptionPane.showOptionDialog(whereToShow, applyAppendx(msg, errorCode),
					L10N.t("gradle.errors.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
					options, options[0]);
			if (option == 0) {
				ClearAllGradleCachesAction.clearAllGradleCaches(whereToShow, false, false);
			}
		}
	}

	private static void showGradleCacheOutdatedDialogOfflineMode(Window whereToShow, int errorCode) {
		String msg = L10N.t("gradle.errors.cache_outdated");

		String[] options = { L10N.t("gradle.errors.open_options"), L10N.t("gradle.errors.do_nothing") };
		int option = JOptionPane.showOptionDialog(whereToShow, applyAppendx(msg, errorCode),
				L10N.t("gradle.errors.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
				options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showGradleReobfFailedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.reobf_failed"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showInternetInterruptedErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.internet_interrupted"), JOptionPane.ERROR_MESSAGE, null,
				errorCode);
	}

	private static void showNoInternetErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.no_internet"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMHeapSpaceErrorDialog(Window whereToShow, int errorCode) {
		String msg = L10N.t("gradle.errors.jvm_heap_space");

		String[] options = { L10N.t("gradle.errors.open_options"), L10N.t("gradle.errors.do_nothing") };
		int option = JOptionPane.showOptionDialog(whereToShow, applyAppendx(msg, errorCode),
				L10N.t("gradle.errors.title"), JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, options,
				options[0]);
		if (option == 0) {
			new PreferencesDialog(whereToShow, "Gradle settings");
		}
	}

	private static void showXMSInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.xms_invalid"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showXMXInvalidErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.xmx_invalid"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showJVMCrashErrorDialog(Window whereToShow, int errorCode) {
		showErrorDialog(whereToShow, L10N.t("gradle.errors.jvm_crashed"), JOptionPane.ERROR_MESSAGE, null, errorCode);
	}

	private static void showGradleBuildFailedErrorDialog(Window whereToShow) {
		Object[] options = { L10N.t("gradle.errors.open_help_page"), L10N.t("gradle.errors.do_nothing") };
		int reply = JOptionPane.showOptionDialog(whereToShow, L10N.t("gradle.errors.build_failed"),
				L10N.t("gradle.errors.build_failed.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
				null, options, options[0]);
		if (reply == 0) {
			DesktopUtils.browseSafe(MCreatorApplication.SERVER_DOMAIN + "/wiki/gradle-setup-errors");
		}
	}

	private static void showErrorDialog(Window window, String msg, int type, Icon icon, int errorCode) {
		JOptionPane.showMessageDialog(window, applyAppendx(msg, errorCode), L10N.t("gradle.errors.title"), type, icon);
	}

}
