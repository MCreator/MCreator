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

package net.mcreator.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import paulscode.sound.SoundSystem;
import paulscode.sound.SoundSystemConfig;
import paulscode.sound.SoundSystemException;
import paulscode.sound.codecs.CodecJOgg;
import paulscode.sound.libraries.LibraryJavaSound;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class SoundUtils {

	private static final Logger LOG = LogManager.getLogger(SoundUtils.class);

	private static SoundSystem soundSystem;

	private static final Set<String> activeSources = new HashSet<>();
	private static final ScheduledExecutorService soundPlayer = Executors.newScheduledThreadPool(1);

	public static void initSoundSystem() {
		try {
			SoundSystemConfig.setNumberStreamingChannels(1);
			SoundSystemConfig.addLibrary(LibraryJavaSound.class);
			SoundSystemConfig.setCodec("ogg", CodecJOgg.class);
			soundSystem = new SoundSystem();
		} catch (SoundSystemException e) {
			LOG.warn("Failed to initialize sound system", e);
		}
	}

	public static void playSound(File file) {
		if (soundSystem == null) {
			LOG.warn("Sound system not ready");
			return;
		}

		soundPlayer.execute(() -> {
			try {
				activeSources.forEach(soundSystem::stop);
				activeSources.clear();


				activeSources.add(soundSystem.quickStream(false, file.toURI().toURL(), file.getName(), false, 0, 0, 0,
						SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff()));
			} catch (Exception e) {
				LOG.warn("Failed to load sound file", e);
			}
		});

	}

	public static void stopAllSounds() {
		soundPlayer.execute(() -> {
			activeSources.forEach(soundSystem::stop);
			activeSources.clear();
		});
	}

}
