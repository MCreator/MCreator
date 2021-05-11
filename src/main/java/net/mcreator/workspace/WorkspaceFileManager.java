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

package net.mcreator.workspace;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mcreator.io.FileIO;
import net.mcreator.preferences.PreferencesManager;
import net.mcreator.workspace.elements.ModElementManager;
import net.mcreator.workspace.elements.SoundElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.annotation.Nonnull;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WorkspaceFileManager implements Closeable {

	private final Logger LOG;

	public static final Gson gson = new GsonBuilder().setLenient().setPrettyPrinting()
			.registerTypeAdapter(SoundElement.class, new SoundElement.SoundElementDeserializer()).create();

	private DataSavedListener dataSavedListener;

	private final ScheduledExecutorService dataSaveExecutor = Executors.newScheduledThreadPool(1);
	private ScheduledFuture<?> lastSchedule;

	private final File workspaceFile;
	private final Workspace workspace;
	private final WorkspaceFolderManager folderManager;
	private final ModElementManager modElementManager;

	WorkspaceFileManager(@Nonnull File workspaceFile, @Nonnull Workspace workspace) {
		this.workspaceFile = workspaceFile;
		this.workspace = workspace;

		this.LOG = LogManager.getLogger("Workspace File Manager/" + workspace.toString().toUpperCase());

		this.folderManager = new WorkspaceFolderManager(workspaceFile, workspace);
		this.modElementManager = new ModElementManager(workspace);

		// start autosave scheduler
		lastSchedule = dataSaveExecutor
				.schedule(new SaveTask(this), PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval,
						TimeUnit.SECONDS);
	}

	public File getWorkspaceFile() {
		return workspaceFile;
	}

	WorkspaceFolderManager getFolderManager() {
		return folderManager;
	}

	public ModElementManager getModElementManager() {
		return modElementManager;
	}

	@Override public void close() {
		saveWorkspaceDirectlyAndWait(); // save workspace to FS
		lastSchedule.cancel(true); // we stop autosaving for this workspace after it is done
	}

	public void saveWorkspaceDirectlyAndWait() {
		LOG.info("Saving the workspace by direct request!");

		// set changed flag so the saving happens in all cases (this is a direct save request)
		workspace.markDirty();

		// we need to cancel currently scheduled save, so that another save in scheduler thread
		// does not happen while we are saving in this method
		if (lastSchedule != null)
			lastSchedule.cancel(true); // we allow interruption of existing task as we will save all again nonetheless

		new SaveTask(this).run(); // this run will save the workspace and schedule new save task too
	}

	private void saveWorkspaceIfChanged() {
		if (!workspace.isDirty()) // if the workspace file was not changed, we do not perform save
			return;

		String workspacestring = gson.toJson(workspace);
		if (workspacestring != null && !workspacestring.equals("")) {
			// first we backup workspace file
			rotateWorkspaceFileBackup();

			// We do an "atomic" write to the FS
			File outFile = workspaceFile;
			File tmpFile = new File(folderManager.getWorkspaceFolder(), workspaceFile.getName() + ".lock");
			FileIO.writeStringToFile(workspacestring, tmpFile);
			try {
				Files.move(tmpFile.toPath(), outFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
			} catch (Exception e) {
				LOG.info("Failed to do atomic move, trying normal move!");
				try {
					Files.move(tmpFile.toPath(), outFile.toPath(), StandardCopyOption.REPLACE_EXISTING,
							StandardCopyOption.COPY_ATTRIBUTES);
				} catch (IOException e1) {
					LOG.error(e1.getMessage(), e1);
					LOG.error("Falling back to normal write (non atomic, without move!)");
					FileIO.writeStringToFile(workspacestring, outFile);
				}
			}

			workspace.markClean(); // once the data is saved,
			// we mark workspace as not changed as the current version is on the FS

			if (dataSavedListener != null)
				dataSavedListener.dataSaved();

			LOG.debug("Workspace stored on the FS");
		} else {
			LOG.error("Skipping workspace save. Workspace is defined but we failed to serialize it!");
		}
	}

	private void rotateWorkspaceFileBackup() {
		int numberOfBackupsExcludingCurrent = PreferencesManager.PREFERENCES.backups.numberOfBackupsToStore - 1;
		File[] existingBackupsArray = folderManager.getWorkspaceBackupsCacheDir().listFiles();

		if (existingBackupsArray != null && existingBackupsArray.length > 0) { // we already have some backups
			List<File> existingBackups = new ArrayList<>(Arrays.asList(existingBackupsArray));
			existingBackups.sort(Comparator.comparingLong(File::lastModified));
			Collections.reverse(existingBackups);
			long lastBackupTime = existingBackups.get(0).lastModified();
			if ((System.currentTimeMillis() - lastBackupTime) / (1000 * 60)
					> PreferencesManager.PREFERENCES.backups.automatedBackupInterval) {  // check if we have surpassed backup interval
				if (existingBackupsArray.length
						> numberOfBackupsExcludingCurrent) // only delete old ones if we have more than threshold of backups
					existingBackups.stream().skip(numberOfBackupsExcludingCurrent).forEach(File::delete);
				createNewWorkspaceFileBackup();
			}
		} else { // we don't have any backup yet
			createNewWorkspaceFileBackup();
		}
	}

	private void createNewWorkspaceFileBackup() {
		// if workspace file exists so we can back it up, we backup it
		if (workspaceFile.isFile()) {
			File backupFile = new File(folderManager.getWorkspaceBackupsCacheDir(),
					workspaceFile.getName() + "-backup_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()));
			FileIO.copyFile(workspaceFile, backupFile);
		}
	}

	public void setDataSavedListener(DataSavedListener listener) {
		dataSavedListener = listener;
	}

	public interface DataSavedListener {
		void dataSaved();
	}

	private static class SaveTask implements Runnable {

		private final WorkspaceFileManager fileManager;

		SaveTask(WorkspaceFileManager fileManager) {
			this.fileManager = fileManager;
		}

		@Override public void run() {
			fileManager.saveWorkspaceIfChanged();

			// after we call save, we schedule a new call
			fileManager.lastSchedule = fileManager.dataSaveExecutor.schedule(new SaveTask(fileManager),
					PreferencesManager.PREFERENCES.backups.workspaceAutosaveInterval, TimeUnit.SECONDS);
		}
	}

}
