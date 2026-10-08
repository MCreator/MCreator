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

package net.mcreator.io;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class WindowsDefenderUtil {

	private static final Logger LOG = LogManager.getLogger("Windows Defender");

	public enum ExclusionResult {
		ADDED, DECLINED, FAILED
	}

	private static final int EXIT_ADDED = 0;
	private static final int EXIT_ADD_FAILED = 2;
	private static final int EXIT_VERIFICATION_FAILED = 3;
	private static final int EXIT_DECLINED = 10;
	private static final int EXIT_LAUNCH_FAILED = 11;

	/**
	 * Checks if Windows Defender real-time protection is enabled. This does not require elevation.
	 *
	 * @return true if real-time protection is enabled, false if it is disabled, if another antivirus is active,
	 * if the status cannot be determined or if not running on Windows
	 */
	public static boolean isRealTimeProtectionEnabled() {
		if (OS.getOS() != OS.WINDOWS)
			return false;

		try {
			String output = runPowerShell("(Get-MpComputerStatus).RealTimeProtectionEnabled", Duration.ofSeconds(20));
			return output.lines().anyMatch(line -> line.trim().equalsIgnoreCase("True"));
		} catch (Exception e) {
			LOG.warn("Failed to check Windows Defender status", e);
			return false;
		}
	}

	/**
	 * Adds the given folders to Windows Defender exclusions. This shows the UAC prompt to the user. The exclusions
	 * are verified in the same elevated session, so ADDED is only returned if the folders are really excluded.
	 * Folders that are already excluded are left as they are.
	 *
	 * @param folders Folders to exclude from Windows Defender scanning
	 * @return Result of the operation, DECLINED if the user declined the UAC prompt
	 */
	public static ExclusionResult addFolderExclusions(List<File> folders) {
		if (OS.getOS() != OS.WINDOWS || folders.isEmpty())
			return ExclusionResult.FAILED;

		// Result is written to a file by the elevated process, as exit codes are not reliable across the
		// elevation boundary and the elevated process may run as a different (administrator) user
		File resultFile;
		try {
			resultFile = File.createTempFile("mcreator_defender_", ".result");
		} catch (IOException e) {
			LOG.warn("Failed to create Windows Defender result file", e);
			return ExclusionResult.FAILED;
		}

		String paths = folders.stream().map(folder -> quote(normalizePath(folder))).collect(Collectors.joining(","));

		String elevatedScript = """
				function Finish($code) { Set-Content -LiteralPath %s -Value $code; exit $code }
				$paths = @(%s)
				try { Add-MpPreference -ExclusionPath $paths -ErrorAction Stop } catch { Finish %d }
				$exclusions = (Get-MpPreference).ExclusionPath
				foreach ($path in $paths) { if (-not ($exclusions -contains $path)) { Finish %d } }
				Finish %d
				""".formatted(quote(normalizePath(resultFile)), paths, EXIT_ADD_FAILED, EXIT_VERIFICATION_FAILED,
				EXIT_ADDED);

		// Process.Start is used instead of Start-Process as Start-Process in PowerShell 5.1 drops the Win32 error
		// code of the failure, which we need to detect the declined UAC prompt (ERROR_CANCELLED, 1223).
		// The launcher reports failures through the result file too (code on the first line, error details after it)
		// so no output pipes are needed that could fill up and block the process.
		String launcherScript = """
				$startInfo = New-Object System.Diagnostics.ProcessStartInfo
				$startInfo.FileName = %s
				$startInfo.Arguments = '-NoProfile -NonInteractive -ExecutionPolicy Bypass -EncodedCommand %s'
				$startInfo.Verb = 'runas'
				$startInfo.UseShellExecute = $true
				$startInfo.WindowStyle = [System.Diagnostics.ProcessWindowStyle]::Hidden
				try {
				    $process = [System.Diagnostics.Process]::Start($startInfo)
				    if ($process) { $process.WaitForExit() }
				    exit 0
				} catch {
				    $exception = $_.Exception
				    while ($exception) {
				        if ($exception -is [System.ComponentModel.Win32Exception] -and $exception.NativeErrorCode -eq 1223) {
				            Set-Content -LiteralPath %s -Value %d
				            exit %d
				        }
				        $exception = $exception.InnerException
				    }
				    Set-Content -LiteralPath %s -Value (@('%d', "$_") -join "`n")
				    exit %d
				}
				""".formatted(quote(getPowerShellPath()), encodeCommand(elevatedScript),
				quote(normalizePath(resultFile)), EXIT_DECLINED, EXIT_DECLINED, quote(normalizePath(resultFile)),
				EXIT_LAUNCH_FAILED, EXIT_LAUNCH_FAILED);

		try {
			ProcessBuilder processBuilder = new ProcessBuilder(getPowerShellPath(), "-NoProfile", "-NonInteractive",
					"-ExecutionPolicy", "Bypass", "-EncodedCommand", encodeCommand(launcherScript));
			processBuilder.redirectOutput(ProcessBuilder.Redirect.DISCARD);
			processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
			Process process = processBuilder.start();

			// the elevated process waits for the user to respond to the UAC prompt, so this can take a while
			if (!process.waitFor(15, TimeUnit.MINUTES)) {
				process.destroyForcibly();
				LOG.warn("Timed out waiting for Windows Defender exclusions to be added, treating as declined");
				return ExclusionResult.DECLINED; // no decision was made, so the user should be asked again
			}

			String[] result = FileIO.readFileToString(resultFile).trim().split("\n", 2);
			int code;
			try {
				code = Integer.parseInt(result[0].trim());
			} catch (NumberFormatException e) {
				LOG.warn("Failed to add Windows Defender exclusions, no result reported, launcher exit code: {}",
						process.exitValue());
				return ExclusionResult.FAILED;
			}

			switch (code) {
			case EXIT_ADDED -> {
				LOG.info("Windows Defender exclusions added for {}", folders);
				return ExclusionResult.ADDED;
			}
			case EXIT_DECLINED -> {
				LOG.info("User declined adding Windows Defender exclusions");
				return ExclusionResult.DECLINED;
			}
			default -> LOG.warn("Failed to add Windows Defender exclusions, result code: {}, details: {}", code,
					result.length > 1 ? result[1].trim() : "");
			}
		} catch (Exception e) {
			LOG.warn("Failed to add Windows Defender exclusions", e);
		} finally {
			resultFile.delete();
		}

		return ExclusionResult.FAILED;
	}

	private static String runPowerShell(String command, Duration timeout) throws Exception {
		ProcessBuilder processBuilder = new ProcessBuilder(getPowerShellPath(), "-NoProfile", "-NonInteractive",
				"-ExecutionPolicy", "Bypass", "-EncodedCommand", encodeCommand(command));
		// PowerShell writes progress records to stderr when it is redirected, so we only read stdout
		processBuilder.redirectError(ProcessBuilder.Redirect.DISCARD);
		Process process = processBuilder.start();

		if (!process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS)) {
			process.destroyForcibly();
			throw new RuntimeException("Timed out running PowerShell command: " + command);
		}

		return new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
	}

	private static String getPowerShellPath() {
		String systemRoot = System.getenv("SystemRoot");
		if (systemRoot != null) {
			File powerShell = new File(systemRoot, "System32/WindowsPowerShell/v1.0/powershell.exe");
			if (powerShell.isFile())
				return powerShell.getAbsolutePath();
		}
		return "powershell.exe";
	}

	private static String encodeCommand(String script) {
		return Base64.getEncoder().encodeToString(script.getBytes(StandardCharsets.UTF_16LE));
	}

	private static String normalizePath(File file) {
		return file.toPath().toAbsolutePath().normalize().toString();
	}

	private static String quote(String value) {
		return "'" + value.replace("'", "''") + "'";
	}

}
