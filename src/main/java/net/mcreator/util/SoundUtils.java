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
import paulscode.sound.codecs.CodecJOrbis;
import paulscode.sound.libraries.LibraryJavaSound;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SoundUtils {

	private static final Logger LOG = LogManager.getLogger(SoundUtils.class);

	private static final String sourceName = "SOURCE";

	private static SoundSystem soundSystem;
	private static ExecutorService soundSystemThread;

	public static void initSoundSystem() {
		soundSystemThread = Executors.newSingleThreadExecutor(r -> new Thread(r, "Sound system"));
		soundSystemThread.execute(() -> {
			try {
				SoundSystemConfig.setNumberStreamingChannels(1);
				SoundSystemConfig.addLibrary(LibraryJavaSound.class);
				SoundSystemConfig.setCodec("ogg", CodecJOrbis.class);
				soundSystem = new SoundSystem();
			} catch (Exception e) {
				LOG.warn("Failed to initialize sound system", e);
			}
		});
	}

	public static void playSound(File file) {
		if (soundSystem == null) {
			LOG.warn("Sound system not ready");
			return;
		}

		soundSystemThread.execute(() -> {
			try {
				soundSystem.newSource(false, sourceName, file.toURI().toURL(), file.getName(), false, 0, 0, 0,
						SoundSystemConfig.ATTENUATION_NONE, SoundSystemConfig.getDefaultRolloff());
				soundSystem.play(sourceName);
			} catch (Exception e) {
				LOG.warn("Failed to load sound file", e);
			}
		});
	}

	public static void stopAllSounds() {
		if (soundSystem == null) {
			LOG.warn("Sound system not ready");
			return;
		}

		try {
			soundSystem.stop(sourceName);
			soundSystem.removeSource(sourceName);
		} catch (Exception e) {
			LOG.warn("Failed to stop sound all sounds", e);
		}
	}

	public static void close() {
		try {
			if (soundSystem != null) {
				soundSystemThread.execute(() -> {
					soundSystem.cleanup();
					soundSystem = null;
				});

				soundSystemThread.shutdown();
				soundSystemThread.awaitTermination(3, TimeUnit.SECONDS);
			}
		} catch (Exception e) {
			LOG.warn("Failed to stop sound system", e);
		}
	}

}
